package pt.com.LBC.Vacation_System.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import pt.com.LBC.Vacation_System.dto.CreateUserRequest;
import pt.com.LBC.Vacation_System.dto.UpdateUserRequest;
import pt.com.LBC.Vacation_System.exception.ForbiddenException;
import pt.com.LBC.Vacation_System.exception.NotFoundException;
import pt.com.LBC.Vacation_System.model.Role;
import pt.com.LBC.Vacation_System.model.User;
import pt.com.LBC.Vacation_System.repository.UserRepository;
import pt.com.LBC.Vacation_System.security.UserDetailsImpl;
import pt.com.LBC.Vacation_System.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService service;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserController(UserService service, UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.service = service;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @GetMapping
  @Operation(summary = "Lista usuários", description = "Retorna a lista de usuários visíveis para o usuário autenticado.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Lista retornada"),
      @ApiResponse(responseCode = "401", description = "Não autenticado")
  })
  public List<User> list(@AuthenticationPrincipal UserDetailsImpl user) {
    return service.list(user.getUser());
  }

  @GetMapping("/{id}")
  @Operation(summary = "Obter usuário por ID", description = "Retorna o usuário identificado por {id}.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
      @ApiResponse(responseCode = "401", description = "Não autenticado"),
      @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
  })
  public User get(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl user) {
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
  public User create(@RequestBody CreateUserRequest req, @AuthenticationPrincipal UserDetailsImpl user) {
    // Somente admin
    if (user.getUser().getRole() != Role.ADMIN) {
      throw new ForbiddenException("Apenas admins podem criar usuários");
    }
    User u = new User();
    u.setName(req.getName());
    u.setEmail(req.getEmail());
    // role
    Role role = Role.COLLABORATOR;
    if (req.getRole() != null && !req.getRole().isBlank()) {
      try {
        role = Role.valueOf(req.getRole().trim().toUpperCase());
      } catch (IllegalArgumentException ignored) {
      }
    }
    u.setRole(role);
    // gestor
    if (role == Role.COLLABORATOR && req.getManagerId() != null) {
      User manager = userRepository.findById(req.getManagerId()).orElse(null);
      u.setManager(manager);
    }
    // saldo e ativo
    u.setBalanceDays(req.getBalanceDays() != null ? req.getBalanceDays() : 22);
    u.setActive(req.getActive() != null ? req.getActive() : true);
    // senha
    if (req.getPassword() != null && !req.getPassword().isBlank()) {
      u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
    }
    return service.create(user.getUser(), u);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Atualizar usuário", description = "Apenas ADMIN. Atualiza campos conforme a DTO de atualização.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Usuário atualizado"),
      @ApiResponse(responseCode = "401", description = "Não autenticado"),
      @ApiResponse(responseCode = "403", description = "Apenas ADMIN"),
      @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
  })
  public User update(@PathVariable Long id, @RequestBody UpdateUserRequest req,
      @AuthenticationPrincipal UserDetailsImpl user) {
    // Apenas admin
    if (user.getUser().getRole() != Role.ADMIN) {
      throw new ForbiddenException("Apenas admins podem editar usuários");
    }
    User existing = userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    if (req.getName() != null)
      existing.setName(req.getName());
    if (req.getEmail() != null)
      existing.setEmail(req.getEmail());
    if (req.getRole() != null && !req.getRole().isBlank()) {
      try {
        existing.setRole(Role.valueOf(req.getRole().trim().toUpperCase()));
      } catch (IllegalArgumentException ignored) {
      }
    }
    if (existing.getRole() == Role.COLLABORATOR) {
      if (req.getManagerId() != null) {
        User manager = userRepository.findById(req.getManagerId()).orElse(null);
        existing.setManager(manager);
      }
    } else {
      existing.setManager(null);
    }
    if (req.getBalanceDays() != null)
      existing.setBalanceDays(req.getBalanceDays());
    if (req.getActive() != null)
      existing.setActive(req.getActive());
    if (req.getPassword() != null && !req.getPassword().isBlank()) {
      existing.setPasswordHash(passwordEncoder.encode(req.getPassword()));
    }
    return service.update(user.getUser(), id, existing);
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
    service.delete(user.getUser(), id);
  }
}
