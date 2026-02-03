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
import lombok.extern.slf4j.Slf4j;
import pt.com.LBC.Vacation_System.dto.SettingsRequestDTO;
import pt.com.LBC.Vacation_System.dto.SettingsResponseDTO;
import pt.com.LBC.Vacation_System.security.UserDetailsImpl;
import pt.com.LBC.Vacation_System.service.SettingsService;

@Slf4j
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
  public SettingsResponseDTO get(@AuthenticationPrincipal UserDetailsImpl user) {
    log.info("Buscando configurações. Requisitante: {}", user.getUser().getEmail());
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
  public SettingsResponseDTO update(@RequestBody SettingsRequestDTO request,
      @AuthenticationPrincipal UserDetailsImpl user) {
    log.info("Atualizando configurações. Requisitante: {}", user.getUser().getEmail());
    return service.update(user.getUser(), request);
  }
}
