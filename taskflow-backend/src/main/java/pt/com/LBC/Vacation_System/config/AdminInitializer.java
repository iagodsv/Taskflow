package pt.com.LBC.Vacation_System.config;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import pt.com.LBC.Vacation_System.model.Role;
import pt.com.LBC.Vacation_System.model.User;
import pt.com.LBC.Vacation_System.repository.UserRepository;

@Component
public class AdminInitializer implements ApplicationRunner {

  private static final org.slf4j.Logger log = LoggerFactory.getLogger(AdminInitializer.class);

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${app.admin.email:iago.admin@taskflow.pt}")
  private String adminEmail;

  @Value("${app.admin.password:123456}")
  private String adminPassword;

  @Value("${app.admin.name:Administrador}")
  private String adminName;

  public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(ApplicationArguments args) {
    long admins = userRepository.countByRole(Role.ADMIN);
    if (admins > 0) {
      log.info("AdminInitializer: já existe ADMIN cadastrado. Nenhuma ação necessária.");
      return;
    }

    // Caso não exista nenhum ADMIN, cria o usuário inicial com as credenciais
    // configuradas
    User admin = new User();
    admin.setName(adminName);
    admin.setEmail(adminEmail);
    admin.setRole(Role.ADMIN);
    admin.setActive(true);
    admin.setPasswordHash(passwordEncoder.encode(adminPassword));

    userRepository.save(admin);
    log.warn("AdminInitializer: usuário ADMIN inicial criado -> {} (altere a senha assim que possível)", adminEmail);
  }
}
