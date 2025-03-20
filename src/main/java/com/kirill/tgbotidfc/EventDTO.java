package com.kirill.tgbotidfc;

import lombok.Data;

import java.util.List;

@Data
public class EventDTO implements PrintableDTO {
    private long id;
    private String title;
    private List<TaskDTO> tasks;

    public EventDTO(long id, String title, List<TaskDTO> tasks) {
        this.id = id;
        this.title = title;
        this.tasks = tasks;
    }
}
