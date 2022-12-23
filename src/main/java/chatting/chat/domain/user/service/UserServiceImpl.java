package chatting.chat.domain.user.service;

import chatting.chat.domain.data.Friend;
import chatting.chat.domain.data.Participant;
import chatting.chat.domain.data.Room;
import chatting.chat.domain.data.User;
import chatting.chat.domain.friend.repository.FriendRepository;
import chatting.chat.domain.participant.repository.ParticipantRepository;
import chatting.chat.domain.room.repository.RoomRepository;
import chatting.chat.domain.user.repository.UserRepository;
import chatting.chat.web.kafka.dto.ChatRoomDTO;
import chatting.chat.web.kafka.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
//@Transactional(rollbackFor=Exception.class) // RollBack 설정
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
    public ResponseAddUserDTO save(User user) {
        Optional<User> findUser = userRepository.findById(user.getUserId());
        ResponseAddUserDTO responseAddUserDTO = new ResponseAddUserDTO(user.getUserId());


        if (findUser.isPresent()) {
            responseAddUserDTO.setUser(findUser.get());
        }else{
            User save = userRepository.save(user);
            responseAddUserDTO.setUser(save);
            responseAddUserDTO.setIsSuccess(true);
        }

        return responseAddUserDTO;
    }

    // 유저 상태메세지 업데이트
    @Override
    public ResponseChangeUserStatusDTO updateUserStatus(RequestChangeUserStatusDTO req) {
        Optional<User> findUser = userRepository.findById(req.getUserId());
        ResponseChangeUserStatusDTO resp = new ResponseChangeUserStatusDTO();

        if (!findUser.isPresent()) {
            resp.setErrorMessage("유저가 존재하지 않습니다");
        }
        else{
            findUser.get().setUserStatus(req.getStatus());
            resp.setStatus(req.getStatus());
            resp.setIsSuccess(true);
        }
        return resp;
    }


    // 채팅방 생성
    @Override
    public ResponseAddUserRoomDTO makeRoomWithFriends(RequestAddUserRoomDTO req){
        ResponseAddUserRoomDTO resp = new ResponseAddUserRoomDTO(req.getUserId());

        // 새로운 채팅방 생성
        Room room = new Room();
        Room save = roomRepository.save(room);

        Optional<User> findUser = userRepository.findById(req.getUserId());
        if (!findUser.isPresent()){
            resp.setErrorMessage("ID="+req.getUserId()+" 존재하지 않는 사용자");
            return resp;
        }

        Participant participant = new Participant();
        participant.setUser(findUser.get());
        participant.setRoom(room);
        participant.setRoomName(req.getFriendId().toString().replace("[","").replace("]",""));
        participant.setCreatedAt(LocalDate.now());
        participant.setUpdatedAt(LocalDate.now());
        participantRepository.save(participant);

        // 채팅방에 참여하는 친구들 목록 생성
        for (String userId : req.getFriendId()){
            Optional<User> findFriend = userRepository.findById(userId);
            if (!findUser.isPresent()){
                resp.setErrorMessage("ID="+userId+" 존재하지 않는 사용자");
                return resp;
            }
            Participant friendParticipant = new Participant();
            friendParticipant.setUser(findFriend.get());
            friendParticipant.setRoom(room);
            friendParticipant.setRoomName(req.getFriendId().toString().replace("[","").replace("]",""));
            friendParticipant.setCreatedAt(LocalDate.now());
            friendParticipant.setUpdatedAt(LocalDate.now());
            participantRepository.save(participant);
        }

        resp.setFriendId(req.getFriendId());
        resp.setIsSuccess(true);
        resp.setCreatedAt(save.getCreatedAt());

        return resp;
    }


    // 유저 검색
    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    // login
    @Override
    public ResponseLoginDTO login(String userId, String userPw) {
        Optional<User> findUser = userRepository.findById(userId);
        if (findUser.isPresent()){
            if (findUser.get().getUserPw().equals(userPw)){
                findUser.get().setLoginDate(LocalDate.now());
                return new ResponseLoginDTO(userId,true,"",findUser.get());
            }
            return new ResponseLoginDTO(userId,false,findUser.get().getUserPw()+":"+userPw+"비밀번호가 맞지 않습니다",new User());
        }
        return new ResponseLoginDTO(userId,false,"유저가 존재하지 않습니다",new User());
    }

    // 로그아웃
    @Override
    public ResponseLogoutDTO logout(String userId) {
        Optional<User> findUser = userRepository.findById(userId);
        if (findUser.isPresent()){
            findUser.get().setLogoutDate(LocalDate.now());
            return new ResponseLogoutDTO(userId,true,"");
        }
        return new ResponseLogoutDTO(userId,false,"유저가 존재하지 않습니다");
    }


    // 유저참여 채팅방 검색
    @Override
    public ResponseUserRoomDTO findAllMyRooms(String userId) {
        Optional<User> findUser = userRepository.findById(userId);

        if (!findUser.isPresent()) {
            return new ResponseUserRoomDTO(userId, false,"유저가 존재하지 않습니다");
        }

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

        return new ResponseUserRoomDTO(userId,true,chatRoomDTOS,"");
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
