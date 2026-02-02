package pt.com.LBC.Vacation_System.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {
  @Schema(description = "Nome do usuário", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @Schema(description = "Email do usuário", requiredMode = Schema.RequiredMode.REQUIRED)
  private String email;

  @Schema(description = "Papel do usuário (ADMIN | MANAGER | COLLABORATOR). Se ausente, usa COLLABORATOR", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "COLLABORATOR")
  private String role;

  @Schema(description = "ID do gestor. Opcional e aplicável apenas quando role = COLLABORATOR", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private Long managerId;

  @Schema(description = "Saldo de dias de férias. Opcional, padrão 22", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "22")
  private Integer balanceDays;

  @Schema(description = "Se o usuário está ativo. Opcional, padrão true", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "true")
  private Boolean active;

  @Schema(description = "Senha inicial. Opcional", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String password;

}
