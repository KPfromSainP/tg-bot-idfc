package com.kirill.tgbotidfc.dto;

import lombok.Data;
import java.util.List;

@Data
public class EventDTO implements Printable {
    private long id;
    private String title;
    private List<ParticipantDTO> participants;

    public EventDTO(long id, String title, List<ParticipantDTO> participants) {
        this.id = id;
        this.title = title;
        this.participants = participants;
    }
}
