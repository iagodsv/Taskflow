package pt.com.LBC.Vacation_System.service;

import pt.com.LBC.Vacation_System.model.Settings;
import pt.com.LBC.Vacation_System.model.User;

public interface SettingsService {
  Settings get(User requester);

  Settings update(User requester, Settings toUpdate);
}
