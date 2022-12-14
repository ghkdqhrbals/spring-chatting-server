package chatting.chat.domain.participant.service;

import chatting.chat.domain.data.Participant;

import java.util.List;



public interface ParticipantService {

    List<Participant> findAllByUserId(String userId);

    String save(Participant participant);

    String remove(Participant participant);

}
