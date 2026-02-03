package pt.com.LBC.Vacation_System.service;

import java.util.List;

import pt.com.LBC.Vacation_System.dto.CreateUserRequest;
import pt.com.LBC.Vacation_System.dto.UpdateUserRequest;
import pt.com.LBC.Vacation_System.dto.UserResponseDTO;
import pt.com.LBC.Vacation_System.model.User;

public interface UserService {

  /**
   * Lista usuários visíveis para o usuário autenticado
   */
  List<UserResponseDTO> list(User requester);

  /**
   * Cria um novo usuário
   */
  UserResponseDTO create(User requester, CreateUserRequest request);

  /**
   * Atualiza um usuário existente
   */
  UserResponseDTO update(User requester, Long id, UpdateUserRequest request);

  /**
   * Remove um usuário
   */
  void delete(User requester, Long id);

  /**
   * Obtém um usuário por ID
   */
  UserResponseDTO get(User requester, Long id);
}
