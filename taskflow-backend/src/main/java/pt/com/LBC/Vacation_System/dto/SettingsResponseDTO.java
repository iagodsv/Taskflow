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
public class SettingsResponseDTO {

  @Schema(description = "ID das configurações")
  private Long id;

  @Schema(description = "Dias mínimos de antecedência para solicitar férias")
  private int minLeadDays;

  @Schema(description = "Período mínimo de férias em dias")
  private int minPeriodDays;

  @Schema(description = "Período máximo de férias em dias")
  private int maxPeriodDays;

  @Schema(description = "Dias bloqueados para férias")
  private List<String> blackoutDays;

  @Schema(description = "Emails para notificação")
  private List<String> notificationEmails;
}
