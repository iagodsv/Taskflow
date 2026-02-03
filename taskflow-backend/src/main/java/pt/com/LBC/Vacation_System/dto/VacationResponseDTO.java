package pt.com.LBC.Vacation_System.dto;

import java.time.LocalDate;

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
public class VacationResponseDTO {

  @Schema(description = "ID do pedido de férias")
  private Long id;

  @Schema(description = "ID do colaborador")
  private Long collaboratorId;

  @Schema(description = "Nome do colaborador")
  private String collaboratorName;

  @Schema(description = "Email do colaborador")
  private String collaboratorEmail;

  @Schema(description = "Data de início das férias")
  private LocalDate startDate;

  @Schema(description = "Data de término das férias")
  private LocalDate endDate;

  @Schema(description = "Status do pedido (PENDING | APPROVED | REJECTED)")
  private String status;
}
