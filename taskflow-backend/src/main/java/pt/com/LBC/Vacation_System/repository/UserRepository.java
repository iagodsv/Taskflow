package pt.com.LBC.Vacation_System.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pt.com.LBC.Vacation_System.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  long countByManager_Id(Long managerId);

}
