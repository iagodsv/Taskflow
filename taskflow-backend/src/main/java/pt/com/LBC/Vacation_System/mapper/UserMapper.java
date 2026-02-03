package pt.com.LBC.Vacation_System.mapper;

import org.springframework.stereotype.Component;

import pt.com.LBC.Vacation_System.dto.CreateUserRequest;
import pt.com.LBC.Vacation_System.dto.UpdateUserRequest;
import pt.com.LBC.Vacation_System.dto.UserResponseDTO;
import pt.com.LBC.Vacation_System.model.Role;
import pt.com.LBC.Vacation_System.model.User;

@Component
public class UserMapper {

  /**
   * Converte a entidade User para UserResponseDTO
   */
  public UserResponseDTO toResponseDTO(User user) {
    if (user == null) {
      return null;
    }

    return UserResponseDTO.builder()
        .id(user.getId())
        .name(user.getName())
        .email(user.getEmail())
        .role(user.getRole() != null ? user.getRole().name() : null)
        .managerId(user.getManager() != null ? user.getManager().getId() : null)
        .managerName(user.getManager() != null ? user.getManager().getName() : null)
        .balanceDays(user.getBalanceDays())
        .active(user.getActive())
        .build();
  }

  /**
   * Converte CreateUserRequest para a entidade User
   */
  public User toEntity(CreateUserRequest request) {
    if (request == null) {
      return null;
    }

    User user = new User();
    user.setName(request.getName());
    user.setEmail(request.getEmail());

    // Define o role, padrão COLLABORATOR
    Role role = Role.COLLABORATOR;
    if (request.getRole() != null && !request.getRole().isBlank()) {
      try {
        role = Role.valueOf(request.getRole().trim().toUpperCase());
      } catch (IllegalArgumentException ignored) {
        // Mantém COLLABORATOR se role inválido
      }
    }
    user.setRole(role);

    // Saldo e status padrão
    user.setBalanceDays(request.getBalanceDays() != null ? request.getBalanceDays() : 22);
    user.setActive(request.getActive() != null ? request.getActive() : true);

    return user;
  }

  /**
   * Aplica as atualizações do UpdateUserRequest na entidade User existente
   */
  public void updateEntity(User existing, UpdateUserRequest request) {
    if (request == null || existing == null) {
      return;
    }

    if (request.getName() != null) {
      existing.setName(request.getName());
    }
    if (request.getEmail() != null) {
      existing.setEmail(request.getEmail());
    }
    if (request.getRole() != null && !request.getRole().isBlank()) {
      try {
        existing.setRole(Role.valueOf(request.getRole().trim().toUpperCase()));
      } catch (IllegalArgumentException ignored) {
        // Mantém role atual se inválido
      }
    }
    if (request.getBalanceDays() != null) {
      existing.setBalanceDays(request.getBalanceDays());
    }
    if (request.getActive() != null) {
      existing.setActive(request.getActive());
    }
  }
}
