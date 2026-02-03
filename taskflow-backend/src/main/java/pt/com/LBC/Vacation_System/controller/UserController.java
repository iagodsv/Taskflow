package pt.com.LBC.Vacation_System.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import pt.com.LBC.Vacation_System.dto.CreateUserRequest;
import pt.com.LBC.Vacation_System.dto.UpdateUserRequest;
import pt.com.LBC.Vacation_System.dto.UserResponseDTO;
import pt.com.LBC.Vacation_System.security.UserDetailsImpl;
import pt.com.LBC.Vacation_System.service.UserService;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService service;

  public UserController(UserService service) {
    this.service = service;
  }

  @GetMapping
  @Operation(summary = "Lista usuários", description = "Retorna a lista de usuários visíveis para o usuário autenticado.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Lista retornada"),
      @ApiResponse(responseCode = "401", description = "Não autenticado")
  })
  public List<UserResponseDTO> list(@AuthenticationPrincipal UserDetailsImpl user) {
    log.info("Listando usuários. Requisitante: {}", user.getUser().getEmail());
    return service.list(user.getUser());
  }

  @GetMapping("/{id}")
  @Operation(summary = "Obter usuário por ID", description = "Retorna o usuário identificado por {id}.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
      @ApiResponse(responseCode = "401", description = "Não autenticado"),
      @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
  })
  public UserResponseDTO get(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl user) {
    log.info("Buscando usuário com ID: {}. Requisitante: {}", id, user.getUser().getEmail());
    return service.get(user.getUser(), id);
  }

  @PostMapping
  @Operation(summary = "Criar usuário", description = "Apenas ADMIN. Campos obrigatórios: name, email. Demais campos opcionais conforme documentação da DTO.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Usuário criado"),
      @ApiResponse(responseCode = "401", description = "Não autenticado"),
      @ApiResponse(responseCode = "403", description = "Apenas ADMIN"),
      @ApiResponse(responseCode = "400", description = "Dados inválidos")
  })
  public UserResponseDTO create(@RequestBody CreateUserRequest request,
      @AuthenticationPrincipal UserDetailsImpl user) {
    log.info("Criando usuário com email: {}. Requisitante: {}", request.getEmail(), user.getUser().getEmail());
    return service.create(user.getUser(), request);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Atualizar usuário", description = "Apenas ADMIN. Atualiza campos conforme a DTO de atualização.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Usuário atualizado"),
      @ApiResponse(responseCode = "401", description = "Não autenticado"),
      @ApiResponse(responseCode = "403", description = "Apenas ADMIN"),
      @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
  })
  public UserResponseDTO update(@PathVariable Long id, @RequestBody UpdateUserRequest request,
      @AuthenticationPrincipal UserDetailsImpl user) {
    log.info("Atualizando usuário com ID: {}. Requisitante: {}", id, user.getUser().getEmail());
    return service.update(user.getUser(), id, request);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Excluir usuário", description = "Remove o usuário pelo {id}. Regras de permissão aplicam-se no serviço.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Usuário removido"),
      @ApiResponse(responseCode = "401", description = "Não autenticado"),
      @ApiResponse(responseCode = "403", description = "Sem permissão"),
      @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
  })
  public void delete(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl user) {
    log.info("Removendo usuário com ID: {}. Requisitante: {}", id, user.getUser().getEmail());
    service.delete(user.getUser(), id);
  }
}
