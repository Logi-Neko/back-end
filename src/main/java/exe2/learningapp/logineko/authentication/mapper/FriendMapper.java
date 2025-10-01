package exe2.learningapp.logineko.authentication.mapper;
import exe2.learningapp.logineko.authentication.dtos.friend.FriendDto;
import exe2.learningapp.logineko.authentication.entity.FriendShip;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FriendMapper {
    private final AccountMapper accountMapper;

    public FriendDto friendToFriendDto(FriendShip friend) {
        if (friend == null) {
            return null;
        }

        return FriendDto.builder()
                .id(friend.getId())
                .accountId(friend.getFromAccount().getId())
                .friendAccount(accountMapper.toAccountShowResponseDTO(friend.getFromAccount()))
                .status(friend.getStatus())
                .createdAt(friend.getCreatedAt())
                .build();
    }

//    public FriendShip friendDtoToFriend(FriendDto dto) {
//        if (dto == null) {
//            return null;
//        }
//
//        return FriendShip.builder()
//                .id(dto.id())
//                .fromAccount(accountMapper.toEntity(dto.accountId()))
//                .ToAccount(accountMapper.toEntity(dto.friendAccount().id()))
//                .status(dto.status())
//                .build();
//    }
}
