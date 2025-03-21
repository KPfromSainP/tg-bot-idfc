package com.kirill.tgbotidfc.dto;

import lombok.Data;
@Data
public class TaskDTO implements Printable {
    private long id;
    private String title;
    private String description;
    private double expenses;
    private long assignedID;
    private long eventID;

    public TaskDTO(long id, String title, String description, double expenses, long assignedID) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.expenses = expenses;
        this.assignedID = assignedID;
    }

    public TaskDTO(long eventID) {
        this.eventID = eventID;
    }
}
