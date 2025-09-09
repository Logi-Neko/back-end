package exe2.learningapp.logineko.authentication.repository;

import exe2.learningapp.logineko.authentication.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {

    List<Child> findByParentId(Long parentId);

    boolean existsByParentIdAndName(Long parentId, String name);

    List<Child> findByBirthDateBetween(LocalDate startDate, LocalDate endDate);

    List<Child> findByGender(String gender);

    List<Child> findByNameContainingIgnoreCase(String keyword);

    int deleteByParentId(Long parentId);

    long countByParentId(Long parentId);

    long countByGender(String gender);
}