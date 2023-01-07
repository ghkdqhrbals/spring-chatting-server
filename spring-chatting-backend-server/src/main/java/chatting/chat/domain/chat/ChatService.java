package chatting.chat.domain.chat;


import chatting.chat.domain.data.Chatting;
import chatting.chat.web.error.CustomException;
import chatting.chat.web.kafka.dto.ResponseAddChatMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static chatting.chat.web.error.ErrorCode.CANNOT_FIND_CHATTING;
import static chatting.chat.web.error.ErrorCode.DUPLICATE_RESOURCE;

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
    public Chatting save(Chatting chatting){
        Optional<Chatting> findChatting = chatRepository.findById(chatting.getId());

        if (findChatting.isPresent()){
            throw new CustomException(DUPLICATE_RESOURCE);
        }

        if (findChatting.isPresent()){
            throw new CustomException(DUPLICATE_RESOURCE);
        }

        Chatting save = chatRepository.save(chatting);
        return save;
    }
}
