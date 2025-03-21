package com.kirill.tgbotidfc.controllers.keyboard;

import com.kirill.tgbotidfc.generate.Constants;
import com.kirill.tgbotidfc.generate.Keyboards;
import com.kirill.tgbotidfc.generate.MockData;
import com.kirill.tgbotidfc.generate.Utils;
import com.kirill.tgbotidfc.dto.EventDTO;
import com.kirill.tgbotidfc.dto.TaskDTO;
import com.kirill.tgbotidfc.stateMachine.State;
import com.kirill.tgbotidfc.stateMachine.UserInput;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;

import static com.kirill.tgbotidfc.generate.SingleNetwork.stateMachine;
import static com.kirill.tgbotidfc.generate.Utils.createMenuKeyboard;
import static com.kirill.tgbotidfc.generate.Utils.createMessage;

public class MenuController implements Controller {

    @Override
    public List<BotApiMethod<?>> control(long chatId, String userInput) {
        List<BotApiMethod<?>> result = new ArrayList<>();
        switch (userInput) {
            case Constants.MENU_BTN1, Constants.MENU_BTN2 -> stateMachine.sendEvent(chatId, UserInput.MENU);
            case Constants.MENU_BTN3 -> stateMachine.sendEvent(chatId, UserInput.ANSWERING_ID_GET);
            case Constants.MENU_BTN4 -> stateMachine.sendEvent(chatId, UserInput.CREATE_TASK);
            case Constants.MENU_BTN5 -> stateMachine.sendEvent(chatId, UserInput.FAST_TASK);
        }
        if (userInput.equals(Constants.MENU_BTN1) || userInput.equals(Constants.MENU_BTN3) || userInput.equals(Constants.MENU_BTN4) || userInput.equals(Constants.MENU_BTN5)) {
            // TODO получить список мероприятий (пока что мок myEventsDTO)
            List<EventDTO> myEvents = MockData.getEvents();
            if (myEvents.isEmpty()) {
                result.add(createMessage(chatId, Constants.NO_ACTIVE_EVENTS));
                return result;
            }
            String userEvents = "Ваши мероприятия: " + System.lineSeparator();
            userEvents += Utils.createTableString(myEvents);
            if (userInput.equals(Constants.MENU_BTN3) || userInput.equals(Constants.MENU_BTN4) || userInput.equals(Constants.MENU_BTN5)) {
                if (userInput.equals(Constants.MENU_BTN3))
                    userEvents += Constants.CHOOSE_EVENT_ID;
                else if (userInput.equals(Constants.MENU_BTN4))
                    userEvents += Constants.CHOOSE_EVENT_ID_CREATE;
                else
                    userEvents += Constants.CHOOSE_EVENT_ID_FAST_CREATE;
                SendMessage msg = createMessage(chatId, userEvents);
                String[] ids = Utils.createIDArrayFromPrintable(myEvents);
                msg.setReplyMarkup(Keyboards.createInlineKeyboard(ids, ids, 5));
                result.add(msg);
            } else {
                result.add(createMessage(chatId, userEvents));
            }
        } else if (userInput.equals(Constants.MENU_BTN2)) {
            // TODO получить список задач всех (пока что мок myTasksDTO)
            List<TaskDTO> myTasks = MockData.getTasks();
            if (myTasks.isEmpty()) {
                result.add(createMessage(chatId, Constants.NO_ACTIVE_TASKS));
                return result;
            }
            String usersTasks = "Ваши задачи по всем мероприятиям: " + System.lineSeparator();
            usersTasks += Utils.createTableString(myTasks);
            result.add(createMessage(chatId, usersTasks));
        }
        if (stateMachine.getUserState(chatId) == State.MENU){
            result.add(createMenuKeyboard(chatId));
        }
        return result;
    }
}
