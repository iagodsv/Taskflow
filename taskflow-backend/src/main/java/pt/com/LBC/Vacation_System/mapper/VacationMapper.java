package pt.com.LBC.Vacation_System.mapper;

import org.springframework.stereotype.Component;

import pt.com.LBC.Vacation_System.dto.VacationDTO;
import pt.com.LBC.Vacation_System.dto.VacationResponseDTO;
import pt.com.LBC.Vacation_System.model.VacationRequest;
import pt.com.LBC.Vacation_System.model.VacationStatus;

@Component
public class VacationMapper {

  /**
   * Converte a entidade VacationRequest para VacationResponseDTO
   */
  public VacationResponseDTO toResponseDTO(VacationRequest vacation) {
    if (vacation == null) {
      return null;
    }

    return VacationResponseDTO.builder()
        .id(vacation.getId())
        .collaboratorId(vacation.getCollaborator() != null ? vacation.getCollaborator().getId() : null)
        .collaboratorName(vacation.getCollaborator() != null ? vacation.getCollaborator().getName() : null)
        .collaboratorEmail(vacation.getCollaborator() != null ? vacation.getCollaborator().getEmail() : null)
        .startDate(vacation.getStartDate())
        .endDate(vacation.getEndDate())
        .status(vacation.getStatus() != null ? vacation.getStatus().name() : null)
        .build();
  }

  /**
   * Converte VacationDTO para a entidade VacationRequest
   */
  public VacationRequest toEntity(VacationDTO dto) {
    if (dto == null) {
      return null;
    }

    VacationRequest vacation = new VacationRequest();
    vacation.setStartDate(dto.getStartDate());
    vacation.setEndDate(dto.getEndDate());
    vacation.setStatus(VacationStatus.PENDING);

    return vacation;
  }
}
