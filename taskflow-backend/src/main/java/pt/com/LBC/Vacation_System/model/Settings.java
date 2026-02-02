package pt.com.LBC.Vacation_System.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "settings")
@Getter
@Setter
public class Settings {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private int minLeadDays;
  private int minPeriodDays;
  private int maxPeriodDays;

  @ElementCollection
  @CollectionTable(name = "settings_blackout_days", joinColumns = @JoinColumn(name = "settings_id"))
  @Column(name = "value")
  private List<String> blackoutDays = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "settings_notification_emails", joinColumns = @JoinColumn(name = "settings_id"))
  @Column(name = "value")
  private List<String> notificationEmails = new ArrayList<>();

}
