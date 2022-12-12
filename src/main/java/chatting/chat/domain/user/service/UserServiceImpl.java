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

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void updateUserStatus(String userId, String userStatus) {
        User findUser = userRepository.findByUserId(userId);
        findUser.setUserStatus(userStatus);
    }


    @Override
    public void makeRoomWithFriends(User user, List<String> friendId) {
        List<Participant> participants = new ArrayList<>();

        String nameDelimiter = "";
        if (friendId.size() >= 1){
            nameDelimiter = " 외 " + friendId.size() +" 명";
        }

        Room room = new Room();
        roomRepository.save(room);

        setParticipant(user, nameDelimiter, room, user.getUserName()); // 자신 참여 설정
        for (String userId : friendId){
            User findUser = userRepository.findByUserId(userId); // 친구 참여 설정
            setParticipant(findUser, nameDelimiter, room, user.getUserName());
        }
    }

    private void setParticipant(User user, String delimiter, Room room, String userName) {
        Participant participant = new Participant();
        participant.setUser(user);
        participant.setRoom(room);
        participant.setRoomName(userName +delimiter);
        participant.setCreatedAt(LocalDate.now());
        participant.setUpdatedAt(LocalDate.now());

        participantRepository.save(participant);
    }

    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     *
     * @param userId
     * @return
     *
     * 1. participantRepository : 내가 현재 참가하고있는 roomid 확인
     * 2. participantRepository : roomid에 참가하고있는 참여자 모두 확인
     */
    @Override
    public List<ChatRoomDTO> findAllMyRooms(String userId) {
        List<Participant> findParticipants = participantRepository.findAllByUserId(userId);
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


}
