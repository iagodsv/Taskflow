package pt.com.LBC.Vacation_System.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
  @Schema(description = "Email do usuário", requiredMode = Schema.RequiredMode.REQUIRED, example = "iago.admin@taskflow.pt")
  private String email;

  @Schema(description = "Senha do usuário", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
  private String password;

  @Schema(description = "Ignorado no login. Papel do usuário (ADMIN | MANAGER | COLLABORATOR)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String role;

  @Schema(description = "Ignorado no login. Nome do usuário", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String name;

}
