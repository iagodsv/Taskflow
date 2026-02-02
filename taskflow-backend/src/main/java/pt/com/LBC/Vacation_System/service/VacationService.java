package pt.com.LBC.Vacation_System.service;

import java.util.List;

import pt.com.LBC.Vacation_System.dto.VacationDTO;
import pt.com.LBC.Vacation_System.model.User;
import pt.com.LBC.Vacation_System.model.VacationRequest;

public interface VacationService {
  List<VacationRequest> list(User user);

  VacationRequest create(User user, VacationDTO dto);

  void approve(Long id, User user);

  void reject(Long id, User user);
}
