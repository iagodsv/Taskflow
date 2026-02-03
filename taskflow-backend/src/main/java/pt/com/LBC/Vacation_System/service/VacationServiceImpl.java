package pt.com.LBC.Vacation_System.service;

import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.com.LBC.Vacation_System.dto.VacationDTO;
import pt.com.LBC.Vacation_System.dto.VacationResponseDTO;
import pt.com.LBC.Vacation_System.exception.BusinessException;
import pt.com.LBC.Vacation_System.exception.ForbiddenException;
import pt.com.LBC.Vacation_System.exception.NotFoundException;
import pt.com.LBC.Vacation_System.mapper.VacationMapper;
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
  private final VacationMapper vacationMapper;

  public VacationServiceImpl(VacationRepository vacationRepository, UserRepository userRepository,
      SettingsRepository settingsRepository, VacationMapper vacationMapper) {
    this.vacationRepository = vacationRepository;
    this.userRepository = userRepository;
    this.settingsRepository = settingsRepository;
    this.vacationMapper = vacationMapper;
  }

  @Override
  public List<VacationResponseDTO> list(User requester) {
    List<VacationRequest> vacations;
    if (requester.getRole() == Role.ADMIN) {
      vacations = vacationRepository.findAll();
    } else if (requester.getRole() == Role.MANAGER) {
      vacations = vacationRepository.findAll().stream()
          .filter(v -> v.getCollaborator().getManager() != null
              && v.getCollaborator().getManager().getId().equals(requester.getId()))
          .toList();
    } else {
      vacations = vacationRepository.findAll().stream()
          .filter(v -> v.getCollaborator().getId().equals(requester.getId()))
          .toList();
    }
    return vacations.stream().map(vacationMapper::toResponseDTO).toList();
  }

  @Override
  @Transactional
  public VacationResponseDTO create(User requester, VacationDTO dto) {
    // Seleciona o colaborador alvo: padrão é o próprio usuário
    User target = requester;
    if (dto.getCollaboratorId() != null) {
      target = userRepository.findById(dto.getCollaboratorId())
          .orElseThrow(() -> new NotFoundException("Colaborador não encontrado"));

      // Permissões: ADMIN pode criar para qualquer; MANAGER apenas para seus
      // colaboradores
      if (requester.getRole() == Role.MANAGER) {
        if (target.getManager() == null || !target.getManager().getId().equals(requester.getId())) {
          throw new ForbiddenException("Sem permissão para criar férias para este colaborador");
        }
      } else if (requester.getRole() == Role.COLLABORATOR && !target.getId().equals(requester.getId())) {
        throw new ForbiddenException("Sem permissão para criar férias para outro colaborador");
      }
    }

    // Validação de limite máximo de dias por período (configuração)
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

    // Validação de conflito global: qualquer pessoa já tem um pedido
    // (pendente/aprovado)
    if (vacationRepository.existsAnyOverlappingNotRejected(dto.getStartDate(), dto.getEndDate())) {
      throw new BusinessException("Já existe alguém de férias neste período.");
    }

    // Converte DTO para entidade usando o Mapper
    VacationRequest request = vacationMapper.toEntity(dto);
    request.setCollaborator(target);

    VacationRequest savedRequest = vacationRepository.save(request);
    return vacationMapper.toResponseDTO(savedRequest);
  }

  @Override
  @Transactional
  public VacationResponseDTO approve(Long id, User requester) {
    VacationRequest request = vacationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Pedido não encontrado"));

    if (request.getStatus() != VacationStatus.PENDING) {
      throw new BusinessException("Pedido já processado");
    }

    if (requester.getRole() == Role.ADMIN ||
        (requester.getRole() == Role.MANAGER && request.getCollaborator().getManager() != null
            && request.getCollaborator().getManager().getId().equals(requester.getId()))) {

      if (vacationRepository.existsOverlappingForUser(request.getStartDate(), request.getEndDate(),
          request.getCollaborator().getId())) {
        throw new BusinessException("Já existe férias aprovadas para este período.");
      }

      request.setStatus(VacationStatus.APPROVED);
      VacationRequest savedRequest = vacationRepository.save(request);
      return vacationMapper.toResponseDTO(savedRequest);
    } else {
      throw new ForbiddenException("Sem permissão para aprovar este pedido");
    }
  }

  @Override
  @Transactional
  public VacationResponseDTO reject(Long id, User requester) {
    VacationRequest request = vacationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Pedido não encontrado"));

    if (request.getStatus() != VacationStatus.PENDING) {
      throw new BusinessException("Pedido já processado");
    }

    if (requester.getRole() == Role.ADMIN ||
        (requester.getRole() == Role.MANAGER && request.getCollaborator().getManager() != null
            && request.getCollaborator().getManager().getId().equals(requester.getId()))) {

      request.setStatus(VacationStatus.REJECTED);
      VacationRequest savedRequest = vacationRepository.save(request);
      return vacationMapper.toResponseDTO(savedRequest);
    } else {
      throw new ForbiddenException("Sem permissão para rejeitar este pedido");
    }
  }
}
