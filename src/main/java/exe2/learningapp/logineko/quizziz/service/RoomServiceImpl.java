package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.RoomDTO;
import exe2.learningapp.logineko.quizziz.entity.Room;
import exe2.learningapp.logineko.quizziz.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService{
    private final RoomRepository roomRepository;


    @Override
    public RoomDTO.RoomResponse create(RoomDTO.CreateRoomRequest create) {
        Room room = new Room();
        room.setTitle(create.title());
        room.setDescription(create.description());
        room.setPublic(true);

        String generatedCode;
        do {
            generatedCode = generateUniqueRoomCode();
        } while (roomRepository.findByCode(generatedCode).isPresent());

        room.setCode(generatedCode);


        Room saved = roomRepository.save(room);

        return new RoomDTO.RoomResponse(
                saved.getRoomId(),
                saved.getCode(),
                saved.getTitle(),
                saved.getDescription(),
                saved.isPublic()
        );
        }

    @Override
    public RoomDTO.UpdateRoom update(Long id,RoomDTO.UpdateRoom update) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        room.setTitle(update.title());
        room.setDescription(update.description());
        room.setPublic(update.isPublic());

        Room saved = roomRepository.save(room);

        return new RoomDTO.UpdateRoom(
                saved.getTitle(),
                saved.getDescription(),
                saved.isPublic()
        );


    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Optional<RoomDTO.RoomResponse> findById(Long id) {
        return roomRepository.findById(id)
                .map(r -> new RoomDTO.RoomResponse(
                        r.getRoomId(),
                        r.getCode(),
                        r.getTitle(),
                        r.getDescription(),
                        r.isPublic()
                ));
    }

    @Override
    public Page<RoomDTO.RoomResponse> findAll(String keyword, Pageable pageable) {
        Page<Room> rooms;
        if (keyword == null || keyword.isBlank()) {
            rooms = roomRepository.findAll(pageable);
        } else {
            rooms = roomRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    keyword, keyword, pageable
            );
        }

        return rooms.map(r -> new RoomDTO.RoomResponse(
                r.getRoomId(),
                r.getCode(),
                r.getTitle(),
                r.getDescription(),
                r.isPublic()
        ));
    }

    private String generateUniqueRoomCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder codeBuilder = new StringBuilder();

        codeBuilder.append(characters.charAt(new Random().nextInt(characters.length())));
        codeBuilder.append(characters.charAt(new Random().nextInt(characters.length())));


        String numbers = "0123456789";
        for (int i = 0; i < 4; i++) {
            codeBuilder.append(numbers.charAt(new Random().nextInt(numbers.length())));
        }

        return codeBuilder.toString();
    }


}
