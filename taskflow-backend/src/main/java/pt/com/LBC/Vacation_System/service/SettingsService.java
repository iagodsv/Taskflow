package pt.com.LBC.Vacation_System.service;

import pt.com.LBC.Vacation_System.dto.SettingsRequestDTO;
import pt.com.LBC.Vacation_System.dto.SettingsResponseDTO;
import pt.com.LBC.Vacation_System.model.User;

public interface SettingsService {

  /**
   * Obtém as configurações do sistema
   */
  SettingsResponseDTO get(User requester);

  /**
   * Atualiza as configurações do sistema
   */
  SettingsResponseDTO update(User requester, SettingsRequestDTO request);
}
