package pt.com.LBC.Vacation_System.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import pt.com.LBC.Vacation_System.security.UserDetailsImpl;
import pt.com.LBC.Vacation_System.service.MaintenanceService;

@Slf4j
@RestController
@RequestMapping("/maintenance")
public class MaintenanceController {

  private final MaintenanceService maintenanceService;

  public MaintenanceController(MaintenanceService maintenanceService) {
    this.maintenanceService = maintenanceService;
  }

  @PostMapping("/clean")
  @Operation(summary = "Limpa dados do sistema", description = "Apaga pedidos, configurações e usuários (em ordem segura). Requer ADMIN.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Limpeza realizada"),
      @ApiResponse(responseCode = "401", description = "Não autenticado"),
      @ApiResponse(responseCode = "403", description = "Apenas ADMIN pode executar")
  })
  public void clean(@AuthenticationPrincipal UserDetailsImpl principal) {
    log.info("Iniciando limpeza do sistema. Requisitante: {}",
        principal != null && principal.getUser() != null ? principal.getUser().getEmail() : "desconhecido");
    maintenanceService.clean(principal != null ? principal.getUser() : null);
    log.info("Limpeza do sistema concluída com sucesso");
  }

  @PostMapping("/seed")
  @Operation(summary = "Popula dados de exemplo", description = "Cria usuários e configurações iniciais para testes. Requer ADMIN.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "População realizada"),
      @ApiResponse(responseCode = "401", description = "Não autenticado"),
      @ApiResponse(responseCode = "403", description = "Apenas ADMIN pode executar")
  })
  public void seed(@AuthenticationPrincipal UserDetailsImpl principal) {
    log.info("Iniciando população de dados de exemplo. Requisitante: {}",
        principal != null && principal.getUser() != null ? principal.getUser().getEmail() : "desconhecido");
    maintenanceService.seed(principal != null ? principal.getUser() : null);
    log.info("População de dados de exemplo concluída com sucesso");
  }
}
