package com.kirill.tgbotidfc;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MockData {
    List<EventDTO> myEventsDTO = new ArrayList<>(); // mock
    List<TaskDTO> myTasksDTO = new ArrayList<>(); // mock

    public MockData() {

//        List<TaskDTO> tasks = new ArrayList<>();
//        tasks.add(new TaskDTO(0, "task1","d1",100, 1));
//        tasks.add(new TaskDTO(1, "task2","d1",100, 6));
//        tasks.add(new TaskDTO(2, "task3","d1",100, 9));

        List<ParticipantDTO> participantDTOs = new ArrayList<>();
        participantDTOs.add(new ParticipantDTO(0, "blue"));
        participantDTOs.add(new ParticipantDTO(1, "voo"));

        myEventsDTO.add(new EventDTO(361, "пивной разгон", participantDTOs)); // event1
        participantDTOs = new ArrayList<>();
        participantDTOs.add(new ParticipantDTO(0, "blue"));
        participantDTOs.add(new ParticipantDTO(1, "voo"));
        myEventsDTO.add(new EventDTO(34562, "банный лист", participantDTOs)); // event2
        participantDTOs = new ArrayList<>();
        participantDTOs.add(new ParticipantDTO(0, "blue"));
        participantDTOs.add(new ParticipantDTO(1, "voo"));
        myEventsDTO.add(new EventDTO(2353, "сходка водолазов", participantDTOs)); // event3
        participantDTOs = new ArrayList<>();
        participantDTOs.add(new ParticipantDTO(0, "blue"));
        participantDTOs.add(new ParticipantDTO(1, "voo"));
        myEventsDTO.add(new EventDTO(465, "созвон по бэку", participantDTOs)); // event4
        myTasksDTO.add(new TaskDTO(1245, "купить пива", "d1", 100, 1)); // event1
        myTasksDTO.add(new TaskDTO(1445, "заказать баню", "d2", 1, 1)); // event1
        myTasksDTO.add(new TaskDTO(1345, "оформить лодку", "d3", 3, 1)); // event1
        myTasksDTO.add(new TaskDTO(1645, "дописать бэк", "d4", 12, 1)); // event2
        //
    }
}
