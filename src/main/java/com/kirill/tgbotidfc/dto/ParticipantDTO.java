package com.kirill.tgbotidfc.dto;

import lombok.Getter;

@Getter
public class ParticipantDTO {
    private final int id;
    private final String name;

    public ParticipantDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
