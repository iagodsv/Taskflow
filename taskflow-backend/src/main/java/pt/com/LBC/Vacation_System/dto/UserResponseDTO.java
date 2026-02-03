package pt.com.LBC.Vacation_System.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

  @Schema(description = "ID do usuário")
  private Long id;

  @Schema(description = "Nome do usuário")
  private String name;

  @Schema(description = "Email do usuário")
  private String email;

  @Schema(description = "Papel do usuário (ADMIN | MANAGER | COLLABORATOR)")
  private String role;

  @Schema(description = "ID do gestor (apenas para COLLABORATOR)")
  private Long managerId;

  @Schema(description = "Nome do gestor (apenas para COLLABORATOR)")
  private String managerName;

  @Schema(description = "Saldo de dias de férias disponíveis")
  private Integer balanceDays;

  @Schema(description = "Se o usuário está ativo")
  private Boolean active;
}
