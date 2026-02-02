package pt.com.LBC.Vacation_System.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    /** Hash de senha (BCrypt). Pode ser nulo para login sem senha. */
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager;

    /** Saldo de dias de férias disponíveis. */
    private Integer balanceDays;

    /** Usuário ativo/inativo. */
    private Boolean active;
}
