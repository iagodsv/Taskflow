package pt.com.LBC.Vacation_System.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VacationDTO {
  @Schema(description = "Data de início das férias (AAAA-MM-DD)", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-07-10")
  private LocalDate startDate;

  @Schema(description = "Data de término das férias (AAAA-MM-DD)", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-07-20")
  private LocalDate endDate;

  @Schema(description = "Opcional: ID do colaborador alvo (para ADMIN/MANAGER)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private Long collaboratorId;

}
