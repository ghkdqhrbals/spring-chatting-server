package chatting.chat.domain.user.service;

import chatting.chat.domain.data.Friend;
import chatting.chat.domain.data.Participant;
import chatting.chat.domain.data.Room;
import chatting.chat.domain.data.User;
import chatting.chat.domain.friend.repository.FriendRepository;
import chatting.chat.domain.participant.repository.ParticipantRepository;
import chatting.chat.domain.room.repository.RoomRepository;
import chatting.chat.domain.user.repository.UserRepository;
import chatting.chat.web.error.CustomException;
import chatting.chat.web.kafka.dto.ChatRoomDTO;
import chatting.chat.web.kafka.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static chatting.chat.web.error.ErrorCode.*;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;
    private final FriendRepository friendRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoomRepository roomRepository, ParticipantRepository participantRepository, FriendRepository friendRepository) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.participantRepository = participantRepository;
        this.friendRepository = friendRepository;
    }

    /**
     *
     * ------ public methods -------
     *
     */

    // 유저 검색
    @Override
    public User findById(String userId) {
        return getUser(userId);
    }

    // 유저 저장
    @Override
    public User save(String userId, String userName, String userStatus) {
        throwErrorWhenUserFind(userId);
        return userRepository.save(createUser(userId, userName, userStatus));
    }

    // 유저 상태메세지 업데이트
    @Override
    public void updateUserStatus(RequestChangeUserStatusDTO req) {
        User findUser = getUser(req.getUserId());
        findUser.setUserStatus(req.getStatus());
    }

    public void remove(String userId){
        throwErrorWhenUserNotFind(userId);
        List<Friend> findFriends = friendRepository.findAllByUserId(userId);
        for (Friend f : findFriends){
            friendRepository.deleteByUserId(f.getFriendId());
        }

        userRepository.deleteById(userId);
    }

    // 채팅방 생성
    @Override
    public void makeRoomWithFriends(RequestAddChatRoomDTO req) {

        User findUser = getUser(req.getUserId());

        // 새로운 채팅방 생성
        Room room = roomRepository.save(new Room(ZonedDateTime.now(),ZonedDateTime.now()));

        // 채팅방 참여 저장-본인
        saveParticipant(findUser, room, req.getFriendIds().toString().replace("[", "").replace("]", ""));

        // 채팅방 참여자 저장-친구
        for (String friendId : req.getFriendIds()) {

            User findFriend = getUser(friendId);
            // 해당 유저가 친구가 아닐 때 오류 반환
            isFriend(findUser, friendId);

            // 채팅방 참여자 저장-친구
            saveParticipant(findFriend, room, req.getFriendIds().toString().replace("[", "").replace("]", ""));
        }
    }

    // 유저참여 채팅방 검색
    @Override
    public List<ChatRoomDTO> findAllMyRooms(String userId) {
        // 유저 존재여부
        throwErrorWhenUserNotFind(userId);
        // 내가 현재 참가하고있는 채팅방 검색
        List<Participant> findParticipants = participantRepository.findAllByUserId(userId);
        // 채팅방 DTO 생성
        List<ChatRoomDTO> chatRoomDTOS = getChatRoomDTOS(findParticipants);
        return chatRoomDTOS;
    }
    public List<Participant> findParticipantWithRoomId(Long roomId){
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
    public void removeUser(String userId){
        User findUser = getUser(userId);
        userRepository.delete(findUser);
    }

    /**
     *
     * ------ private methods -------
     *
     */

    private static User createUser(String userId, String userName, String userStatus) {
        User user = new User();
        user.setUserId(userId);
        user.setUserStatus(userStatus);
        user.setUserName(userName);
        return user;
    }

    public void throwErrorWhenUserFind(String userId) throws CustomException {
        Optional<User> findUser = userRepository.findById(userId);
        if (findUser.isPresent()) {
            throw new CustomException(DUPLICATE_USER_RESOURCE);
        }
    }
    public void throwErrorWhenUserNotFind(String userId) throws CustomException {
        Optional<User> findUser = userRepository.findById(userId);
        if (!findUser.isPresent()) {
            throw new CustomException(CANNOT_FIND_USER);
        }
    }
    private Participant getParticipant(Long roomId, String userId) throws CustomException {
        Participant findParticipant = participantRepository.findByRoomIdAndUserId(roomId, userId);
        if (findParticipant==null){
            throw new CustomException(CANNOT_FIND_PARTICIPANT);
        }
        return findParticipant;
    }
    private User getUser(String userId) throws CustomException {
        Optional<User> findUser = userRepository.findById(userId);
        if (!findUser.isPresent()) {
            throw new CustomException(CANNOT_FIND_USER);
        }
        return findUser.get();
    }
    private void isFriend(User findUser, String userId) throws CustomException {
        Friend f1 = friendRepository.findByUserIdAndFriendId(findUser.getUserId(), userId);
        Friend f2 = friendRepository.findByUserIdAndFriendId(userId, findUser.getUserId());
        if (f1 == null || f2 == null) {
            throw new CustomException(CANNOT_FIND_FRIEND);
        }
    }
    private List<ChatRoomDTO> getChatRoomDTOS(List<Participant> findParticipants) {
        List<ChatRoomDTO> chatRoomDTOS = new ArrayList<>();
        for (Participant p : findParticipants){
            ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
            chatRoomDTO.setRoomId(p.getRoom().getRoomId());
            chatRoomDTO.setRoomName(p.getRoomName());
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
