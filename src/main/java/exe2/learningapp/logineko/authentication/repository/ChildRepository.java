package exe2.learningapp.logineko.authentication.repository;

import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.authentication.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {

}
