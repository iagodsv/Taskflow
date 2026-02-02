package pt.com.LBC.Vacation_System.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import pt.com.LBC.Vacation_System.model.Settings;
import pt.com.LBC.Vacation_System.security.UserDetailsImpl;
import pt.com.LBC.Vacation_System.service.SettingsService;

@RestController
@RequestMapping("/settings")
public class SettingsController {

  private final SettingsService service;

  public SettingsController(SettingsService service) {
    this.service = service;
  }

  @GetMapping
  @Operation(summary = "Buscar configurações", description = "Retorna as configurações globais visíveis ao usuário autenticado.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Configurações retornadas"),
      @ApiResponse(responseCode = "401", description = "Não autenticado")
  })
  public Settings get(@AuthenticationPrincipal UserDetailsImpl user) {
    return service.get(user.getUser());
  }

  @PutMapping
  @Operation(summary = "Atualizar configurações", description = "Atualiza as configurações globais. Requer permissão (ADMIN).")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Configurações atualizadas"),
      @ApiResponse(responseCode = "401", description = "Não autenticado"),
      @ApiResponse(responseCode = "403", description = "Sem permissão"),
      @ApiResponse(responseCode = "400", description = "Dados inválidos")
  })
  public Settings update(@RequestBody Settings toUpdate,
      @AuthenticationPrincipal UserDetailsImpl user) {
    return service.update(user.getUser(), toUpdate);
  }
}
