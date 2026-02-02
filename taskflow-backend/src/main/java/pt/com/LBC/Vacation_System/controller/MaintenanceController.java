package pt.com.LBC.Vacation_System.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import pt.com.LBC.Vacation_System.model.Role;
import pt.com.LBC.Vacation_System.model.User;
import pt.com.LBC.Vacation_System.repository.SettingsRepository;
import pt.com.LBC.Vacation_System.repository.UserRepository;
import pt.com.LBC.Vacation_System.repository.VacationRepository;
import pt.com.LBC.Vacation_System.security.UserDetailsImpl;

@RestController
@RequestMapping("/maintenance")
public class MaintenanceController {

  private final UserRepository userRepository;
  private final VacationRepository vacationRepository;
  private final SettingsRepository settingsRepository;
  private final PasswordEncoder passwordEncoder;

  public MaintenanceController(UserRepository userRepository, VacationRepository vacationRepository,
      SettingsRepository settingsRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.vacationRepository = vacationRepository;
    this.settingsRepository = settingsRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/clean")
  @Operation(summary = "Limpa dados do sistema", description = "Apaga pedidos, configurações e usuários (em ordem segura). Requer ADMIN.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Limpeza realizada"),
      @ApiResponse(responseCode = "401", description = "Não autenticado"),
      @ApiResponse(responseCode = "403", description = "Apenas ADMIN pode executar")
  })
  @Transactional
  public void clean(@AuthenticationPrincipal UserDetailsImpl principal) {
    if (principal == null || principal.getUser() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Não autenticado");
    }
    if (principal.getUser().getRole() != Role.ADMIN) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas admins podem executar limpeza");
    }
    // Remove vínculos para evitar violação de FK no self-join manager
    userRepository.findAll().forEach(u -> {
      u.setManager(null);
      userRepository.save(u);
    });
    // Apaga dados em ordem segura
    vacationRepository.deleteAll();
    settingsRepository.deleteAll();
    userRepository.deleteAll();
  }

  @PostMapping("/seed")
  @Operation(summary = "Popula dados de exemplo", description = "Cria usuários e configurações iniciais para testes. Requer ADMIN.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "População realizada"),
      @ApiResponse(responseCode = "401", description = "Não autenticado"),
      @ApiResponse(responseCode = "403", description = "Apenas ADMIN pode executar")
  })
  @Transactional
  public void seed(@AuthenticationPrincipal UserDetailsImpl principal) {
    if (principal == null || principal.getUser() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Não autenticado");
    }
    if (principal.getUser().getRole() != Role.ADMIN) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas admins podem executar população");
    }
    // Admin
    User admin = new User();
    admin.setName("Iago Admin");
    admin.setEmail("iago.admin@taskflow.pt");
    admin.setRole(Role.ADMIN);
    admin.setActive(true);
    admin.setBalanceDays(22);
    admin.setPasswordHash(passwordEncoder.encode("123456"));
    admin = userRepository.save(admin);

    // Gestores
    User gestor1 = new User();
    gestor1.setName("Iago Gestor");
    gestor1.setEmail("iago.gestor@taskflow.pt");
    gestor1.setRole(Role.MANAGER);
    gestor1.setActive(true);
    gestor1.setBalanceDays(22);
    gestor1.setPasswordHash(passwordEncoder.encode("123456"));
    gestor1 = userRepository.save(gestor1);

    // Colaboradores
    User col1 = new User();
    col1.setName("Iago Colab 1");
    col1.setEmail("iago.colab1@taskflow.pt");
    col1.setRole(Role.COLLABORATOR);
    col1.setActive(true);
    col1.setBalanceDays(22);
    col1.setPasswordHash(passwordEncoder.encode("123456"));
    col1.setManager(gestor1);
    userRepository.save(col1);

  }
}
