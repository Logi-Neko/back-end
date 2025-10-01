package exe2.learningapp.logineko.authentication.service;

import exe2.learningapp.logineko.authentication.dtos.friend.FriendDto;

import java.util.List;

public interface FriendShipService {
    void sendFriendRequest(Long toAccountId);
    void acceptFriendRequest(Long friendRequestId);
    void declineFriendRequest(Long friendRequestId);
    void removeFriend(Long friendRequestId);
    List<FriendDto> getFriendsList();
    List<FriendDto> getPendingRequests();
}
