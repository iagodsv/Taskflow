package pt.com.LBC.Vacation_System.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import pt.com.LBC.Vacation_System.dto.VacationDTO;
import pt.com.LBC.Vacation_System.model.Role;
import pt.com.LBC.Vacation_System.model.User;
import pt.com.LBC.Vacation_System.model.VacationRequest;
import pt.com.LBC.Vacation_System.model.VacationStatus;
import pt.com.LBC.Vacation_System.repository.UserRepository;
import pt.com.LBC.Vacation_System.repository.VacationRepository;

@SpringBootTest
@Transactional
class VacationServiceImplTest {

  @Autowired
  private VacationService vacationService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private VacationRepository vacationRepository;

  private User admin;
  private User manager;
  private User collaboratorManaged;
  private User collaboratorOther;

  @BeforeEach
  void setup() {
    vacationRepository.deleteAll();
    userRepository.deleteAll();

    admin = new User();
    admin.setName("Admin");
    admin.setEmail("admin@example.com");
    admin.setRole(Role.ADMIN);
    admin = userRepository.save(admin);

    manager = new User();
    manager.setName("Manager");
    manager.setEmail("manager@example.com");
    manager.setRole(Role.MANAGER);
    manager = userRepository.save(manager);

    collaboratorManaged = new User();
    collaboratorManaged.setName("Colab A");
    collaboratorManaged.setEmail("a@example.com");
    collaboratorManaged.setRole(Role.COLLABORATOR);
    collaboratorManaged.setManager(manager);
    collaboratorManaged = userRepository.save(collaboratorManaged);

    collaboratorOther = new User();
    collaboratorOther.setName("Colab B");
    collaboratorOther.setEmail("b@example.com");
    collaboratorOther.setRole(Role.COLLABORATOR);
    // sem manager ou outro manager
    collaboratorOther = userRepository.save(collaboratorOther);
  }

  @Test
  void adminCanCreateForAnyCollaborator() {
    VacationDTO dto = new VacationDTO();
    dto.setStartDate(LocalDate.now());
    dto.setEndDate(LocalDate.now().plusDays(5));
    dto.setCollaboratorId(collaboratorOther.getId());
    VacationRequest req = vacationService.create(admin, dto);
    assertNotNull(req.getId());
    assertEquals(VacationStatus.PENDING, req.getStatus());
    assertEquals(collaboratorOther.getId(), req.getCollaborator().getId());
  }

  @Test
  void managerCanCreateOnlyForManagedCollaborators() {
    VacationDTO dtoManaged = new VacationDTO();
    dtoManaged.setStartDate(LocalDate.now());
    dtoManaged.setEndDate(LocalDate.now().plusDays(3));
    dtoManaged.setCollaboratorId(collaboratorManaged.getId());
    VacationRequest ok = vacationService.create(manager, dtoManaged);
    assertNotNull(ok.getId());

    VacationDTO dtoOther = new VacationDTO();
    dtoOther.setStartDate(LocalDate.now());
    dtoOther.setEndDate(LocalDate.now().plusDays(2));
    dtoOther.setCollaboratorId(collaboratorOther.getId());
    assertThrows(RuntimeException.class, () -> vacationService.create(manager, dtoOther));
  }

  @Test
  void collaboratorCanCreateOnlyForSelf() {
    VacationDTO dtoSelf = new VacationDTO();
    dtoSelf.setStartDate(LocalDate.now());
    dtoSelf.setEndDate(LocalDate.now().plusDays(2));
    VacationRequest created = vacationService.create(collaboratorManaged, dtoSelf);
    assertEquals(collaboratorManaged.getId(), created.getCollaborator().getId());

    VacationDTO dtoOther = new VacationDTO();
    dtoOther.setStartDate(LocalDate.now());
    dtoOther.setEndDate(LocalDate.now().plusDays(2));
    dtoOther.setCollaboratorId(collaboratorOther.getId());
    assertThrows(RuntimeException.class, () -> vacationService.create(collaboratorManaged, dtoOther));
  }

  @Test
  void cannotCreateWhenOverlapsApprovedForSameUser() {
    // Cria um pedido aprovado para collaboratorManaged
    VacationRequest approved = new VacationRequest();
    approved.setCollaborator(collaboratorManaged);
    approved.setStartDate(LocalDate.of(2026, 8, 1));
    approved.setEndDate(LocalDate.of(2026, 8, 5));
    approved.setStatus(VacationStatus.APPROVED);
    vacationRepository.save(approved);

    VacationDTO dto = new VacationDTO();
    dto.setCollaboratorId(collaboratorManaged.getId());
    dto.setStartDate(LocalDate.of(2026, 8, 4));
    dto.setEndDate(LocalDate.of(2026, 8, 6));
    assertThrows(RuntimeException.class, () -> vacationService.create(admin, dto));
  }

  @Test
  void approveOnlyByAdminOrManagerOfCollaborator() {
    // cria pendente do collaboratorManaged
    VacationDTO dto = new VacationDTO();
    dto.setCollaboratorId(collaboratorManaged.getId());
    dto.setStartDate(LocalDate.now());
    dto.setEndDate(LocalDate.now().plusDays(3));
    VacationRequest req = vacationService.create(admin, dto);

    // manager do colaborador aprova
    vacationService.approve(req.getId(), manager);
    VacationRequest saved = vacationRepository.findById(req.getId()).orElseThrow();
    assertEquals(VacationStatus.APPROVED, saved.getStatus());

    // cria outro pendente do collaboratorOther
    VacationDTO dto2 = new VacationDTO();
    dto2.setCollaboratorId(collaboratorOther.getId());
    // Define datas que não sobrepõem o primeiro período
    dto2.setStartDate(LocalDate.now().plusDays(4));
    dto2.setEndDate(LocalDate.now().plusDays(6));
    VacationRequest req2 = vacationService.create(admin, dto2);

    // manager não consegue aprovar colaborador que não gerencia
    assertThrows(RuntimeException.class, () -> vacationService.approve(req2.getId(), manager));

    // admin consegue
    vacationService.approve(req2.getId(), admin);
    assertEquals(VacationStatus.APPROVED, vacationRepository.findById(req2.getId()).orElseThrow().getStatus());
  }

  @Test
  void rejectOnlyByAdminOrManagerOfCollaborator() {
    VacationDTO dto = new VacationDTO();
    dto.setCollaboratorId(collaboratorManaged.getId());
    dto.setStartDate(LocalDate.now());
    dto.setEndDate(LocalDate.now().plusDays(1));
    VacationRequest req = vacationService.create(admin, dto);

    // manager do colaborador rejeita
    vacationService.reject(req.getId(), manager);
    VacationRequest saved = vacationRepository.findById(req.getId()).orElseThrow();
    assertEquals(VacationStatus.REJECTED, saved.getStatus());

    // outro caso: colaboradorOther pendente
    VacationDTO dto2 = new VacationDTO();
    dto2.setCollaboratorId(collaboratorOther.getId());
    dto2.setStartDate(LocalDate.now());
    dto2.setEndDate(LocalDate.now().plusDays(1));
    VacationRequest req2 = vacationService.create(admin, dto2);
    assertThrows(RuntimeException.class, () -> vacationService.reject(req2.getId(), manager));
  }

  @Test
  void listByRoleBehavior() {
    // cria 2 pedidos: um de managed, um de other
    VacationRequest r1 = new VacationRequest();
    r1.setCollaborator(collaboratorManaged);
    r1.setStartDate(LocalDate.now());
    r1.setEndDate(LocalDate.now().plusDays(1));
    r1.setStatus(VacationStatus.PENDING);
    vacationRepository.save(r1);

    VacationRequest r2 = new VacationRequest();
    r2.setCollaborator(collaboratorOther);
    r2.setStartDate(LocalDate.now());
    r2.setEndDate(LocalDate.now().plusDays(2));
    r2.setStatus(VacationStatus.PENDING);
    vacationRepository.save(r2);

    List<VacationRequest> adminList = vacationService.list(admin);
    assertEquals(2, adminList.size());

    List<VacationRequest> managerList = vacationService.list(manager);
    assertEquals(1, managerList.size());
    assertEquals(collaboratorManaged.getId(), managerList.get(0).getCollaborator().getId());

    List<VacationRequest> collabList = vacationService.list(collaboratorManaged);
    assertEquals(1, collabList.size());
    assertEquals(collaboratorManaged.getId(), collabList.get(0).getCollaborator().getId());
  }
}
