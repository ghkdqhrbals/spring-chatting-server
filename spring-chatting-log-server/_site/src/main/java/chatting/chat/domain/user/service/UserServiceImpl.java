package chatting.chat.domain.user.service;

import chatting.chat.domain.data.Friend;
import chatting.chat.domain.data.Participant;
import chatting.chat.domain.data.Room;
import chatting.chat.domain.data.User;
import chatting.chat.domain.friend.repository.FriendRepository;
import chatting.chat.domain.participant.repository.ParticipantRepository;
import chatting.chat.domain.room.repository.RoomRepository;
import chatting.chat.domain.user.repository.UserRepository;
import chatting.chat.web.dto.ChatRoomDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
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

    // 유저 저장
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    // 유저 상태메세지 업데이트
    @Override
    public void updateUserStatus(String userId, String userStatus) {
        User findUser = userRepository.findByUserId(userId);
        findUser.setUserStatus(userStatus);
    }


    // 채팅방 생성
    @Override
    public void makeRoomWithFriends(User user, List<String> friendId) {
        List<Participant> participants = new ArrayList<>();

        // 채팅방 이름 설정
        String nameDelimiter = "";
        if (friendId.size() >= 1){
            nameDelimiter = " 외 " + friendId.size() +" 명";
        }

        // 새로운 채팅방 생성
        Room room = new Room();
        roomRepository.save(room);

        // 유저자신 채팅방 참여목록 생성
        setParticipant(user, nameDelimiter, room, user.getUserName());

        // 채팅방에 참여하는 친구들 목록 생성
        for (String userId : friendId){
            User findUser = userRepository.findByUserId(userId);
            setParticipant(findUser, nameDelimiter, room, user.getUserName());
        }
    }


    // 특정 유저 검색-1
    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    // 특정 유저 검색-2
    @Override
    public User findByUserId(String id) {
        return userRepository.findByUserId(id);
    }


    // 유저참여 채팅방 검색
    @Override
    public List<ChatRoomDTO> findAllMyRooms(String userId) {
        // 내가 현재 참가하고있는 채팅방 검색
        List<Participant> findParticipants = participantRepository.findAllByUserId(userId);

        // 채팅방 DTO 생성
        List<ChatRoomDTO> chatRoomDTOS = new ArrayList<>();
        for (Participant p : findParticipants){
            ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
            chatRoomDTO.setRoomId(p.getRoom().getRoomId());
            chatRoomDTO.setRoomName(p.getRoomName());
            chatRoomDTOS.add(chatRoomDTO);
        }

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
        return participantRepository.findByRoomIdAndUserId(roomId,userId);
    }

    // 채팅방 참여자 저장
    private void setParticipant(User user, String delimiter, Room room, String userName) {
        Participant participant = new Participant();
        participant.setUser(user);
        participant.setRoom(room);
        participant.setRoomName(userName +delimiter);
        participant.setCreatedAt(LocalDate.now());
        participant.setUpdatedAt(LocalDate.now());
        participantRepository.save(participant);
    }

}
