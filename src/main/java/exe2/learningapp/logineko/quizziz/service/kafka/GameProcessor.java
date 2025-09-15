package exe2.learningapp.logineko.quizziz.service.kafka;

import exe2.learningapp.logineko.quizziz.dto.GameEventDTO;
import exe2.learningapp.logineko.quizziz.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameProcessor {
    private final AnswerService answerService; // your service for persisting answers
    private final ParticipantService participantService;
    private final ContestQuestionService contestQuestionService;
    private final AnswerOptionService answerOptionService;
    private final QuestionService questionService;
    private final LeaderBoardService leaderboardService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "answer.submitted", groupId = "game-service")
    @Transactional
    public void onAnswerSubmitted(GameEventDTO.AnswerSubmittedEvent ev) {
        Long contestId = ev.contestId();
        if (answerService.existsBySubmissionUuid(ev.submissionUuid())) return;

        // Idempotency: check whether submission already processed.
        // For demo: check via answerService.existsBySubmissionUuid(submissionUuid)
//        if (answerService.existsBySubmissionUuid(submissionUuid)) {
//            return; // already processed
//        }

        // Load ContestQuestion & selected option via services
        var contestQuestion = contestQuestionService.findById(ev.contestQuestionId())
                .orElseThrow(() -> new RuntimeException("ContestQuestion not found"));
        var participant = participantService.findById(ev.participantId())
                .orElseThrow(() -> new RuntimeException("Participant not found"));
        var selectedOption = answerOptionService.findById(ev.answerOptionId())
                .orElseThrow(() -> new RuntimeException("AnswerOption not found"));
        boolean isCorrect = selectedOption.isCorrect() != null && selectedOption.isCorrect();
        // scoring logic: e.g. base points from Question.points and speed bonus
        var question = questionService.findById(contestQuestion.questionId());
        int base = question.points() != null ? question.points() : 1000;
        int timeSeconds = (int) (ev.timeTakenMs() / 1000);
        int score = isCorrect ? Math.max(0, base - timeSeconds * 10) : 0;

        // persist answer (make answerService produce Answer and mark processed)
        answerService.saveFromEvent(ev.submissionUuid(), participant.id(), contestQuestion.id(), selectedOption.id(), isCorrect, score, timeSeconds);

        // update participant total score (participantService should be transactional & idempotent)
        int newTotal = participantService.incrementScore(participant.id(), score);

        // compute simple rank (or you could compute via leaderboard query)
     //   int rank = participantService.computeRank(participant.getContest().getContestId(), participant.getId());
        int rank = leaderboardService.computeRank(contestId, participant.id());

        // publish score.updated
        GameEventDTO.ScoreUpdatedEvent scoreEv = new GameEventDTO.ScoreUpdatedEvent("score.updated", contestId, participant.id(), newTotal,rank, java.time.Instant.now());
        kafkaTemplate.send("score.updated", String.valueOf(contestId), scoreEv);
    }
}

