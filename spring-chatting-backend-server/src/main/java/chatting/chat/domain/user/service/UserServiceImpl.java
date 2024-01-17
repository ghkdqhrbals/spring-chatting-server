package chatting.chat.domain.user.service;

import chatting.chat.domain.friend.entity.Friend;
import chatting.chat.domain.participant.entity.Participant;
import chatting.chat.domain.room.dto.RoomDto;
import chatting.chat.domain.room.entity.Room;
import chatting.chat.domain.user.dto.UserDto;
import chatting.chat.domain.user.entity.User;
import chatting.chat.web.filter.UserContext;
import com.example.commondto.dto.friend.FriendResponse;
import com.example.commondto.dto.friend.FriendResponse.FriendDTO;
import chatting.chat.domain.friend.repository.FriendRepository;
import chatting.chat.domain.participant.repository.ParticipantRepository;
import chatting.chat.domain.room.repository.RoomRepository;
import chatting.chat.domain.user.repository.UserRepository;

import chatting.chat.web.kafka.dto.ChatRoomDTO;
import chatting.chat.web.kafka.dto.*;
import io.micrometer.core.annotation.Timed;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.commondto.error.ErrorCode.*;

import com.example.commondto.error.CustomException;
import com.example.commondto.error.ErrorCode;
import com.example.commondto.error.ErrorResponse;
import com.example.commondto.error.AppException;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserContext userContext;

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;
    private final FriendRepository friendRepository;

    @Override
    @Timed(value = "userService.findById")
    @Transactional(readOnly = true)
    public User findById(String userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(CANNOT_FIND_USER));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "friendsList", key = "#userId")
    public List<FriendResponse.FriendDTO> findAllFriends(String userId) {
        userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(CANNOT_FIND_USER));
        List<Friend> findFriends = friendRepository.findAllByUserId(userId);
        if (findFriends.isEmpty()) {
            return new ArrayList<>();
        }
        ArrayList<FriendResponse.FriendDTO> collect = findFriends.stream()
            .map(Friend::getFriendId)
            .map(friendId -> {
                User user = userRepository.findById(friendId)
                    .orElseThrow(() -> new CustomException(CANNOT_FIND_USER));
                return FriendResponse.FriendDTO.builder()
                    .friendId(user.getUserId())
                    .friendName(user.getUserName())
                    .friendStatus(user.getUserStatus())
                    .build();
            })
            .collect(Collectors.toCollection(ArrayList::new));
        return collect;
    }


    // 유저 저장
    @Override
    public User save(String userId, String userName, String userStatus) {
        userRepository.findByUserId(userId).ifPresent(user -> {
            throw new CustomException(DUPLICATE_RESOURCE);
        });
        return userRepository.save(createUser(userId, userName, userStatus));
    }

    // 유저 상태메세지 업데이트
    @Override
    public void updateUserStatus(RequestChangeUserStatusDTO req) {
        User findUser = userRepository.findById(req.getUserId())
            .orElseThrow(() -> new CustomException(CANNOT_FIND_USER));
        findUser.setUserStatus(req.getStatus());
    }

    public void remove(String userId) {
        Optional<User> findUser = userRepository.findById(userId);
        if (!findUser.isPresent()) {
            throw new CustomException(CANNOT_FIND_USER);
        }

        List<Friend> findFriends = friendRepository.findAllByUserId(userId);
        for (Friend f : findFriends) {
            friendRepository.deleteByUserId(f.getFriendId());
        }
        userRepository.deleteById(userId);
    }

    // 채팅방 생성
    @Override
    @Timed(value = "roomService.makeRoomWithFriends")
    @CacheEvict(value = "chatRoom", key = "#userId")
    public RoomDto makeRoomWithFriends(RequestAddChatRoomDTO req) throws CustomException {
        ArrayList<UserDto> userParticipants = new ArrayList<>();

        User findUser = userRepository.findByUserId(userContext.getUserId())
            .orElseThrow(() -> new CustomException(CANNOT_FIND_USER));

        userParticipants.add(findUser.toDto());

        // 채팅방 참여자가 없을 때 오류 반환
        if (req.getFriendIds().isEmpty()) {
            throw new CustomException(CANNOT_FIND_FRIEND);
        }

        req.getFriendIds().forEach(friendId -> {
            User findFriend = userRepository.findByUserId(friendId)
                .orElseThrow(() -> new CustomException(CANNOT_FIND_USER));
            userParticipants.add(findFriend.toDto());
        });

        // 새로운 채팅방 생성
        Room room = roomRepository.save(new Room(ZonedDateTime.now(), ZonedDateTime.now()));

        // 채팅방 참여 저장-본인
        saveParticipant(findUser, room,
            req.getFriendIds().toString().replace("[", "").replace("]", ""));

        // 채팅방 참여자 저장-친구
        for (String friendId : req.getFriendIds()) {

            User findFriend = getUser(friendId);
            // 해당 유저가 친구가 아닐 때 오류 반환
            isFriend(findUser, friendId);

            // 채팅방 참여자 저장-친구
            saveParticipant(findFriend, room,
                req.getFriendIds().toString().replace("[", "").replace("]", ""));
        }

        return RoomDto.builder()
            .roomId(room.getRoomId())
            .users(userParticipants)
            .createdAt(room.getCreatedAt())
            .updatedAt(room.getUpdatedAt())
            .build();
    }

    // 유저참여 채팅방 검색
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "chatRoom", key = "#userId")
    public List<ChatRoomDTO> findAllMyRooms(String userId) {
        // 유저 존재여부
        Optional<User> findUser = userRepository.findById(userId);
        if (findUser.isEmpty()) {
            throw new CustomException(CANNOT_FIND_USER);
        }
        // 내가 현재 참가하고있는 채팅방 검색
        List<Participant> findParticipants = participantRepository.findAllByUserId(userId);
        // 채팅방 DTO 생성
        List<ChatRoomDTO> chatRoomDTOS = getChatRoomDTOS(findParticipants);

        return chatRoomDTOS;
    }

    public List<Participant> findParticipantWithRoomId(Long roomId) {
        return participantRepository.findAllByRoomId(roomId);
    }

    @Override
    public List<Friend> getMyFriends(String userId) {
        return friendRepository.findAllByUserId(userId);
    }

    @Override
    public Participant findByRoomIdAndUserId(Long roomId, String userId) {
        Participant findParticipant = getParticipant(roomId, userId);
        return findParticipant;
    }

    @Override
    public void removeUser(String userId) {
        User findUser = getUser(userId);
        userRepository.delete(findUser);
    }

    /**
     * 유저 생성 메소드
     *
     * @param userId
     * @param userName
     * @param userStatus
     * @return {@link User}
     */
    private static User createUser(String userId, String userName, String userStatus) {
        return User.builder().userStatus(userStatus).userName(userName).userId(userId).build();
    }

    /**
     * roomId와 userId를 기반으로 참여자를 찾습니다. 만약 room 에 userId 참여자가 없다면 예외를 발생시킵니다.
     *
     * @param roomId
     * @param userId
     * @return {@link Participant}
     * @throws CustomException
     */
    private Participant getParticipant(Long roomId, String userId) throws CustomException {
        return participantRepository.findByRoomIdAndUserId(roomId, userId)
            .orElseThrow(() -> new CustomException(CANNOT_FIND_PARTICIPANT));
    }

    /**
     * userId 기반으로 유저를 찾습니다. 만약 유저가 없다면 예외를 발생시킵니다.
     *
     * @param userId
     * @return {@link User}
     * @throws CustomException
     */
    private User getUser(String userId) throws CustomException {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(CANNOT_FIND_USER));
    }

    /**
     * 서로 친구인지 확인합니다. 만약 한명이라도 친구가 아니라면 예외를 발생시킵니다.
     *
     * @param findUser {@link User}
     * @param userId   {@link String}
     * @throws CustomException
     */
    private void isFriend(User findUser, String userId) throws CustomException {
        Friend f1 = friendRepository.findByUserIdAndFriendId(findUser.getUserId(), userId);
        Friend f2 = friendRepository.findByUserIdAndFriendId(userId, findUser.getUserId());
        if (f1 == null || f2 == null) {
            throw new CustomException(CANNOT_FIND_FRIEND);
        }
    }


    /**
     * 채팅방 리스트를 반환하는 메소드
     *
     * @param findParticipants
     * @return {@link List} < {@link ChatRoomDTO} >
     */
    private List<ChatRoomDTO> getChatRoomDTOS(List<Participant> findParticipants) {
        List<ChatRoomDTO> chatRoomDTOS = new ArrayList<>();
        for (Participant p : findParticipants) {
            ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
            chatRoomDTO.setRoomId(p.getRoom().getRoomId());
            chatRoomDTO.setRoomName(p.getRoomName());
            chatRoomDTO.setParticipantNames(
                participantRepository.findAllByRoomId(p.getRoom().getRoomId()).stream()
                    .map(Participant::getUser)
                    .map(User::getUserName)
                    .collect(Collectors.toList()));
            chatRoomDTOS.add(chatRoomDTO);

        }
        return chatRoomDTOS;
    }

    private void saveParticipant(User findFriend, Room room, String roomName) {
        Participant friendParticipant = new Participant();
        friendParticipant.setUser(findFriend);
        friendParticipant.setRoom(room);
        friendParticipant.setRoomName(roomName);
        friendParticipant.setCreatedAt(LocalDate.now());
        friendParticipant.setUpdatedAt(LocalDate.now());
        participantRepository.save(friendParticipant);
    }
}
