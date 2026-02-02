package pt.com.LBC.Vacation_System.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import pt.com.LBC.Vacation_System.exception.BadRequestException;
import pt.com.LBC.Vacation_System.exception.ForbiddenException;
import pt.com.LBC.Vacation_System.exception.NotFoundException;
import pt.com.LBC.Vacation_System.model.Role;
import pt.com.LBC.Vacation_System.model.User;
import pt.com.LBC.Vacation_System.repository.UserRepository;
import pt.com.LBC.Vacation_System.repository.VacationRepository;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final VacationRepository vacationRepository;

  public UserServiceImpl(UserRepository userRepository, VacationRepository vacationRepository) {
    this.userRepository = userRepository;
    this.vacationRepository = vacationRepository;
  }

  @Override
  public List<User> list(User user) {
    if (user.getRole() == Role.ADMIN) {
      return userRepository.findAll();
    } else if (user.getRole() == Role.MANAGER) {
      return userRepository.findAll().stream()
          .filter(u -> u.getManager() != null && u.getManager().getId().equals(user.getId()))
          .toList();
    } else {
      return List.of(user);
    }
  }

  @Override
  @Transactional
  public User create(User user, User toCreate) {
    if (user.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Apenas admins podem criar usuários");
    }
    // Valida duplicidade de email
    if (toCreate.getEmail() != null && !toCreate.getEmail().isBlank()) {
      boolean exists = userRepository.findByEmail(toCreate.getEmail()).isPresent();
      if (exists) {
        throw new BadRequestException("Já existe email cadastrado");
      }
    }
    return userRepository.save(toCreate);
  }

  @Override
  @Transactional
  public User update(User user, Long id, User toUpdate) {
    if (user.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Apenas admins podem editar usuários");
    }
    User existing = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    // Valida duplicidade ao alterar email
    if (toUpdate.getEmail() != null && !toUpdate.getEmail().isBlank()) {
      var found = userRepository.findByEmail(toUpdate.getEmail());
      if (found.isPresent() && !found.get().getId().equals(id)) {
        throw new BadRequestException("Já existe email cadastrado");
      }
    }
    existing.setName(toUpdate.getName());
    existing.setEmail(toUpdate.getEmail());
    existing.setRole(toUpdate.getRole());
    existing.setManager(toUpdate.getManager());
    return userRepository.save(existing);
  }

  @Override
  @Transactional
  public void delete(User user, Long id) {
    if (user.getRole() != Role.ADMIN) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas admins podem remover usuários");
    }
    // Bloqueia exclusão quando há vínculos: colaboradores de gestor ou pedidos de
    // férias do colaborador
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
  public User get(User user, Long id) {
    if (user.getRole() == Role.ADMIN) {
      return userRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    } else if (user.getRole() == Role.MANAGER) {
      User u = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
      if (u.getManager() != null && u.getManager().getId().equals(user.getId())) {
        return u;
      } else {
        throw new ForbiddenException("Sem permissão para acessar este usuário");
      }
    } else {
      if (!user.getId().equals(id)) {
        throw new ForbiddenException("Sem permissão para acessar este usuário");
      }
      return user;
    }
  }
}
