package com.kirill.tgbotidfc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateMachine {
    private final Map<Long, State> userStates = new HashMap<>();
    private final Map<Event, State> eventStates = new HashMap<>();
    private final Map<State, List<Event>> availableEvents = new HashMap<>();

    public StateMachine() {
        for (Event event : Event.values()) {
            switch (event) {
                case START -> eventStates.put(Event.START, State.MENU);
                case MENU -> eventStates.put(Event.MENU, State.MENU);
                case ANSWERING_ID_GET -> eventStates.put(Event.ANSWERING_ID_GET, State.ANSWERING_ID_GET);
                case ANSWERED_ID_GET -> eventStates.put(Event.ANSWERED_ID_GET, State.MENU);
                case CREATE_TASK -> eventStates.put(Event.CREATE_TASK, State.ANSWERING_ID_CREATE);
                case ANSWERED_ID_CREATE -> eventStates.put(Event.ANSWERED_ID_CREATE, State.CREATING_TASK_TITLE);
                case CREATE_TASK_TITLE -> eventStates.put(Event.CREATE_TASK_TITLE, State.CREATING_TASK_DESCRIPTION);
                case CREATE_TASK_DESCRIPTION -> eventStates.put(Event.CREATE_TASK_DESCRIPTION, State.CREATING_TASK_ASSIGNED);
                case CREATE_TASK_ASSIGNED -> eventStates.put(Event.CREATE_TASK_ASSIGNED, State.CREATING_TASK_PRICE);
                case CREATE_TASK_PRICE -> eventStates.put(Event.CREATE_TASK_PRICE, State.MENU);
                case FAST_TASK -> eventStates.put(Event.FAST_TASK, State.ANSWERING_ID_FAST);
                case ANSWERED_ID_CREATE_FAST -> eventStates.put(Event.ANSWERED_ID_CREATE_FAST, State.FAST_TITLE);
                case FAST_TITLE -> eventStates.put(Event.FAST_TITLE, State.FAST_PRICE);
                case FAST_PRICE -> eventStates.put(Event.FAST_PRICE, State.MENU);
                default -> throw new IllegalStateException("Unexpected event: " + event.getName());
            }
        }
        List<Event> def = new ArrayList<>();
        def.add(Event.START);
        def.add(Event.MENU);
        for (State state : State.values()) {
            switch (state) {
                case NO_STATE -> availableEvents.put(State.NO_STATE, List.of(Event.values()));
                case MENU -> availableEvents.put(state, List.of(Event.values()));
                case ANSWERING_ID_GET -> availableEvents.put(state, List.of(Event.ANSWERED_ID_GET, Event.MENU));
                case ANSWERING_ID_CREATE -> availableEvents.put(state, List.of(Event.ANSWERED_ID_CREATE, Event.MENU));
                case CREATING_TASK_TITLE -> availableEvents.put(state, List.of(Event.CREATE_TASK_TITLE, Event.MENU));
                case CREATING_TASK_DESCRIPTION -> availableEvents.put(state, List.of(Event.CREATE_TASK_DESCRIPTION, Event.MENU));
                case CREATING_TASK_ASSIGNED -> availableEvents.put(state, List.of(Event.CREATE_TASK_ASSIGNED, Event.MENU));
                case CREATING_TASK_PRICE -> availableEvents.put(state, List.of(Event.CREATE_TASK_PRICE, Event.MENU));
                case ANSWERING_ID_FAST -> availableEvents.put(state, List.of(Event.ANSWERED_ID_CREATE_FAST, Event.MENU));
                case FAST_TITLE -> availableEvents.put(state, List.of(Event.FAST_TITLE, Event.MENU));
                case FAST_PRICE -> availableEvents.put(state, List.of(Event.FAST_PRICE, Event.MENU));
                default -> throw new IllegalStateException("Unexpected state: " + state.getName());
            }
        }
    }

    public boolean sendEvent(long chatId, Event event) {
        userStates.putIfAbsent(chatId, State.NO_STATE);
        System.out.println(event.getName());
        System.out.println(userStates.get(chatId));
        if (!availableEvents.get(userStates.get(chatId)).contains(event)) {
            return false;
        }
        System.out.println(eventStates.get(event).getName());
        userStates.put(chatId, eventStates.get(event));
        return true;
    }

    public State getUserState(long chatId) {
        return userStates.getOrDefault(chatId, State.NO_STATE);
    }
}
