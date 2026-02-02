package pt.com.LBC.Vacation_System.service;

import java.util.List;

import pt.com.LBC.Vacation_System.model.User;

public interface UserService {
  List<User> list(User user);

  User create(User user, User toCreate);

  User update(User user, Long id, User toUpdate);

  void delete(User user, Long id);

  User get(User user, Long id);
}
