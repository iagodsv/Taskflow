package pt.com.LBC.Vacation_System.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import pt.com.LBC.Vacation_System.dto.CreateUserRequest;
import pt.com.LBC.Vacation_System.dto.UpdateUserRequest;
import pt.com.LBC.Vacation_System.dto.UserResponseDTO;
import pt.com.LBC.Vacation_System.exception.BadRequestException;
import pt.com.LBC.Vacation_System.exception.ForbiddenException;
import pt.com.LBC.Vacation_System.exception.NotFoundException;
import pt.com.LBC.Vacation_System.mapper.UserMapper;
import pt.com.LBC.Vacation_System.model.Role;
import pt.com.LBC.Vacation_System.model.User;
import pt.com.LBC.Vacation_System.repository.UserRepository;
import pt.com.LBC.Vacation_System.repository.VacationRepository;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final VacationRepository vacationRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  public UserServiceImpl(UserRepository userRepository, VacationRepository vacationRepository,
      UserMapper userMapper, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.vacationRepository = vacationRepository;
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public List<UserResponseDTO> list(User requester) {
    List<User> users;
    if (requester.getRole() == Role.ADMIN) {
      users = userRepository.findAll();
    } else if (requester.getRole() == Role.MANAGER) {
      users = userRepository.findAll().stream()
          .filter(u -> u.getManager() != null && u.getManager().getId().equals(requester.getId()))
          .toList();
    } else {
      users = List.of(requester);
    }
    return users.stream().map(userMapper::toResponseDTO).toList();
  }

  @Override
  @Transactional
  public UserResponseDTO create(User requester, CreateUserRequest request) {
    if (requester.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Apenas admins podem criar usuários");
    }

    // Valida duplicidade de email
    if (request.getEmail() != null && !request.getEmail().isBlank()) {
      boolean exists = userRepository.findByEmail(request.getEmail()).isPresent();
      if (exists) {
        throw new BadRequestException("Já existe email cadastrado");
      }
    }

    // Converte DTO para entidade usando o Mapper
    User user = userMapper.toEntity(request);

    // Define o gestor se necessário
    if (user.getRole() == Role.COLLABORATOR && request.getManagerId() != null) {
      User manager = userRepository.findById(request.getManagerId()).orElse(null);
      user.setManager(manager);
    }

    // Codifica a senha se fornecida
    if (request.getPassword() != null && !request.getPassword().isBlank()) {
      user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
    }

    User savedUser = userRepository.save(user);
    return userMapper.toResponseDTO(savedUser);
  }

  @Override
  @Transactional
  public UserResponseDTO update(User requester, Long id, UpdateUserRequest request) {
    if (requester.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Apenas admins podem editar usuários");
    }

    User existing = userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

    // Valida duplicidade ao alterar email
    if (request.getEmail() != null && !request.getEmail().isBlank()) {
      var found = userRepository.findByEmail(request.getEmail());
      if (found.isPresent() && !found.get().getId().equals(id)) {
        throw new BadRequestException("Já existe email cadastrado");
      }
    }

    // Aplica atualizações usando o Mapper
    userMapper.updateEntity(existing, request);

    // Trata o gestor
    if (existing.getRole() == Role.COLLABORATOR) {
      if (request.getManagerId() != null) {
        User manager = userRepository.findById(request.getManagerId()).orElse(null);
        existing.setManager(manager);
      }
    } else {
      existing.setManager(null);
    }

    // Codifica a senha se fornecida
    if (request.getPassword() != null && !request.getPassword().isBlank()) {
      existing.setPasswordHash(passwordEncoder.encode(request.getPassword()));
    }

    User savedUser = userRepository.save(existing);
    return userMapper.toResponseDTO(savedUser);
  }

  @Override
  @Transactional
  public void delete(User requester, Long id) {
    if (requester.getRole() != Role.ADMIN) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas admins podem remover usuários");
    }

    // Bloqueia exclusão quando há vínculos
    long managedCount = userRepository.countByManager_Id(id);
    if (managedCount > 0) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
          "Não é possível excluir: gestor possui colaboradores associados");
    }

    long requestsCount = vacationRepository.countByCollaborator_Id(id);
    if (requestsCount > 0) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
          "Não é possível excluir: colaborador possui pedidos de férias");
    }

    userRepository.deleteById(id);
  }

  @Override
  public UserResponseDTO get(User requester, Long id) {
    User user;
    if (requester.getRole() == Role.ADMIN) {
      user = userRepository.findById(id)
          .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    } else if (requester.getRole() == Role.MANAGER) {
      user = userRepository.findById(id)
          .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
      if (user.getManager() == null || !user.getManager().getId().equals(requester.getId())) {
        throw new ForbiddenException("Sem permissão para acessar este usuário");
      }
    } else {
      if (!requester.getId().equals(id)) {
        throw new ForbiddenException("Sem permissão para acessar este usuário");
      }
      user = requester;
    }
    return userMapper.toResponseDTO(user);
  }
}
