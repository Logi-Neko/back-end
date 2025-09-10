package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.RoomDTO;
import exe2.learningapp.logineko.quizziz.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface RoomService {
 RoomDTO.RoomResponse create(RoomDTO.CreateRoomRequest create);
 RoomDTO.UpdateRoom update(Long id,RoomDTO.UpdateRoom update);
 void delete(Long id);
 Optional<RoomDTO.RoomResponse> findById(Long id);
 Page<RoomDTO.RoomResponse> findAll(String keyword ,Pageable pageable);
}
