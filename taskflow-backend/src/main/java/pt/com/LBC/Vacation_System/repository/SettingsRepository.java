package pt.com.LBC.Vacation_System.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pt.com.LBC.Vacation_System.model.Settings;

public interface SettingsRepository extends JpaRepository<Settings, Long> {
}
