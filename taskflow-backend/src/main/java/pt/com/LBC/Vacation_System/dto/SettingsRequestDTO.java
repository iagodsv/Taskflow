package pt.com.LBC.Vacation_System.dto;

import java.util.List;

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
public class SettingsRequestDTO {

  @Schema(description = "Dias mínimos de antecedência para solicitar férias", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private Integer minLeadDays;

  @Schema(description = "Período mínimo de férias em dias", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private Integer minPeriodDays;

  @Schema(description = "Período máximo de férias em dias", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private Integer maxPeriodDays;

  @Schema(description = "Dias bloqueados para férias", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private List<String> blackoutDays;

  @Schema(description = "Emails para notificação", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private List<String> notificationEmails;
}
