package pt.com.LBC.Vacation_System.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import pt.com.LBC.Vacation_System.dto.LoginRequest;
import pt.com.LBC.Vacation_System.dto.LoginResponse;
import pt.com.LBC.Vacation_System.exception.UnauthorizedException;
import pt.com.LBC.Vacation_System.security.UserDetailsImpl;
import pt.com.LBC.Vacation_System.service.AuthService;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  @Operation(summary = "Autenticação e emissão de JWT", description = "Envia e-mail e senha. Retorna token JWT e dados básicos do usuário.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Login bem-sucedido"),
      @ApiResponse(responseCode = "400", description = "Campos obrigatórios ausentes"),
      @ApiResponse(responseCode = "401", description = "Credenciais inválidas ou usuário sem senha")
  })
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    log.info("Tentativa de login para email: {}", request.getEmail());
    LoginResponse response = authService.login(request);
    log.info("Login bem-sucedido para usuário: {}", request.getEmail());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/me")
  @Operation(summary = "Usuário autenticado", description = "Retorna os dados do usuário atual. Requer Authorization: Bearer <token>.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Usuário autenticado"),
      @ApiResponse(responseCode = "401", description = "Não autenticado")
  })
  public ResponseEntity<LoginResponse> me() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof UserDetailsImpl details)) {
      log.warn("Tentativa de acesso não autenticado ao endpoint /me");
      throw new UnauthorizedException("Não autenticado");
    }
    log.info("Consultando dados do usuário autenticado: {}", details.getUser().getEmail());
    LoginResponse response = authService.me(details.getUser());
    return ResponseEntity.ok(response);
  }
}
