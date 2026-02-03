package pt.com.LBC.Vacation_System.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import pt.com.LBC.Vacation_System.dto.CreateUserRequest;
import pt.com.LBC.Vacation_System.dto.UpdateUserRequest;
import pt.com.LBC.Vacation_System.dto.UserResponseDTO;
import pt.com.LBC.Vacation_System.model.Role;
import pt.com.LBC.Vacation_System.model.User;
import pt.com.LBC.Vacation_System.repository.UserRepository;
import pt.com.LBC.Vacation_System.repository.VacationRepository;

@SpringBootTest
@Transactional
class UserServiceImplTest {

  @Autowired
  private UserService userService;
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
    // Limpa primeiro dependências para evitar violação de FK
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
    collaboratorOther = userRepository.save(collaboratorOther);
  }

  @Test
  void onlyAdminCreatesUsers() {
    CreateUserRequest request = new CreateUserRequest();
    request.setName("NewUser");
    request.setEmail("new@example.com");
    request.setRole("COLLABORATOR");

    UserResponseDTO created = userService.create(admin, request);
    assertNotNull(created.getId());

    assertThrows(RuntimeException.class, () -> userService.create(manager, request));
    assertThrows(RuntimeException.class, () -> userService.create(collaboratorManaged, request));
  }

  @Test
  void listByRole() {
    List<UserResponseDTO> adminList = userService.list(admin);
    assertTrue(adminList.size() >= 4);

    List<UserResponseDTO> managerList = userService.list(manager);
    assertTrue(
        managerList.stream().allMatch(u -> u.getManagerId() != null && u.getManagerId().equals(manager.getId())));

    List<UserResponseDTO> collabList = userService.list(collaboratorManaged);
    assertEquals(1, collabList.size());
    assertEquals(collaboratorManaged.getId(), collabList.get(0).getId());
  }

  @Test
  void updateDeleteOnlyByAdmin() {
    UpdateUserRequest updated = new UpdateUserRequest();
    updated.setName("Updated");
    updated.setEmail("a@example.com");
    updated.setRole("COLLABORATOR");

    // Admin pode
    var res = userService.update(admin, collaboratorManaged.getId(), updated);
    assertEquals("Updated", res.getName());

    // outros não
    assertThrows(RuntimeException.class, () -> userService.update(manager, collaboratorManaged.getId(), updated));
    assertThrows(RuntimeException.class,
        () -> userService.update(collaboratorManaged, collaboratorManaged.getId(), updated));

    // delete
    assertDoesNotThrow(() -> userService.delete(admin, collaboratorOther.getId()));
    // outros não
    assertThrows(RuntimeException.class, () -> userService.delete(manager, collaboratorManaged.getId()));
  }
}
