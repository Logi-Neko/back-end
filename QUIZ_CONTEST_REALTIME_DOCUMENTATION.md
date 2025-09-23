# Quiz Contest Real-time System Documentation

## Tổng quan
Hệ thống Quiz Contest Real-time sử dụng Kafka và WebSocket để xử lý các cuộc thi quiz theo thời gian thực. Hệ thống bao gồm:

- **Kafka**: Xử lý events và message queuing
- **WebSocket**: Giao tiếp real-time với clients
- **Spring Boot**: Backend framework
- **Redis**: Caching và session management

## Kiến trúc hệ thống

### Kafka Topics
1. **question.revealed** - Khi một câu hỏi được hiển thị
2. **answer.submitted** - Khi người chơi nộp câu trả lời
3. **score.updated** - Khi điểm số được cập nhật
4. **contest.lifecycle** - Các sự kiện vòng đời của contest (start, end, join, leave)
5. **leaderboard.updated** - Khi bảng xếp hạng được cập nhật

### WebSocket Destinations
- **Public Topics**: `/topic/contest.{contestId}`
- **User-specific Queues**: `/queue/contest.{contestId}`

## API Endpoints

### Game Management API

#### 1. Bắt đầu Contest
```http
POST /api/game/{contestId}/start
```
**Response**: `200 OK` - "Contest started"

#### 2. Hiển thị Câu hỏi
```http
POST /api/game/{contestId}/reveal/{contestQuestionId}
```
**Response**: `200 OK` - "Question revealed successfully"

#### 3. Nộp Câu trả lời
```http
POST /api/game/{contestId}/submit
Content-Type: application/json

{
  "eventType": "answer.submitted",
  "contestId": 1,
  "contestQuestionId": 1,
  "participantId": 1,
  "answerOptionId": 1,
  "answeredAt": "2024-01-01T10:00:00Z",
  "timeTakenMs": 5000,
  "submissionUuid": "unique-uuid"
}
```

#### 4. Kết thúc Contest
```http
POST /api/game/{contestId}/end
```
**Response**: `200 OK` - "Contest ended successfully"

#### 5. Tham gia Contest
```http
POST /api/game/{contestId}/join/{participantId}
```
**Response**: `200 OK` - "Participant joined successfully"

#### 6. Rời Contest
```http
POST /api/game/{contestId}/leave/{participantId}
```
**Response**: `200 OK` - "Participant left successfully"

#### 7. Lấy Bảng xếp hạng
```http
GET /api/game/{contestId}/leaderboard
```
**Response**: `200 OK` - Leaderboard data

## WebSocket Events

### Client → Server Messages

#### 1. Tham gia Contest
```javascript
// Gửi message
stompClient.send("/app/contest.join", {}, JSON.stringify({
  contestId: 1,
  participantId: 1
}));

// Nhận confirmation
// Destination: /user/{participantId}/queue/contest.1
{
  "type": "join.confirmed",
  "contestId": 1,
  "participantId": 1,
  "timestamp": 1640995200000
}
```

#### 2. Rời Contest
```javascript
// Gửi message
stompClient.send("/app/contest.leave", {}, JSON.stringify({
  contestId: 1,
  participantId: 1
}));

// Nhận confirmation
// Destination: /user/{participantId}/queue/contest.1
{
  "type": "leave.confirmed",
  "contestId": 1,
  "participantId": 1,
  "timestamp": 1640995200000
}
```

#### 3. Nộp Câu trả lời
```javascript
// Gửi message
stompClient.send("/app/answer.submit", {}, JSON.stringify({
  contestId: 1,
  participantId: 1,
  contestQuestionId: 1,
  answerOptionId: 1,
  submissionId: "unique-uuid",
  timeTakenMs: 5000
}));

// Nhận confirmation
// Destination: /user/{participantId}/queue/contest.1
{
  "type": "answer.received",
  "contestId": 1,
  "participantId": 1,
  "submissionId": "unique-uuid",
  "timestamp": 1640995200000
}
```

### Server → Client Messages

#### 1. Câu hỏi được hiển thị
```javascript
// Destination: /topic/contest.1
{
  "eventType": "question.revealed",
  "contestId": 1,
  "contestQuestionId": 1,
  "orderIndex": 1,
  "question": {
    "id": 1,
    "questionText": "What is 2+2?",
    "answerOptions": [
      {"id": 1, "text": "3"},
      {"id": 2, "text": "4"},
      {"id": 3, "text": "5"},
      {"id": 4, "text": "6"}
    ],
    "points": 1000,
    "timeLimit": 30
  },
  "timestamp": "2024-01-01T10:00:00Z"
}
```

#### 2. Điểm số được cập nhật
```javascript
// Destination: /topic/contest.1
{
  "eventType": "score.updated",
  "contestId": 1,
  "participantId": 1,
  "newScore": 950,
  "rank": 1,
  "timestamp": "2024-01-01T10:00:05Z"
}
```

#### 3. Bảng xếp hạng được cập nhật
```javascript
// Destination: /topic/contest.1
{
  "eventType": "leaderboard.updated",
  "contestId": 1,
  "leaderboard": [
    {"participantId": 1, "score": 950, "rank": 1},
    {"participantId": 2, "score": 800, "rank": 2},
    {"participantId": 3, "score": 750, "rank": 3}
  ],
  "timestamp": "2024-01-01T10:00:05Z"
}
```

#### 4. Sự kiện vòng đời Contest
```javascript
// Destination: /topic/contest.1
{
  "eventType": "contest.started", // hoặc "contest.ended", "participant.joined", "participant.left"
  "contestId": 1,
  "participantId": null, // null cho contest events
  "timestamp": "2024-01-01T10:00:00Z"
}
```

## Cấu hình Kafka

### Producer Configuration
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      acks: all
      enable-idempotence: true
```

### Consumer Configuration
```yaml
spring:
  kafka:
    consumer:
      group-id: quiz-service
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      auto-offset-reset: latest
      enable-auto-commit: true
```

## WebSocket Configuration

### Endpoint
```
ws://localhost:8080/ws
```

### STOMP Configuration
```javascript
const stompClient = new StompJs.Client({
  brokerURL: 'ws://localhost:8080/ws',
  connectHeaders: {},
  debug: function (str) {
    console.log(str);
  },
  reconnectDelay: 5000,
  heartbeatIncoming: 4000,
  heartbeatOutgoing: 4000,
});
```

## Error Handling

### Kafka Error Handling
- **Retry Policy**: 3 lần retry với backoff 1 giây
- **Dead Letter Queue**: Messages thất bại sau 3 lần retry
- **Idempotency**: Đảm bảo không xử lý duplicate messages

### WebSocket Error Handling
- **Connection Recovery**: Tự động reconnect khi mất kết nối
- **Message Validation**: Validate message format trước khi xử lý
- **Error Responses**: Gửi error messages về client khi có lỗi

## Performance Considerations

### Kafka
- **Partitioning**: Sử dụng contestId làm partition key
- **Batch Processing**: Xử lý messages theo batch
- **Compression**: Enable compression cho messages

### WebSocket
- **Connection Pooling**: Quản lý connection pool hiệu quả
- **Message Batching**: Batch multiple events khi có thể
- **Heartbeat**: Sử dụng heartbeat để maintain connection

## Monitoring và Logging

### Metrics
- Message throughput per topic
- WebSocket connection count
- Response time cho API endpoints
- Error rates

### Logging
- Tất cả events được log với level INFO
- Error events được log với level ERROR
- Performance metrics được log với level DEBUG

## Security

### Authentication
- Sử dụng JWT token cho API authentication
- WebSocket authentication qua query parameters

### Authorization
- Kiểm tra quyền truy cập contest
- Validate participant permissions

## Deployment

### Docker
```yaml
version: '3.8'
services:
  kafka:
    image: confluentinc/cp-kafka:latest
    environment:
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
  
  redis:
    image: redis:alpine
    
  app:
    build: .
    depends_on:
      - kafka
      - redis
    environment:
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
      SPRING_REDIS_HOST: redis
```

### Environment Variables
```bash
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379
```

## Testing

### Unit Tests
- Test tất cả service methods
- Test Kafka producers và consumers
- Test WebSocket message handling

### Integration Tests
- Test end-to-end flow
- Test với real Kafka và Redis
- Test WebSocket connections

### Load Testing
- Test với nhiều concurrent users
- Test message throughput
- Test WebSocket connection limits
