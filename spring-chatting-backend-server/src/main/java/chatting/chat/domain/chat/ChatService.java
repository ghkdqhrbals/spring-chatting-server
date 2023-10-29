package chatting.chat.domain.chat;


import chatting.chat.domain.chat.entity.Chatting;
import chatting.chat.domain.participant.entity.Participant;
import chatting.chat.domain.room.entity.Room;
import chatting.chat.domain.participant.repository.ParticipantRepository;
import chatting.chat.domain.room.repository.RoomRepository;
import chatting.chat.web.error.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static chatting.chat.web.error.ErrorCode.*;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ChatService {
    private final ChatRepository chatRepository;
    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;


    public ChatService(ChatRepository chatRepository, RoomRepository roomRepository, ParticipantRepository participantRepository) {
        this.chatRepository = chatRepository;
        this.roomRepository = roomRepository;
        this.participantRepository = participantRepository;
    }

    @Nullable
    public List<Chatting>  findAllByRoomId(Long roomId){
        Optional<Room> findRoom = roomRepository.findById(roomId);
        if (!findRoom.isPresent()){
            throw new CustomException(CANNOT_FIND_ROOM);
        }

        return chatRepository.findAllByRoomId(roomId);
    }

    public Chatting findById(Long id){
        Optional<Chatting> findChatting = chatRepository.findById(id);
        if (!findChatting.isPresent()){
            throw new CustomException(CANNOT_FIND_CHATTING);
        }
        return findChatting.get();
    }

    public void saveAll(List<Chatting> chattings){
        chatRepository.saveAll(chattings);
    }

    public Chatting save(Chatting chatting) throws CustomException {
        Participant findParticipant = participantRepository.findByRoomIdAndUserId(chatting.getRoom().getRoomId(), chatting.getSendUser().getUserId());
        // 채팅방 참여인원인지 확인
        if (findParticipant == null){
            throw new CustomException(INVALID_PARTICIPANT);
        }

        Chatting save = chatRepository.save(chatting);
        return save;
    }
}
