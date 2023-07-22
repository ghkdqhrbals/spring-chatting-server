package chatting.chat.domain.participant.service;

import chatting.chat.domain.data.Participant;
import chatting.chat.domain.participant.repository.ParticipantRepository;

import java.util.List;

public class ParticipantServiceImpl implements ParticipantService{
    private final ParticipantRepository participantRepository;

    public ParticipantServiceImpl(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    @Override
    public List<Participant> findAllByUserId(String userId) {
        return participantRepository.findAllByUserId(userId);
    }

    @Override
    public String save(Participant participant) {
        participantRepository.save(participant);
        return null;
    }

    @Override
    public String remove(Participant participant) {
        participantRepository.delete(participant);
        return null;
    }
}
