package exe2.learningapp.logineko.quizziz.controller;

import exe2.learningapp.logineko.common.dto.ApiResponse;
import exe2.learningapp.logineko.common.dto.PaginatedResponse;
import exe2.learningapp.logineko.quizziz.dto.ContestDTO;
import exe2.learningapp.logineko.quizziz.dto.ContestHistoryDTO;
import exe2.learningapp.logineko.quizziz.dto.ParticipantDTO;
import exe2.learningapp.logineko.quizziz.entity.Participant;
import exe2.learningapp.logineko.quizziz.service.ContestService;
import exe2.learningapp.logineko.quizziz.service.ParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contest")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Contest Management", description = "API quản lý các Contest (Cuộc thi/Phòng thi)")
public class ContestController {

    private final ContestService contestService;
    private final ParticipantService participantService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Tạo một Contest mới",
            description = "Tạo một contest mới với tiêu đề và mô tả. Hệ thống sẽ tự động tạo một mã code duy nhất cho contest."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo contest thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Thông tin contest không hợp lệ")
    })
    public ApiResponse<ContestDTO.ContestResponse> createContest(
            @Valid @RequestBody ContestDTO.ContestRequest create) {
        log.info("Creating a new contest with title: {}", create.title());
        ContestDTO.ContestResponse contest = contestService.create(create);
        return ApiResponse.success(contest, "Tạo contest thành công");
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Cập nhật thông tin Contest",
            description = "Cập nhật tiêu đề, mô tả và trạng thái công khai của một contest."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật contest thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy contest"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Thông tin cập nhật không hợp lệ")
    })
    public ApiResponse<ContestDTO.UpdateRoom> updateContest(
            @Parameter(description = "ID của contest") @PathVariable Long id,
            @Valid @RequestBody ContestDTO.UpdateRoom update) {
        log.info("Updating contest with ID: {}", id);
        ContestDTO.UpdateRoom contest = contestService.update(id, update);
        return ApiResponse.success(contest, "Cập nhật contest thành công");
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Xóa một Contest",
            description = "Xóa hoàn toàn một contest khỏi hệ thống."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Xóa contest thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy contest")
    })
    public ApiResponse<Void> deleteContest(
            @Parameter(description = "ID của contest") @PathVariable Long id) {
        log.info("Deleting contest with ID: {}", id);
        contestService.delete(id);
        return ApiResponse.success(null, "Xóa contest thành công");
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thông tin Contest theo ID",
            description = "Lấy thông tin chi tiết của một contest cụ thể, bao gồm mã code contest."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy thông tin contest thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy contest")
    })
    public ApiResponse<ContestDTO.ContestResponse> getContestById(
            @Parameter(description = "ID của contest") @PathVariable Long id) {
        log.info("Getting contest by ID: {}", id);
        ContestDTO.ContestResponse contest = contestService.findById(id)
                .orElseThrow(() -> new RuntimeException("Contest not found"));
        return ApiResponse.success(contest, "Lấy thông tin contest thành công");
    }

    @GetMapping
    @Operation(
            summary = "Lấy danh sách tất cả các Contest",
            description = "Lấy danh sách tất cả các contest trong hệ thống với phân trang và tìm kiếm."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lấy danh sách contest thành công"
            )
    })
    public ApiResponse<PaginatedResponse<ContestDTO.ContestResponse>> getAllContests(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Getting all contests with keyword: {}", keyword);

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ContestDTO.ContestResponse> contests = contestService.findAll(keyword, pageable);

        PaginatedResponse<ContestDTO.ContestResponse> result = new PaginatedResponse<>(contests);

        return ApiResponse.success(result, "Lấy danh sách contest thành công");
    }

    @GetMapping("/participant")
    @Operation(
            summary = "Lấy danh sách nguời chơi của 1 contest",
description = "Lấy danh"  )
    public ApiResponse<List<ParticipantDTO.Participant>> getAllParticipantsInContest(
            @RequestParam Long contestId) {
        log.info("Getting all participants in contest with ID: {}", contestId);
        List<ParticipantDTO.Participant> participants = participantService.getParticipantsByContestId(contestId);
        return ApiResponse.success(participants, "Lấy danh sách người chơi thành công");
    }

    @PostMapping("/{id}/reward-top-5")
    @Operation(summary = "Thưởng cho top 5 người chơi")
    public ApiResponse<Void> rewardTopFive(@PathVariable Long id) {
        contestService.rewardTopFiveParticipants(id);
        return ApiResponse.success(null, "Đã trao thưởng thành công cho top 5 người chơi");
    }

    @GetMapping("/history/{accountId}")
    @Operation(summary = "Lấy lịch sử cuộc thi của một tài khoản")
    public ApiResponse<List<ContestHistoryDTO>> getContestHistory(@PathVariable Long accountId) {
        return ApiResponse.success(contestService.getContestHistory(accountId));
    }

}
