package pt.com.LBC.Vacation_System.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.com.LBC.Vacation_System.exception.ForbiddenException;
import pt.com.LBC.Vacation_System.model.Role;
import pt.com.LBC.Vacation_System.model.Settings;
import pt.com.LBC.Vacation_System.model.User;
import pt.com.LBC.Vacation_System.repository.SettingsRepository;

@Service
public class SettingsServiceImpl implements SettingsService {

  private final SettingsRepository repo;

  public SettingsServiceImpl(SettingsRepository repo) {
    this.repo = repo;
  }

  @Override
  public Settings get(User requester) {
    // Somente ADMIN pode visualizar/editar configurações
    if (requester.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Apenas admins podem acessar configurações");
    }
    return repo.findAll().stream().findFirst().orElseGet(() -> {
      Settings s = new Settings();
      s.setMinLeadDays(0);
      s.setMinPeriodDays(1);
      s.setMaxPeriodDays(30);
      return repo.save(s);
    });
  }

  @Override
  @Transactional
  public Settings update(User requester, Settings toUpdate) {
    if (requester.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Apenas admins podem editar configurações");
    }
    Settings existing = repo.findAll().stream().findFirst().orElse(null);
    if (existing == null) {
      existing = new Settings();
    }
    existing.setMinLeadDays(toUpdate.getMinLeadDays());
    existing.setMinPeriodDays(toUpdate.getMinPeriodDays());
    existing.setMaxPeriodDays(toUpdate.getMaxPeriodDays());
    existing.setBlackoutDays(toUpdate.getBlackoutDays());
    existing.setNotificationEmails(toUpdate.getNotificationEmails());
    return repo.save(existing);
  }
}
