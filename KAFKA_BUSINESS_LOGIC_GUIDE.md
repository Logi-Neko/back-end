# Hệ thống Kafka Real-time với Business Logic Hoàn chỉnh

## Tổng quan

Hệ thống đã được hoàn thiện với business logic thực tế cho tất cả các processor, tích hợp đầy đủ với các service layer và database operations.

## Kiến trúc Business Logic

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   GameController│───▶│   EventProducer │───▶│     Kafka       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                       │
                                                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   WebSocket     │◀───│  EventsConsumer │◀───│     Kafka       │
│   Clients       │    └─────────────────┘    └─────────────────┘
└─────────────────┘           │
                              ▼
                    ┌─────────────────┐
                    │   Processors    │
                    │ - GameProcessor │◀─── Database Services
                    │ - Leaderboard   │◀─── Database Services  
                    │ - Participant   │◀─── Database Services
                    │ - ContestState  │◀─── Database Services
                    └─────────────────┘
```

## Business Logic Chi tiết

### 1. GameProcessor
**File**: `src/main/java/exe2/learningapp/logineko/quizziz/service/kafka/processor/GameProcessor.java`

#### Chức năng chính:
- **Question Revealed**: Lấy thông tin câu hỏi từ database và cập nhật event
- **Answer Submitted**: Xử lý câu trả lời, tính điểm, lưu database
- **Score Calculation**: Tính điểm dựa trên độ chính xác và thời gian trả lời

#### Business Logic:
```java
// Question Revealed
- Lấy ContestQuestion từ ContestQuestionService
- Lấy Question details từ QuestionService  
- Cập nhật event với thông tin thực tế

// Answer Submitted
- Kiểm tra duplicate submission bằng UUID
- Tính điểm dựa trên độ chính xác
- Lưu answer vào database với score
- Phát score updated event

// Score Calculation
- Base score: 10 điểm cho câu trả lời đúng
- Bonus: +5 điểm nếu trả lời dưới 5 giây
- Tính thời gian trả lời từ question reveal time
```

### 2. ParticipantProcessor
**File**: `src/main/java/exe2/learningapp/logineko/quizziz/service/kafka/processor/ParticipantProcessor.java`

#### Chức năng chính:
- **Participant Created**: Tạo participant mới và khởi tạo leaderboard
- **Score Update**: Cập nhật điểm số participant trong database

#### Business Logic:
```java
// Participant Created
- Kiểm tra participant đã tồn tại chưa
- Tạo participant mới trong database
- Khởi tạo leaderboard với điểm 0
- Log thông tin tạo thành công

// Score Update
- Cập nhật điểm số participant
- Log thông tin cập nhật
```

### 3. ContestStateManager
**File**: `src/main/java/exe2/learningapp/logineko/quizziz/service/kafka/processor/ContestStateManager.java`

#### Chức năng chính:
- **Contest Lifecycle Management**: Quản lý trạng thái contest
- **State Validation**: Kiểm tra tính hợp lệ của các thao tác
- **Database Integration**: Đồng bộ với database

#### Business Logic:
```java
// Contest Created
- Kiểm tra contest đã tồn tại trong database
- Tạo state mới với status "created"
- Log thông tin tạo contest

// Contest Started  
- Validate contest tồn tại trong database
- Kiểm tra contest chưa được start
- Cập nhật state thành "started"
- Gọi ContestService.startContest()
- Khởi tạo leaderboard

// Contest Ended
- Validate contest tồn tại trong database
- Kiểm tra contest chưa được end
- Cập nhật state thành "ended"
- Gọi ContestService.endContest()
- Finalize leaderboard
```

### 4. LeaderboardProcessor
**File**: `src/main/java/exe2/learningapp/logineko/quizziz/service/kafka/processor/LeaderboardProcessor.java`

#### Chức năng chính:
- **Score Update**: Cập nhật leaderboard khi có điểm số mới
- **Rank Calculation**: Tính toán thứ hạng participant
- **Real-time Broadcasting**: Phát sóng leaderboard qua WebSocket

#### Business Logic:
```java
// Score Updated
- Tính score delta (chênh lệch với điểm cũ)
- Cập nhật leaderboard với delta
- Tính rank mới cho participant
- Lấy leaderboard đã cập nhật
- Phát sóng qua Kafka và WebSocket

// Leaderboard Refresh
- Lấy leaderboard hiện tại
- Phát sóng leaderboard update event
- Broadcast qua multiple WebSocket topics

// Contest Started/Ended
- Khởi tạo/finalize leaderboard
- Phát sóng kết quả cuối cùng
```

## Database Integration

### Services được tích hợp:
- **ContestService**: Quản lý contest lifecycle
- **ContestQuestionService**: Quản lý câu hỏi trong contest
- **QuestionService**: Quản lý thông tin câu hỏi
- **AnswerService**: Lưu trữ câu trả lời
- **ParticipantService**: Quản lý participant
- **LeaderBoardService**: Quản lý bảng xếp hạng

### Database Operations:
```java
// Contest Operations
contestService.findById(contestId)
contestService.startContest(contestId)
contestService.endContest(contestId)

// Question Operations  
contestQuestionService.findById(contestQuestionId)
questionService.findById(questionId)

// Answer Operations
answerService.existsBySubmissionUuid(uuid)
answerService.saveFromEvent(uuid, participantId, questionId, optionId, isCorrect, score, time)

// Participant Operations
participantService.findById(participantId)
participantService.createParticipant(contestId, accountId)
participantService.incrementScore(participantId, delta)

// Leaderboard Operations
leaderBoardService.updateScore(contestId, participantId, delta)
leaderBoardService.getLeaderboard(contestId)
leaderBoardService.computeRank(contestId, participantId)
leaderBoardService.initializeLeaderboard(contestId)
leaderBoardService.finalizeLeaderboard(contestId)
```

## Event Flow với Business Logic

### 1. Contest Creation Flow
```
POST /api/game?contestId=123
    ↓
EventProducer.publishContestCreated(123)
    ↓
Kafka: game-events
    ↓
EventsConsumer.consumeGameEvent()
    ↓
ContestStateManager.handleContestCreated()
    ↓
- Kiểm tra contest trong database
- Tạo state mới
- Log thông tin
    ↓
WebSocket: /topic/contest.123
```

### 2. Participant Join Flow
```
POST /api/game/123/join?participantId=1&name=Player1
    ↓
EventProducer.publishParticipantCreated(123, 1, "Player1")
    ↓
Kafka: game-events
    ↓
EventsConsumer.consumeGameEvent()
    ↓
ParticipantProcessor.handleParticipantCreated()
    ↓
- Kiểm tra participant đã tồn tại
- participantService.createParticipant()
- leaderBoardService.updateScore() với điểm 0
- Log thông tin
    ↓
WebSocket: /topic/contest.123
```

### 3. Question Reveal Flow
```
POST /api/game/123/reveal/456?orderIndex=1&question=What is 2+2?
    ↓
EventProducer.publishQuestionRevealed(123, 456, 1, "What is 2+2?")
    ↓
Kafka: game-events
    ↓
EventsConsumer.consumeGameEvent()
    ↓
GameProcessor.handleQuestionRevealed()
    ↓
- contestQuestionService.findById(456)
- questionService.findById(questionId)
- Cập nhật event với thông tin thực tế
    ↓
WebSocket: /topic/contest.123
WebSocket: /topic/contest.123.question.456
```

### 4. Answer Submission Flow
```
POST /api/game/123/submit/1/456?answer=4
    ↓
EventProducer.publishAnswerSubmitted(123, 1, 456, "4")
    ↓
Kafka: game-events
    ↓
EventsConsumer.consumeGameEvent()
    ↓
GameProcessor.handleAnswerSubmitted()
    ↓
- Kiểm tra duplicate submission
- calculateScore() - tính điểm
- answerService.saveFromEvent() - lưu database
- publishScoreUpdate() - phát score event
    ↓
Kafka: game-events (score.updated)
    ↓
LeaderboardProcessor.handleScoreUpdated()
    ↓
- Tính score delta
- leaderBoardService.updateScore()
- leaderBoardService.computeRank()
- Phát leaderboard update
    ↓
WebSocket: /topic/contest.123.leaderboard
WebSocket: /topic/contest.123
```

## Error Handling & Validation

### 1. Duplicate Prevention
```java
// Answer Submission
Long submissionUuid = generateSubmissionUuid(answerEvent);
if (answerService.existsBySubmissionUuid(submissionUuid)) {
    log.warn("Duplicate answer submission detected");
    return;
}

// Participant Creation
var existingParticipant = participantService.findById(participantId);
if (existingParticipant.isPresent()) {
    log.warn("Participant already exists");
    return;
}
```

### 2. State Validation
```java
// Contest Start
var contest = contestService.findById(contestId);
if (contest.isEmpty()) {
    log.error("Cannot start contest - not found in database");
    return;
}

if ("started".equals(state.getStatus())) {
    log.warn("Contest is already started");
    return;
}
```

### 3. Data Validation
```java
// Question Reveal
var contestQuestion = contestQuestionService.findById(contestQuestionId);
if (contestQuestion.isEmpty()) {
    log.warn("Contest question not found");
    return;
}

var question = questionService.findById(questionId);
if (question == null) {
    log.warn("Question not found");
    return;
}
```

## Performance Optimizations

### 1. Async Processing
- Tất cả Kafka operations đều async
- CompletableFuture cho event publishing
- Non-blocking WebSocket broadcasting

### 2. Database Efficiency
- Sử dụng delta updates cho score
- Batch operations cho leaderboard
- Efficient queries với proper indexing

### 3. Memory Management
- In-memory state management cho contest states
- Efficient event routing
- Proper resource cleanup

## Monitoring & Logging

### 1. Comprehensive Logging
```java
// Event Processing
log.info("Processing {} for contest {}", eventType, contestId);

// Business Logic
log.info("Participant {} created for contest {} with name: {}", 
    participantId, contestId, name);

// Error Handling
log.error("Error processing event: {}", e.getMessage(), e);
```

### 2. Performance Metrics
- Event processing time
- Database operation time
- WebSocket broadcast time
- Score calculation time

## Testing Strategy

### 1. Unit Tests
- Test individual processor methods
- Mock service dependencies
- Validate business logic

### 2. Integration Tests
- Test full event flow
- Database integration
- WebSocket broadcasting

### 3. Load Tests
- High-throughput event processing
- Multiple concurrent contests
- WebSocket connection limits

## Deployment Considerations

### 1. Kafka Configuration
- Proper partitioning strategy
- Consumer group configuration
- Retention policies

### 2. Database Configuration
- Connection pooling
- Transaction management
- Index optimization

### 3. WebSocket Configuration
- Connection limits
- Message size limits
- Heartbeat configuration

## Kết luận

Hệ thống đã được hoàn thiện với:
- ✅ **Complete Business Logic**: Tất cả processors có logic thực tế
- ✅ **Database Integration**: Tích hợp đầy đủ với các service layer
- ✅ **Error Handling**: Xử lý lỗi và validation toàn diện
- ✅ **Performance Optimization**: Async processing và efficient operations
- ✅ **Real-time Updates**: WebSocket broadcasting cho tất cả events
- ✅ **Comprehensive Logging**: Logging chi tiết cho monitoring
- ✅ **Scalable Architecture**: Event-driven design cho high throughput

Hệ thống sẵn sàng cho production với đầy đủ business logic và database operations!
