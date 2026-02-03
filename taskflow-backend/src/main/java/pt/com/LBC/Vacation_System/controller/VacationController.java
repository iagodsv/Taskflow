package pt.com.LBC.Vacation_System.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import pt.com.LBC.Vacation_System.dto.VacationDTO;
import pt.com.LBC.Vacation_System.dto.VacationResponseDTO;
import pt.com.LBC.Vacation_System.security.UserDetailsImpl;
import pt.com.LBC.Vacation_System.service.VacationService;

@Slf4j
@RestController
@RequestMapping("/vacations")
public class VacationController {

    private final VacationService service;

    public VacationController(VacationService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista pedidos de férias", description = "Retorna os pedidos visíveis para o usuário autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public List<VacationResponseDTO> list(@AuthenticationPrincipal UserDetailsImpl user) {
        log.info("Listando pedidos de férias. Requisitante: {}", user.getUser().getEmail());
        return service.list(user.getUser());
    }

    @PostMapping
    @Operation(summary = "Criar pedido de férias", description = "Campos obrigatórios: startDate, endDate. Opcional: collaboratorId (ADMIN/MANAGER).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido criado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public VacationResponseDTO create(@RequestBody VacationDTO dto,
            @AuthenticationPrincipal UserDetailsImpl user) {
        log.info("Criando pedido de férias. Período: {} a {}. Requisitante: {}",
                dto.getStartDate(), dto.getEndDate(), user.getUser().getEmail());
        return service.create(user.getUser(), dto);
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Aprovar pedido", description = "Aprova o pedido identificado por {id}. Requer permissão adequada (MANAGER/ADMIN).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido aprovado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public VacationResponseDTO approve(@PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl user) {
        log.info("Aprovando pedido de férias com ID: {}. Requisitante: {}", id, user.getUser().getEmail());
        return service.approve(id, user.getUser());
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Rejeitar pedido", description = "Rejeita o pedido identificado por {id}. Requer permissão adequada (MANAGER/ADMIN).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido rejeitado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public VacationResponseDTO reject(@PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl user) {
        log.info("Rejeitando pedido de férias com ID: {}. Requisitante: {}", id, user.getUser().getEmail());
        return service.reject(id, user.getUser());
    }
}
