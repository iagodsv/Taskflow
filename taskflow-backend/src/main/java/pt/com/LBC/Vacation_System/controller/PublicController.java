package pt.com.LBC.Vacation_System.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/public")
public class PublicController {

  @GetMapping("/ping")
  @Operation(summary = "Ping público", description = "Endpoint público para verificação de disponibilidade da aplicação.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Aplicação disponível")
  })
  public ResponseEntity<Map<String, String>> ping() {
    return ResponseEntity.ok(Map.of("status", "ok"));
  }
}
