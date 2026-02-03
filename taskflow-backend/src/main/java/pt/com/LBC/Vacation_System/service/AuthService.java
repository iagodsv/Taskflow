package pt.com.LBC.Vacation_System.service;

import pt.com.LBC.Vacation_System.dto.LoginRequest;
import pt.com.LBC.Vacation_System.dto.LoginResponse;
import pt.com.LBC.Vacation_System.model.User;

public interface AuthService {

  /**
   * Realiza o login do usuário e retorna o token JWT
   */
  LoginResponse login(LoginRequest request);

  /**
   * Retorna os dados do usuário autenticado
   */
  LoginResponse me(User user);
}
