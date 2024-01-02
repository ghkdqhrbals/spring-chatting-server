package chatting.chat.domain.chat.service;


import chatting.chat.domain.chat.entity.Chatting;
import chatting.chat.domain.chat.repository.ChatRepository;
import chatting.chat.domain.participant.entity.Participant;
import chatting.chat.domain.room.entity.Room;
import chatting.chat.domain.participant.repository.ParticipantRepository;
import chatting.chat.domain.room.repository.RoomRepository;
import chatting.chat.domain.user.entity.User;
import chatting.chat.domain.user.repository.UserRepository;
import chatting.chat.web.kafka.dto.RequestAddChatMessageDTO;
import com.example.commondto.dto.chat.ChatRequest.ChatRecordDTO;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.example.commondto.error.ErrorCode.*;

import com.example.commondto.error.CustomException;
import com.example.commondto.error.ErrorCode;
import com.example.commondto.error.ErrorResponse;
import com.example.commondto.error.AppException;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ChatService {

    private final ChatRepository chatRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;


    @Nullable
    public List<ChatRecordDTO> findAllByRoomId(Long roomId) {
        roomRepository.findById(roomId)
            .orElseThrow(() -> new CustomException(CANNOT_FIND_ROOM));
        return chatRepository.findAllByRoomId(roomId).stream().map(
            Chatting::toChatRecordDTO).collect(Collectors.toList());
    }

    public ChatRecordDTO findById(Long id) {
        return chatRepository.findById(id)
            .orElseThrow(() -> new CustomException(CANNOT_FIND_CHATTING)).toChatRecordDTO();
    }

    public void saveAll(List<Chatting> chattings) {
        chatRepository.saveAll(chattings);
    }

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
