package pt.com.LBC.Vacation_System.service;

import java.util.List;

import pt.com.LBC.Vacation_System.dto.VacationDTO;
import pt.com.LBC.Vacation_System.dto.VacationResponseDTO;
import pt.com.LBC.Vacation_System.model.User;

public interface VacationService {

  /**
   * Lista pedidos de férias visíveis para o usuário autenticado
   */
  List<VacationResponseDTO> list(User requester);

  /**
   * Cria um novo pedido de férias
   */
  VacationResponseDTO create(User requester, VacationDTO dto);

  /**
   * Aprova um pedido de férias
   */
  VacationResponseDTO approve(Long id, User requester);

  /**
   * Rejeita um pedido de férias
   */
  VacationResponseDTO reject(Long id, User requester);
}
