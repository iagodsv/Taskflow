package pt.com.LBC.Vacation_System.service;

import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.com.LBC.Vacation_System.dto.VacationDTO;
import pt.com.LBC.Vacation_System.exception.BusinessException;
import pt.com.LBC.Vacation_System.exception.ForbiddenException;
import pt.com.LBC.Vacation_System.exception.NotFoundException;
import pt.com.LBC.Vacation_System.model.Role;
import pt.com.LBC.Vacation_System.model.Settings;
import pt.com.LBC.Vacation_System.model.User;
import pt.com.LBC.Vacation_System.model.VacationRequest;
import pt.com.LBC.Vacation_System.model.VacationStatus;
import pt.com.LBC.Vacation_System.repository.SettingsRepository;
import pt.com.LBC.Vacation_System.repository.UserRepository;
import pt.com.LBC.Vacation_System.repository.VacationRepository;

@Service
public class VacationServiceImpl implements VacationService {

  private final VacationRepository vacationRepository;
  private final UserRepository userRepository;
  private final SettingsRepository settingsRepository;

  public VacationServiceImpl(VacationRepository vacationRepository, UserRepository userRepository,
      SettingsRepository settingsRepository) {
    this.vacationRepository = vacationRepository;
    this.userRepository = userRepository;
    this.settingsRepository = settingsRepository;
  }

  @Override
  public List<VacationRequest> list(User user) {
    if (user.getRole() == Role.ADMIN) {
      return vacationRepository.findAll();
    } else if (user.getRole() == Role.MANAGER) {
      return vacationRepository.findAll().stream()
          .filter(v -> v.getCollaborator().getManager() != null
              && v.getCollaborator().getManager().getId().equals(user.getId()))
          .toList();
    } else {
      return vacationRepository.findAll().stream()
          .filter(v -> v.getCollaborator().getId().equals(user.getId()))
          .toList();
    }
  }

  @Override
  @Transactional
  public VacationRequest create(User user, VacationDTO dto) {
    // Seleciona o colaborador alvo: padrão é o próprio usuário
    User target = user;
    if (dto.getCollaboratorId() != null) {
      target = userRepository.findById(dto.getCollaboratorId())
          .orElseThrow(() -> new NotFoundException("Colaborador não encontrado"));
      // Permissões: ADMIN pode criar para qualquer; MANAGER apenas para seus
      // colaboradores
      if (user.getRole() == Role.MANAGER) {
        if (target.getManager() == null || !target.getManager().getId().equals(user.getId())) {
          throw new ForbiddenException("Sem permissão para criar férias para este colaborador");
        }
      } else if (user.getRole() == Role.COLLABORATOR && !target.getId().equals(user.getId())) {
        throw new ForbiddenException("Sem permissão para criar férias para outro colaborador");
      }
    }

    // validação de limite máximo de dias por período (configuração)
    Settings settings = settingsRepository.findAll().stream().findFirst().orElse(null);
    if (settings != null && settings.getMaxPeriodDays() > 0) {
      long days = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1; // inclusivo
      if (days > settings.getMaxPeriodDays()) {
        throw new BusinessException("Período excede o limite máximo de " + settings.getMaxPeriodDays() + " dias.");
      }
    }

    if (vacationRepository.existsOverlappingForUser(dto.getStartDate(), dto.getEndDate(), target.getId())) {
      throw new BusinessException("Já existe férias aprovadas para este período.");
    }

    // validação de conflito global: qualquer pessoa já tem um pedido
    // (pendente/aprovado)
    if (vacationRepository.existsAnyOverlappingNotRejected(dto.getStartDate(), dto.getEndDate())) {
      throw new BusinessException("Já existe alguém de férias neste período.");
    }
    VacationRequest request = new VacationRequest();
    request.setCollaborator(target);
    request.setStartDate(dto.getStartDate());
    request.setEndDate(dto.getEndDate());
    request.setStatus(VacationStatus.PENDING);
    return vacationRepository.save(request);
  }

  @Override
  @Transactional
  public void approve(Long id, User user) {
    VacationRequest request = vacationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Pedido não encontrado"));
    if (request.getStatus() != VacationStatus.PENDING) {
      throw new BusinessException("Pedido já processado");
    }
    if (user.getRole() == Role.ADMIN ||
        (user.getRole() == Role.MANAGER && request.getCollaborator().getManager() != null
            && request.getCollaborator().getManager().getId().equals(user.getId()))) {
      if (vacationRepository.existsOverlappingForUser(request.getStartDate(), request.getEndDate(),
          request.getCollaborator().getId())) {
        throw new BusinessException("Já existe férias aprovadas para este período.");
      }
      request.setStatus(VacationStatus.APPROVED);
      vacationRepository.save(request);
    } else {
      throw new ForbiddenException("Sem permissão para aprovar este pedido");
    }
  }

  @Override
  @Transactional
  public void reject(Long id, User user) {
    VacationRequest request = vacationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Pedido não encontrado"));
    if (request.getStatus() != VacationStatus.PENDING) {
      throw new BusinessException("Pedido já processado");
    }
    if (user.getRole() == Role.ADMIN ||
        (user.getRole() == Role.MANAGER && request.getCollaborator().getManager() != null
            && request.getCollaborator().getManager().getId().equals(user.getId()))) {
      request.setStatus(VacationStatus.REJECTED);
      vacationRepository.save(request);
    } else {
      throw new ForbiddenException("Sem permissão para rejeitar este pedido");
    }
  }
}
