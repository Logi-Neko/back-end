package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.common.exception.AppException;
import exe2.learningapp.logineko.common.exception.ErrorCode;
import exe2.learningapp.logineko.quizziz.dto.QuizDTO;
import exe2.learningapp.logineko.quizziz.entity.Quiz;
import exe2.learningapp.logineko.quizziz.entity.Room;
import exe2.learningapp.logineko.quizziz.repository.QuizRepository;
import exe2.learningapp.logineko.quizziz.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;
    private final RoomRepository roomRepository;
    @Override
    public QuizDTO.Response createQuiz(QuizDTO.Request request) {
        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        Quiz quiz = Quiz
                .builder()
                .name(request.name())
                .description(request.description())
                .duration(request.duration())
                .totalQuestions(request.totalQuestions())
                .build();
        quizRepository.save(quiz);
        return QuizDTO.Response .builder()
                .id(quiz.getQuizId())
                .name(quiz.getName())
                .description(quiz.getDescription())
                .duration(quiz.getDuration())
                .totalQuestions(quiz.getTotalQuestions())
                .roomId(room.getId())
                .build();
    }

    @Override
    public Optional<QuizDTO.Response> findById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        return  Optional.of(QuizDTO.Response.builder()
                .id(quiz.getQuizId())
                .name(quiz.getName())
                .description(quiz.getDescription())
                .duration(quiz.getDuration())
                .totalQuestions(quiz.getTotalQuestions())
                .roomId(quiz.getRoom() != null ? quiz.getRoom().getId() : null)
                .build());
    }

    @Override
    public List<QuizDTO.Response> findByRoom(Long roomId) {
        List<Quiz> quizzes = quizRepository.findByRoomId(roomId);
        return quizzes.stream().map(quiz -> QuizDTO.Response.builder()
                .id(quiz.getQuizId())
                .name(quiz.getName())
                .description(quiz.getDescription())
                .duration(quiz.getDuration())
                .totalQuestions(quiz.getTotalQuestions())
                .roomId(quiz.getRoom() != null ? quiz.getRoom().getId() : null)
                .build()).toList();
    }

    @Override
    public void deleteQuiz(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new EntityNotFoundException("Quiz not found");
        }
        quizRepository.deleteById(id);
    }

    @Override
    public QuizDTO.Response updateQuiz(Long id, QuizDTO.Request request) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));
        quiz.setName(request.name());
        quiz.setDescription(request.description());
        quiz.setDuration(request.duration());
        quiz.setTotalQuestions(request.totalQuestions());
        quizRepository.save(quiz);
        return QuizDTO.Response.builder()
                .id(quiz.getQuizId())
                .name(quiz.getName())
                .description(quiz.getDescription())
                .duration(quiz.getDuration())
                .totalQuestions(quiz.getTotalQuestions())
                .roomId(quiz.getRoom() != null ? quiz.getRoom().getId() : null)
                .build();
    }

    @Override
    public QuizDTO.Response startQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));
        if("RUNNING".equalsIgnoreCase(quiz.getStatus().toString()))
            throw new IllegalStateException("Quiz is already running");
        if("CLOSED".equalsIgnoreCase(quiz.getStatus().toString()))
            throw new IllegalStateException("Quiz is already closed");
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(quiz.getDuration());
        quiz.setStartTime(start);
        quiz.setEndTime(end);
        quiz.setStatus(Quiz.Status.RUNNING);
        return QuizDTO.Response.builder()
                .id(quiz.getQuizId())
                .name(quiz.getName())
                .description(quiz.getDescription())
                .duration(quiz.getDuration())
                .totalQuestions(quiz.getTotalQuestions())
                .startTime(quiz.getStartTime())
                .endTime(quiz.getEndTime())
                .roomId(quiz.getRoom() != null ? quiz.getRoom().getId() : null)
                .build();
    }
}
