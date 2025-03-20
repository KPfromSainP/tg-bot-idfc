package com.kirill.tgbotidfc;

import lombok.Data;

import java.util.List;

@Data
public class EventDTO implements PrintableDTO {
    private long id;
    private String title;
    private List<ParticipantDTO> participants;

    public EventDTO(long id, String title, List<ParticipantDTO> participants) {
        this.id = id;
        this.title = title;
        this.participants = participants;
    }
}
