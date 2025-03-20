package com.kirill.tgbotidfc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateMachine {
    private final Map<Long, State> userStates = new HashMap<>();
    private final Map<UserInput, State> eventStates = new HashMap<>();
    private final Map<State, List<UserInput>> availableEvents = new HashMap<>();

    public StateMachine() {
        for (UserInput event : UserInput.values()) {
            switch (event) {
                case START -> eventStates.put(UserInput.START, State.MENU);
                case MENU -> eventStates.put(UserInput.MENU, State.MENU);
                case ANSWERING_ID_GET -> eventStates.put(UserInput.ANSWERING_ID_GET, State.ANSWERING_ID_GET);
                case ANSWERED_ID_GET -> eventStates.put(UserInput.ANSWERED_ID_GET, State.MENU);
                case CREATE_TASK -> eventStates.put(UserInput.CREATE_TASK, State.ANSWERING_ID_CREATE);
                case ANSWERED_ID_CREATE -> eventStates.put(UserInput.ANSWERED_ID_CREATE, State.CREATING_TASK_TITLE);
                case CREATE_TASK_TITLE -> eventStates.put(UserInput.CREATE_TASK_TITLE, State.CREATING_TASK_DESCRIPTION);
                case CREATE_TASK_DESCRIPTION -> eventStates.put(UserInput.CREATE_TASK_DESCRIPTION, State.CREATING_TASK_ASSIGNED);
                case CREATE_TASK_ASSIGNED -> eventStates.put(UserInput.CREATE_TASK_ASSIGNED, State.CREATING_TASK_PRICE);
                case CREATE_TASK_PRICE -> eventStates.put(UserInput.CREATE_TASK_PRICE, State.MENU);
                case FAST_TASK -> eventStates.put(UserInput.FAST_TASK, State.ANSWERING_ID_FAST);
                case ANSWERED_ID_CREATE_FAST -> eventStates.put(UserInput.ANSWERED_ID_CREATE_FAST, State.FAST_TITLE);
                case FAST_TITLE -> eventStates.put(UserInput.FAST_TITLE, State.FAST_PRICE);
                case FAST_PRICE -> eventStates.put(UserInput.FAST_PRICE, State.MENU);
                default -> throw new IllegalStateException("Unexpected event: " + event + " " + event.getName());
            }
        }
        for (State state : State.values()) {
            switch (state) {
                case NO_STATE -> availableEvents.put(State.NO_STATE, List.of(UserInput.values()));
                case MENU -> availableEvents.put(state, List.of(UserInput.values()));
                case ANSWERING_ID_GET -> availableEvents.put(state, List.of(UserInput.ANSWERED_ID_GET, UserInput.MENU));
                case ANSWERING_ID_CREATE -> availableEvents.put(state, List.of(UserInput.ANSWERED_ID_CREATE, UserInput.MENU));
                case CREATING_TASK_TITLE -> availableEvents.put(state, List.of(UserInput.CREATE_TASK_TITLE, UserInput.MENU));
                case CREATING_TASK_DESCRIPTION -> availableEvents.put(state, List.of(UserInput.CREATE_TASK_DESCRIPTION, UserInput.MENU));
                case CREATING_TASK_ASSIGNED -> availableEvents.put(state, List.of(UserInput.CREATE_TASK_ASSIGNED, UserInput.MENU));
                case CREATING_TASK_PRICE -> availableEvents.put(state, List.of(UserInput.CREATE_TASK_PRICE, UserInput.MENU));
                case ANSWERING_ID_FAST -> availableEvents.put(state, List.of(UserInput.ANSWERED_ID_CREATE_FAST, UserInput.MENU));
                case FAST_TITLE -> availableEvents.put(state, List.of(UserInput.FAST_TITLE, UserInput.MENU));
                case FAST_PRICE -> availableEvents.put(state, List.of(UserInput.FAST_PRICE, UserInput.MENU));
                default -> throw new IllegalStateException("Unexpected state: " + state + " " + state.getName());
            }
        }
    }

    public boolean sendEvent(long chatId, UserInput event) {
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
