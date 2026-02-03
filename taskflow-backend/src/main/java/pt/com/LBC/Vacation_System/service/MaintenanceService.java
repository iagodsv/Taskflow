package pt.com.LBC.Vacation_System.service;

import pt.com.LBC.Vacation_System.model.User;

public interface MaintenanceService {

  /**
   * Limpa todos os dados do sistema (vacations, settings, users)
   */
  void clean(User requester);

  /**
   * Popula dados de exemplo para testes
   */
  void seed(User requester);
}
