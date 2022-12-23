package chatting.chat.domain.chat;


import chatting.chat.domain.data.Chatting;
import chatting.chat.web.kafka.dto.ResponseAddChatMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ChatService {
    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Nullable
    public List<Chatting>  findAllByRoomId(Long roomId){
        log.info("findAllByRoomID={}",roomId);

        return chatRepository.findAllByRoomId(roomId);
    }

    public void saveAll(List<Chatting> chattings){
        chatRepository.saveAll(chattings);
    }
    public ResponseAddChatMessageDTO save(Chatting chatting){
        Optional<Chatting> findChatting = chatRepository.findById(chatting.getId());
        ResponseAddChatMessageDTO resp = new ResponseAddChatMessageDTO(chatting.getRoom().getRoomId(), chatting.getSendUser().getUserId());

        if (findChatting.isPresent()){
            resp.setErrorMessage("중복전송");
            return resp;
        }

        chatRepository.save(chatting);

        resp.setIsSuccess(true);
        resp.setMessage(chatting.getMessage());
        resp.setCreatedAt(ZonedDateTime.now());
        return resp;

    }


}
