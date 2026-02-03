package pt.com.LBC.Vacation_System.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.com.LBC.Vacation_System.dto.SettingsRequestDTO;
import pt.com.LBC.Vacation_System.dto.SettingsResponseDTO;
import pt.com.LBC.Vacation_System.exception.ForbiddenException;
import pt.com.LBC.Vacation_System.mapper.SettingsMapper;
import pt.com.LBC.Vacation_System.model.Role;
import pt.com.LBC.Vacation_System.model.Settings;
import pt.com.LBC.Vacation_System.model.User;
import pt.com.LBC.Vacation_System.repository.SettingsRepository;

@Service
public class SettingsServiceImpl implements SettingsService {

  private final SettingsRepository repo;
  private final SettingsMapper settingsMapper;

  public SettingsServiceImpl(SettingsRepository repo, SettingsMapper settingsMapper) {
    this.repo = repo;
    this.settingsMapper = settingsMapper;
  }

  @Override
  public SettingsResponseDTO get(User requester) {
    // Somente ADMIN pode visualizar/editar configurações
    if (requester.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Apenas admins podem acessar configurações");
    }

    Settings settings = repo.findAll().stream().findFirst().orElseGet(() -> {
      Settings s = new Settings();
      s.setMinLeadDays(0);
      s.setMinPeriodDays(1);
      s.setMaxPeriodDays(30);
      return repo.save(s);
    });

    return settingsMapper.toResponseDTO(settings);
  }

  @Override
  @Transactional
  public SettingsResponseDTO update(User requester, SettingsRequestDTO request) {
    if (requester.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Apenas admins podem editar configurações");
    }

    Settings existing = repo.findAll().stream().findFirst().orElse(null);
    if (existing == null) {
      existing = new Settings();
    }

    // Aplica atualizações usando o Mapper
    settingsMapper.updateEntity(existing, request);

    Settings savedSettings = repo.save(existing);
    return settingsMapper.toResponseDTO(savedSettings);
  }
}
