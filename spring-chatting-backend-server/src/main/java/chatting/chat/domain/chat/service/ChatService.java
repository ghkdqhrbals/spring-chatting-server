package chatting.chat.domain.chat.service;


import chatting.chat.domain.chat.consts.ChatConst;
import chatting.chat.domain.chat.entity.Chatting;
import chatting.chat.domain.chat.repository.ChatRepository;
import chatting.chat.domain.room.entity.Room;
import chatting.chat.domain.participant.repository.ParticipantRepository;
import chatting.chat.domain.room.repository.RoomRepository;
import chatting.chat.domain.user.entity.User;
import chatting.chat.domain.user.repository.UserRepository;
import chatting.chat.web.kafka.dto.RequestAddChatMessageDTO;
import com.example.commondto.dto.chat.ChatRequest.ChatRecordDTO;
import io.micrometer.core.annotation.Timed;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.commondto.error.ErrorCode.*;

import com.example.commondto.error.CustomException;
import com.example.commondto.error.ErrorCode;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ChatService {


    private final ChatRepository chatRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;


    /**
     * 채팅방 아이디로 채팅기록을 조회하는 메소드입니다.
     * <br>채팅방이 존재하지 않는다면 예외를 발생시킵니다. {@link ErrorCode#CANNOT_FIND_ROOM}
     *
     * @param roomId
     * @return List {@link ChatRecordDTO}
     */
    @Nullable
    @Timed(value = "chat.findAllByRoomId")
    public List<ChatRecordDTO> findAllByRoomId(Long roomId) {
        roomRepository.findById(roomId)
            .orElseThrow(() -> new CustomException(CANNOT_FIND_ROOM));
        PageRequest pr = PageRequest.of(0, ChatConst.maxChatRecordPage,
            Sort.by("createdAt").descending());
        return chatRepository.findAllByRoomId(roomId, pr).map(Chatting::toChatRecordDTO)
            .toList();
    }

    public ChatRecordDTO findById(String id) {
        return chatRepository.findById(id)
            .orElseThrow(() -> new CustomException(CANNOT_FIND_CHATTING)).toChatRecordDTO();
    }

    public void saveAll(List<Chatting> chattings) {
        chatRepository.saveAll(chattings);
    }

    /**
     * 채팅을 저장하는 메소드입니다.
     * <br>채팅을 저장하기 전에 해당 유저가 해당 채팅방에 참여중인지 확인합니다. 참여중이지 않다면 예외를 발생시킵니다.
     * {@link ErrorCode#INVALID_PARTICIPANT}
     * <br>채팅방이 존재하지 않는다면 예외를 발생시킵니다. {@link ErrorCode#CANNOT_FIND_ROOM}
     * <br>채팅을 전송하는 유저가 존재하지 않는다면 예외를 발생시킵니다. {@link ErrorCode#CANNOT_FIND_USER}
     *
     * @param req    {@link RequestAddChatMessageDTO}
     * @param userId
     * @return {@link ChatRecordDTO}
     * @throws CustomException
     */
    public ChatRecordDTO save(RequestAddChatMessageDTO req, String userId) throws CustomException {
        Room room = roomRepository.findByRoomId(req.getRoomId())
            .orElseThrow(() -> new CustomException(CANNOT_FIND_ROOM));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(CANNOT_FIND_USER));

        Chatting chatting = Chatting.builder()
            .room(room)
            .sendUser(user)
            .message(req.getMessage())
            .build();

        participantRepository.findByRoomIdAndUserId(req.getRoomId(),
            userId).orElseThrow(() -> new CustomException(INVALID_PARTICIPANT));

        return chatRepository.save(chatting).toChatRecordDTO();
    }
}
