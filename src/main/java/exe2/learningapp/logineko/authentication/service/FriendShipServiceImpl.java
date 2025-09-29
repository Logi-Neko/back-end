package exe2.learningapp.logineko.authentication.service;

import exe2.learningapp.logineko.authentication.component.CurrentUserProvider;
import exe2.learningapp.logineko.authentication.dtos.friend.FriendDto;
import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.authentication.entity.FriendShip;
import exe2.learningapp.logineko.authentication.mapper.FriendMapper;
import exe2.learningapp.logineko.authentication.repository.AccountRepository;
import exe2.learningapp.logineko.authentication.repository.FriendShipRepository;
import exe2.learningapp.logineko.common.exception.AppException;
import exe2.learningapp.logineko.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendShipServiceImpl implements FriendShipService {
    private final CurrentUserProvider currentUserProvider;
    private final FriendShipRepository friendShipRepository;
    private final AccountRepository accountRepository;
    private final FriendMapper friendMapper;
    private final WebSocketNotificationService webSocketNotificationService;

    @Override
    @Transactional
    public void sendFriendRequest(Long toAccountId) {
        try {
            Account currentUser = currentUserProvider.getCurrentUser();

            // Cannot send friend request to yourself
            if (currentUser.getId().equals(toAccountId)) {
                throw new AppException(ErrorCode.ERR_BAD_REQUEST);
            }

            Account toAccount = accountRepository.findById(toAccountId)
                    .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

            // Check if pending friendship already exists in either direction
            Optional<FriendShip> existingPendingFriendship = friendShipRepository
                    .findByFromAccountAndToAccountAndStatus(currentUser, toAccount, FriendShip.StatusFriendShip.PENDING);
            Optional<FriendShip> reversePendingFriendship = friendShipRepository
                    .findByFromAccountAndToAccountAndStatus(toAccount, currentUser, FriendShip.StatusFriendShip.PENDING);

            if (existingPendingFriendship.isPresent() || reversePendingFriendship.isPresent()) {
                throw new AppException(ErrorCode.ERR_EXISTS);
            }

            FriendShip friendRequest = FriendShip.builder()
                    .fromAccount(currentUser)
                    .toAccount(toAccount)
                    .status(FriendShip.StatusFriendShip.PENDING)
                    .build();

            friendShipRepository.save(friendRequest);

            // Send WebSocket notification
            webSocketNotificationService.sendFriendRequestNotification(
                toAccountId.toString(),
                currentUser.getId().toString(),
                currentUser.getUsername(),
                currentUser.getAvatarUrl()
            );

            log.info("Friend request sent from {} to {}", currentUser.getId(), toAccountId);

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error sending friend request: {}", e.getMessage());
            throw new AppException(ErrorCode.ERR_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void acceptFriendRequest(Long friendRequestId) {
        try {
            Account currentUser = currentUserProvider.getCurrentUser();

            FriendShip friendRequest = friendShipRepository.findById(friendRequestId)
                    .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

            // Verify that current user is the recipient of the friend request
            if (!friendRequest.getToAccount().getId().equals(currentUser.getId())) {
                throw new AppException(ErrorCode.ERR_FORBIDDEN);
            }

            // Check if request is still pending
            if (friendRequest.getStatus() != FriendShip.StatusFriendShip.PENDING) {
                throw new AppException(ErrorCode.ERR_BAD_REQUEST);
            }

            friendRequest.setStatus(FriendShip.StatusFriendShip.ACCEPTED);
            friendShipRepository.save(friendRequest);

            // Send WebSocket notification to the original sender
            log.info("Friend request {} accepted by {}", friendRequestId, currentUser.getId());

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error accepting friend request: {}", e.getMessage());
            throw new AppException(ErrorCode.ERR_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void declineFriendRequest(Long friendRequestId) {
        try {
            Account currentUser = currentUserProvider.getCurrentUser();

            FriendShip friendRequest = friendShipRepository.findById(friendRequestId)
                    .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

            // Verify that current user is the recipient of the friend request
            if (!friendRequest.getToAccount().getId().equals(currentUser.getId())) {
                throw new AppException(ErrorCode.ERR_FORBIDDEN);
            }

            // Check if request is still pending
            if (friendRequest.getStatus() != FriendShip.StatusFriendShip.PENDING) {
                throw new AppException(ErrorCode.ERR_BAD_REQUEST);
            }

            friendRequest.setStatus(FriendShip.StatusFriendShip.REJECTED);
            friendShipRepository.save(friendRequest);
            log.info("Friend request {} declined by {}", friendRequestId, currentUser.getId());

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error declining friend request: {}", e.getMessage());
            throw new AppException(ErrorCode.ERR_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void removeFriend(Long friendRequestId) {
        try {
            Account currentUser = currentUserProvider.getCurrentUser();

            FriendShip friendship = friendShipRepository.findById(friendRequestId)
                    .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

            // Verify that current user is part of this friendship
            if (!friendship.getFromAccount().getId().equals(currentUser.getId()) &&
                !friendship.getToAccount().getId().equals(currentUser.getId())) {
                throw new AppException(ErrorCode.ERR_FORBIDDEN);
            }

            // Check if friendship is accepted
            if (friendship.getStatus() != FriendShip.StatusFriendShip.ACCEPTED) {
                throw new AppException(ErrorCode.ERR_BAD_REQUEST);
            }

            friendship.setStatus(FriendShip.StatusFriendShip.UNFRIENDED);
            friendShipRepository.save(friendship);

            log.info("Friendship {} removed by {}", friendRequestId, currentUser.getId());

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error removing friend: {}", e.getMessage());
            throw new AppException(ErrorCode.ERR_SERVER_ERROR);
        }
    }

    @Override
    public List<FriendDto> getFriendsList() {
        try {
            Account currentUser = currentUserProvider.getCurrentUser();

            List<FriendShip> friendships = friendShipRepository
                    .findAcceptedFriendshipsByAccount(currentUser);

            List<FriendDto> friends = new ArrayList<>();

            for (FriendShip friendship : friendships) {
                // Determine which account is the friend (not the current user)
//                Account friendAccount = friendship.getFromAccount().getId().equals(currentUser.getId())
//                        ? friendship.getToAccount()
//                        : friendship.getFromAccount();
//
//                // Create a temporary friendship object to use the existing mapper method
//                FriendShip tempFriendship = FriendShip.builder()
//                        .id(friendship.getId())
//                        .fromAccount(friendAccount)
//                        .status(friendship.getStatus())
//                        .createdAt(friendship.getCreatedAt())
//                        .build();
                FriendDto friendDto = friendMapper.friendToFriendDto(friendship);
                friends.add(friendDto);
            }

            return friends;
        } catch (AppException e) {
            log.warn("An application exception occurred while getting friends list: {}", e.getErrorCode().getMessage());
            throw e; // Ném lại exception gốc với đúng ErrorCode
        } catch (Exception e) {
            log.error("Error getting friends list: {}", e.getMessage());
            throw new AppException(ErrorCode.ERR_SERVER_ERROR);
        }
    }

    @Override
    public List<FriendDto> getPendingRequests() {
        try {
            Account currentUser = currentUserProvider.getCurrentUser();

            // Get pending requests received by current user
            List<FriendShip> pendingRequests = friendShipRepository
                    .findByToAccountAndStatus(currentUser, FriendShip.StatusFriendShip.PENDING);

            List<FriendDto> requests = new ArrayList<>();

            for (FriendShip request : pendingRequests) {
                FriendDto requestDto = friendMapper.friendToFriendDto(request);
                requests.add(requestDto);
            }

            return requests;
        } catch (AppException e) {
            log.warn("An application exception occurred while getting friends list: {}", e.getErrorCode().getMessage());
            throw e; // Ném lại exception gốc với đúng ErrorCode
        } catch (Exception e) {

            log.error("Error getting pending requests: {}", e.getMessage());
            throw new AppException(ErrorCode.ERR_SERVER_ERROR);
        }
    }
}
