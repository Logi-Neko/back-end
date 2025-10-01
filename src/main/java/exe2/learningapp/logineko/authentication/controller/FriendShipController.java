package exe2.learningapp.logineko.authentication.controller;

import exe2.learningapp.logineko.authentication.dtos.friend.FriendDto;
import exe2.learningapp.logineko.authentication.service.FriendShipService;
import exe2.learningapp.logineko.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/friendship")
@RequiredArgsConstructor
@Tag(name = "FriendShip Controller", description = "APIs for managing friendships and friend requests")
public class FriendShipController {

    private final FriendShipService friendShipService;

    @PostMapping("/send-request/{toAccountId}")
    @Operation(summary = "Send friend request", description = "Send a friend request to another user")
    public ResponseEntity<ApiResponse<String>> sendFriendRequest(
            @Parameter(description = "ID of the user to send friend request to")
            @PathVariable Long toAccountId) {

        log.info("Sending friend request to account ID: {}", toAccountId);
        friendShipService.sendFriendRequest(toAccountId);
        return ResponseEntity.ok(ApiResponse.success("Đã gửi lời mời kết bạn thành công"));
    }

    @PostMapping("/accept/{friendRequestId}")
    @Operation(summary = "Accept friend request", description = "Accept a pending friend request")
    public ResponseEntity<ApiResponse<String>> acceptFriendRequest(
            @Parameter(description = "ID of the friend request to accept")
            @PathVariable Long friendRequestId) {

        log.info("Accepting friend request ID: {}", friendRequestId);
        friendShipService.acceptFriendRequest(friendRequestId);
        return ResponseEntity.ok(ApiResponse.success("Đã chấp nhận lời mời kết bạn"));
    }

    @PostMapping("/decline/{friendRequestId}")
    @Operation(summary = "Decline friend request", description = "Decline a pending friend request")
    public ResponseEntity<ApiResponse<String>> declineFriendRequest(
            @Parameter(description = "ID of the friend request to decline")
            @PathVariable Long friendRequestId) {

        log.info("Declining friend request ID: {}", friendRequestId);
        friendShipService.declineFriendRequest(friendRequestId);
        return ResponseEntity.ok(ApiResponse.success("Đã từ chối lời mời kết bạn"));
    }

    @DeleteMapping("/remove/{friendRequestId}")
    @Operation(summary = "Remove friend", description = "Remove an existing friend from friend list")
    public ResponseEntity<ApiResponse<String>> removeFriend(
            @Parameter(description = "ID of the friendship to remove")
            @PathVariable Long friendRequestId) {

        log.info("Removing friend with request ID: {}", friendRequestId);
        friendShipService.removeFriend(friendRequestId);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa bạn bè thành công"));
    }

    @GetMapping("/friends")
    @Operation(summary = "Get friends list", description = "Get list of all accepted friends")
    public ResponseEntity<ApiResponse<List<FriendDto>>> getFriendsList() {

        log.info("Getting friends list for current user");
        List<FriendDto> friends = friendShipService.getFriendsList();
        return ResponseEntity.ok(ApiResponse.success(friends, "Danh sách bạn bè đã được lấy thành công"));
    }

    @GetMapping("/pending-requests")
    @Operation(summary = "Get pending friend requests", description = "Get list of all pending friend requests received by current user")
    public ResponseEntity<ApiResponse<List<FriendDto>>> getPendingRequests() {

        log.info("Getting pending friend requests for current user");
        List<FriendDto> pendingRequests = friendShipService.getPendingRequests();
        return ResponseEntity.ok(ApiResponse.success(pendingRequests, "Danh sách lời mời kết bạn đang chờ đã được lấy thành công"));
    }
}
