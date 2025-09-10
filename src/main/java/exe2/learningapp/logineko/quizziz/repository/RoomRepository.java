package exe2.learningapp.logineko.quizziz.repository;

import exe2.learningapp.logineko.quizziz.dto.RoomDTO;
import exe2.learningapp.logineko.quizziz.entity.Room;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Page<Room> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String title, String description, Pageable pageable
    );

    Optional<Room> findByCode(String code);
}
