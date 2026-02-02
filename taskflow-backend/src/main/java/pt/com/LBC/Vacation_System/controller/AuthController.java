package pt.com.LBC.Vacation_System.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import pt.com.LBC.Vacation_System.dto.LoginRequest;
import pt.com.LBC.Vacation_System.dto.LoginResponse;
import pt.com.LBC.Vacation_System.model.User;
import pt.com.LBC.Vacation_System.repository.UserRepository;
import pt.com.LBC.Vacation_System.security.JwtTokenProvider;
import pt.com.LBC.Vacation_System.security.UserDetailsImpl;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final UserRepository userRepository;
  private final JwtTokenProvider tokenProvider;
  private final boolean allowRoleUpdateOnLogin;
  private final PasswordEncoder passwordEncoder;

  public AuthController(
      UserRepository userRepository,
      JwtTokenProvider tokenProvider,
      @Value("${app.allowRoleUpdateOnLogin:false}") boolean allowRoleUpdateOnLogin,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.tokenProvider = tokenProvider;
    this.allowRoleUpdateOnLogin = allowRoleUpdateOnLogin;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/login")
  @Operation(summary = "Autenticação e emissão de JWT", description = "Envia e-mail e senha. Retorna token JWT e dados básicos do usuário.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Login bem-sucedido"),
      @ApiResponse(responseCode = "400", description = "Campos obrigatórios ausentes"),
      @ApiResponse(responseCode = "401", description = "Credenciais inválidas ou usuário sem senha")
  })
  public ResponseEntity<?> login(@RequestBody LoginRequest req) {
    if (req.getEmail() == null || req.getEmail().isBlank()) {
      return ResponseEntity.badRequest().body("Email é obrigatório");
    }
    if (req.getPassword() == null || req.getPassword().isBlank()) {
      return ResponseEntity.badRequest().body("Senha é obrigatória");
    }

    User user = userRepository.findByEmail(req.getEmail()).orElse(null);
    if (user == null) {
      return ResponseEntity.status(401).body("Usuário não encontrado");
    }

    // Exige senha configurada e válida
    if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
      return ResponseEntity.status(401).body("Usuário sem senha definida. Contate o administrador.");
    }
    if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
      return ResponseEntity.status(401).body("Credenciais inválidas");
    }

    // Por segurança, ignoramos qualquer tentativa de alterar role via login

    String token = tokenProvider.generateToken(user);
    return ResponseEntity
        .ok(new LoginResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole().name()));
  }

  @GetMapping("/me")
  @Operation(summary = "Usuário autenticado", description = "Retorna os dados do usuário atual. Requer Authorization: Bearer <token>.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Usuário autenticado"),
      @ApiResponse(responseCode = "401", description = "Não autenticado")
  })
  public ResponseEntity<?> me() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof UserDetailsImpl details)) {
      return ResponseEntity.status(401).body("Não autenticado");
    }
    User u = details.getUser();
    return ResponseEntity.ok(new LoginResponse("", u.getId(), u.getName(), u.getEmail(), u.getRole().name()));
  }
}
