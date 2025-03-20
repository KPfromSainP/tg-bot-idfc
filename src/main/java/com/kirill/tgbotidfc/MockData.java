package com.kirill.tgbotidfc;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MockData {
    List<EventDTO> myEventsDTO = new ArrayList<>(); // mock
    List<TaskDTO> myTasksDTO = new ArrayList<>(); // mock
    public MockData() {

        List<TaskDTO> tasks = new ArrayList<>();
        tasks.add(new TaskDTO(0, "task1","d1",100, 1));
        tasks.add(new TaskDTO(1, "task2","d1",100, 6));
        tasks.add(new TaskDTO(2, "task3","d1",100, 9));

        myEventsDTO.add(new EventDTO(361, "пивной разгон", tasks)); // event1
        tasks = new ArrayList<>();
        tasks.add(new TaskDTO(3, "task4","d1",100, 2));
        tasks.add(new TaskDTO(4, "task5","d1",100, 3));
        tasks.add(new TaskDTO(5, "task6","d1",100, 5));
        myEventsDTO.add(new EventDTO(34562, "банный лист", tasks)); // event2
        tasks = new ArrayList<>();
        tasks.add(new TaskDTO(6, "task7","d1",100, 4));
        tasks.add(new TaskDTO(7, "task8","d1",100, 6));
        tasks.add(new TaskDTO(8, "task9","d1",100, 9));
        myEventsDTO.add(new EventDTO(2353, "сходка водолазов", tasks)); // event3
        tasks = new ArrayList<>();
        tasks.add(new TaskDTO(9, "task10","d1",100, 2));
        tasks.add(new TaskDTO(10, "task11","d1",100, 3));
        tasks.add(new TaskDTO(11, "task12","d1",100, 5));
        myEventsDTO.add(new EventDTO(465, "созвон по бэку", tasks)); // event4
        //
        myTasksDTO.add(new TaskDTO(1245, "купить пива","d1",100, 1)); // event1
        myTasksDTO.add(new TaskDTO(1445,"заказать баню", "d2", 1, 1)); // event1
        myTasksDTO.add(new TaskDTO(1345,"оформить лодку", "d3", 3,1)); // event1
        myTasksDTO.add(new TaskDTO(1645,"дописать бэк", "d4", 12,1)); // event2
        //
    }
}
