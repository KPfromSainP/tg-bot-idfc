package com.kirill.tgbotidfc.controllers.inline;

import com.kirill.tgbotidfc.generate.MockData;
import com.kirill.tgbotidfc.generate.Utils;
import com.kirill.tgbotidfc.dto.EventDTO;
import com.kirill.tgbotidfc.dto.TaskDTO;
import com.kirill.tgbotidfc.stateMachine.UserInput;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

import java.util.ArrayList;
import java.util.List;

import static com.kirill.tgbotidfc.generate.SingleNetwork.stateMachine;
import static com.kirill.tgbotidfc.generate.Utils.createMenuKeyboard;
import static com.kirill.tgbotidfc.generate.Utils.createMessage;
import static java.lang.Math.toIntExact;

public class InlineGetController implements InlineController {
    @Override
    public List<BotApiMethod<?>> control(long chatId, String userInput, int messageID) {
        List<BotApiMethod<?>> result = new ArrayList<>();
        result.add(new DeleteMessage(String.valueOf(chatId), toIntExact(messageID)));
        long eventID = Long.parseLong(userInput);
        // TODO myTasks получить хочется по eventID
        EventDTO enterEvent = MockData.getEvents().get(0);
        List<TaskDTO> myTasks = MockData.getTasks();
        String botText = "Ваши задачи на мероприятие *" + enterEvent.getTitle() + "*:" + System.lineSeparator();
        botText += Utils.createTableString(myTasks);
        botText = myTasks.isEmpty() ? "У вас нет активных задач" : botText;
        result.add(createMessage(chatId, botText));
        result.add(createMenuKeyboard(chatId));
        stateMachine.sendEvent(chatId, UserInput.ANSWERED_ID_GET);
        return result;
    }
}