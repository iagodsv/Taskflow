package pt.com.LBC.Vacation_System.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.com.LBC.Vacation_System.exception.ForbiddenException;
import pt.com.LBC.Vacation_System.exception.UnauthorizedException;
import pt.com.LBC.Vacation_System.model.Role;
import pt.com.LBC.Vacation_System.model.User;
import pt.com.LBC.Vacation_System.repository.SettingsRepository;
import pt.com.LBC.Vacation_System.repository.UserRepository;
import pt.com.LBC.Vacation_System.repository.VacationRepository;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {

  private final UserRepository userRepository;
  private final VacationRepository vacationRepository;
  private final SettingsRepository settingsRepository;
  private final PasswordEncoder passwordEncoder;

  public MaintenanceServiceImpl(UserRepository userRepository, VacationRepository vacationRepository,
      SettingsRepository settingsRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.vacationRepository = vacationRepository;
    this.settingsRepository = settingsRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional
  public void clean(User requester) {
    validateAdminAccess(requester);

    // Remove vínculos para evitar violação de FK no self-join manager
    userRepository.findAll().forEach(u -> {
      u.setManager(null);
      userRepository.save(u);
    });

    // Apaga dados em ordem segura
    vacationRepository.deleteAll();
    settingsRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Override
  @Transactional
  public void seed(User requester) {
    validateAdminAccess(requester);

    // Admin
    User admin = new User();
    admin.setName("Iago Admin");
    admin.setEmail("iago.admin@taskflow.pt");
    admin.setRole(Role.ADMIN);
    admin.setActive(true);
    admin.setBalanceDays(22);
    admin.setPasswordHash(passwordEncoder.encode("123456"));
    admin = userRepository.save(admin);

    // Gestores
    User gestor1 = new User();
    gestor1.setName("Iago Gestor");
    gestor1.setEmail("iago.gestor@taskflow.pt");
    gestor1.setRole(Role.MANAGER);
    gestor1.setActive(true);
    gestor1.setBalanceDays(22);
    gestor1.setPasswordHash(passwordEncoder.encode("123456"));
    gestor1 = userRepository.save(gestor1);

    // Colaboradores
    User col1 = new User();
    col1.setName("Iago Colab 1");
    col1.setEmail("iago.colab1@taskflow.pt");
    col1.setRole(Role.COLLABORATOR);
    col1.setActive(true);
    col1.setBalanceDays(22);
    col1.setPasswordHash(passwordEncoder.encode("123456"));
    col1.setManager(gestor1);
    userRepository.save(col1);
  }

  private void validateAdminAccess(User requester) {
    if (requester == null) {
      throw new UnauthorizedException("Não autenticado");
    }
    if (requester.getRole() != Role.ADMIN) {
      throw new ForbiddenException("Apenas admins podem executar esta operação");
    }
  }
}
