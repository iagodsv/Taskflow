package pt.com.LBC.Vacation_System.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import pt.com.LBC.Vacation_System.dto.LoginRequest;
import pt.com.LBC.Vacation_System.dto.LoginResponse;
import pt.com.LBC.Vacation_System.exception.BadRequestException;
import pt.com.LBC.Vacation_System.exception.UnauthorizedException;
import pt.com.LBC.Vacation_System.model.User;
import pt.com.LBC.Vacation_System.repository.UserRepository;
import pt.com.LBC.Vacation_System.security.JwtTokenProvider;

@Service
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final JwtTokenProvider tokenProvider;
  private final PasswordEncoder passwordEncoder;

  public AuthServiceImpl(UserRepository userRepository, JwtTokenProvider tokenProvider,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.tokenProvider = tokenProvider;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public LoginResponse login(LoginRequest request) {
    // Validações
    if (request.getEmail() == null || request.getEmail().isBlank()) {
      throw new BadRequestException("Email é obrigatório");
    }
    if (request.getPassword() == null || request.getPassword().isBlank()) {
      throw new BadRequestException("Senha é obrigatória");
    }

    // Busca usuário
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado"));

    // Valida senha
    if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
      throw new UnauthorizedException("Usuário sem senha definida. Contate o administrador.");
    }
    if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
      throw new UnauthorizedException("Credenciais inválidas");
    }

    // Gera token
    String token = tokenProvider.generateToken(user);

    return new LoginResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole().name());
  }

  @Override
  public LoginResponse me(User user) {
    return new LoginResponse("", user.getId(), user.getName(), user.getEmail(), user.getRole().name());
  }
}
