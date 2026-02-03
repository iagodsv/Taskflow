package pt.com.LBC.Vacation_System.mapper;

import org.springframework.stereotype.Component;

import pt.com.LBC.Vacation_System.dto.SettingsRequestDTO;
import pt.com.LBC.Vacation_System.dto.SettingsResponseDTO;
import pt.com.LBC.Vacation_System.model.Settings;

@Component
public class SettingsMapper {

  /**
   * Converte a entidade Settings para SettingsResponseDTO
   */
  public SettingsResponseDTO toResponseDTO(Settings settings) {
    if (settings == null) {
      return null;
    }

    return SettingsResponseDTO.builder()
        .id(settings.getId())
        .minLeadDays(settings.getMinLeadDays())
        .minPeriodDays(settings.getMinPeriodDays())
        .maxPeriodDays(settings.getMaxPeriodDays())
        .blackoutDays(settings.getBlackoutDays())
        .notificationEmails(settings.getNotificationEmails())
        .build();
  }

  /**
   * Aplica as atualizações do SettingsRequestDTO na entidade Settings existente
   */
  public void updateEntity(Settings existing, SettingsRequestDTO request) {
    if (request == null || existing == null) {
      return;
    }

    if (request.getMinLeadDays() != null) {
      existing.setMinLeadDays(request.getMinLeadDays());
    }
    if (request.getMinPeriodDays() != null) {
      existing.setMinPeriodDays(request.getMinPeriodDays());
    }
    if (request.getMaxPeriodDays() != null) {
      existing.setMaxPeriodDays(request.getMaxPeriodDays());
    }
    if (request.getBlackoutDays() != null) {
      existing.setBlackoutDays(request.getBlackoutDays());
    }
    if (request.getNotificationEmails() != null) {
      existing.setNotificationEmails(request.getNotificationEmails());
    }
  }
}
