package pt.com.LBC.Vacation_System.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pt.com.LBC.Vacation_System.model.VacationRequest;

public interface VacationRepository extends JpaRepository<VacationRequest, Long> {

  @Query("""
        select case when count(v) > 0 then true else false end
        from VacationRequest v
        where (:start <= v.endDate and :end >= v.startDate)
          and v.status = 'APPROVED'
      """)
  boolean existsOverlapping(LocalDate start, LocalDate end);

  @Query("""
        select case when count(v) > 0 then true else false end
        from VacationRequest v
        where (:start <= v.endDate and :end >= v.startDate)
          and v.status = 'APPROVED'
          and v.collaborator.id = :collaboratorId
      """)
  boolean existsOverlappingForUser(LocalDate start, LocalDate end, Long collaboratorId);

  @Query("""
        select case when count(v) > 0 then true else false end
        from VacationRequest v
        where (:start <= v.endDate and :end >= v.startDate)
          and v.status <> 'REJECTED'
      """)
  boolean existsAnyOverlappingNotRejected(LocalDate start, LocalDate end);

  long countByCollaborator_Id(Long collaboratorId);
}
