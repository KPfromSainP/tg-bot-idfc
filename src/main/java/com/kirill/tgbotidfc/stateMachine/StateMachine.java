package com.kirill.tgbotidfc.stateMachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kirill.tgbotidfc.stateMachine.UserInput.*;

public class StateMachine {
    private final Map<Long, State> userStates = new HashMap<>();
    private final Map<UserInput, State> eventStates = new HashMap<>();
    private final Map<State, List<UserInput>> availableEvents = new HashMap<>();

    private List<UserInput> userInputs(List<UserInput> lst, UserInput userInput) {
        List<UserInput> lstCopy = new ArrayList<>(lst);
        lstCopy.add(userInput);
        return lstCopy;
    }

     public StateMachine() {
        for (UserInput event : values()) {
            switch (event) {
                case START -> eventStates.put(START, State.MENU);
                case MENU -> eventStates.put(MENU, State.MENU);
                case ANSWERING_ID_GET -> eventStates.put(ANSWERING_ID_GET, State.ANSWERING_ID_GET);
                case ANSWERED_ID_GET -> eventStates.put(ANSWERED_ID_GET, State.MENU);
                case CREATE_TASK -> eventStates.put(CREATE_TASK, State.ANSWERING_ID_CREATE);
                case ANSWERED_ID_CREATE -> eventStates.put(ANSWERED_ID_CREATE, State.CREATING_TASK_TITLE);
                case CREATE_TASK_TITLE -> eventStates.put(CREATE_TASK_TITLE, State.CREATING_TASK_DESCRIPTION);
                case CREATE_TASK_DESCRIPTION -> eventStates.put(CREATE_TASK_DESCRIPTION, State.CREATING_TASK_ASSIGNED);
                case CREATE_TASK_ASSIGNED -> eventStates.put(CREATE_TASK_ASSIGNED, State.CREATING_TASK_PRICE);
                case CREATE_TASK_PRICE -> eventStates.put(CREATE_TASK_PRICE, State.MENU);
                case FAST_TASK -> eventStates.put(FAST_TASK, State.ANSWERING_ID_FAST);
                case ANSWERED_ID_CREATE_FAST -> eventStates.put(ANSWERED_ID_CREATE_FAST, State.FAST_TITLE);
                case CREATE_FAST_TITLE -> eventStates.put(CREATE_FAST_TITLE, State.FAST_PRICE);
                case CREATE_FAST_PRICE -> eventStates.put(CREATE_FAST_PRICE, State.MENU);
                default -> throw new IllegalStateException("Unexpected event: " + event + " " + event.getName());
            }
        }
        List<UserInput> menuOptions = new ArrayList<>();
        menuOptions.add(MENU);
        menuOptions.add(ANSWERING_ID_GET);
        menuOptions.add(CREATE_TASK);
        menuOptions.add(FAST_TASK);
        for (State state : State.values()) {
            switch (state) {
                case MENU -> availableEvents.put(state, menuOptions);
                case ANSWERING_ID_GET -> availableEvents.put(state, userInputs(menuOptions, ANSWERED_ID_GET));
                case ANSWERING_ID_CREATE -> availableEvents.put(state, userInputs(menuOptions, ANSWERED_ID_CREATE));
                case CREATING_TASK_TITLE -> availableEvents.put(state, userInputs(menuOptions, CREATE_TASK_TITLE));
                case CREATING_TASK_DESCRIPTION -> availableEvents.put(state, userInputs(menuOptions, CREATE_TASK_DESCRIPTION));
                case CREATING_TASK_ASSIGNED -> availableEvents.put(state, userInputs(menuOptions, CREATE_TASK_ASSIGNED));
                case CREATING_TASK_PRICE -> availableEvents.put(state, userInputs(menuOptions, CREATE_TASK_PRICE));
                case ANSWERING_ID_FAST -> availableEvents.put(state, userInputs(menuOptions, ANSWERED_ID_CREATE_FAST));
                case FAST_TITLE -> availableEvents.put(state, userInputs(menuOptions, CREATE_FAST_TITLE));
                case FAST_PRICE -> availableEvents.put(state, userInputs(menuOptions, CREATE_FAST_PRICE));
                default -> throw new IllegalStateException("Unexpected state: " + state + " " + state.getName());
            }
        }
    }

    public boolean sendEvent(long chatId, UserInput event) {
        userStates.putIfAbsent(chatId, State.MENU);
        System.out.println("-----------");
        System.out.println(event.getName());
        if (!availableEvents.get(userStates.get(chatId)).contains(event)) {
            return false;
        }
        System.out.println(eventStates.get(event).getName());
        userStates.put(chatId, eventStates.get(event));
        return true;
    }

    public State getUserState(long chatId) {
        return userStates.getOrDefault(chatId, State.MENU);
    }
}
