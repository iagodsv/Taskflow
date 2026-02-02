package pt.com.LBC.Vacation_System.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
  @Schema(description = "Nome do usuário. Opcional", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String name;

  @Schema(description = "Email do usuário. Opcional", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String email;

  @Schema(description = "Novo papel (ADMIN | MANAGER | COLLABORATOR). Opcional", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String role;

  @Schema(description = "ID do gestor. Opcional e aplicável quando role = COLLABORATOR", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private Long managerId;

  @Schema(description = "Novo saldo de dias de férias. Opcional", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private Integer balanceDays;

  @Schema(description = "Se o usuário está ativo. Opcional", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private Boolean active;

  @Schema(description = "Nova senha. Opcional", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String password;

}
