package com.kirill.tgbotidfc.controllers.inline;

import com.kirill.tgbotidfc.generate.Keyboards;
import com.kirill.tgbotidfc.dto.TaskDTO;
import com.kirill.tgbotidfc.stateMachine.UserInput;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

import java.util.ArrayList;
import java.util.List;

import static com.kirill.tgbotidfc.generate.SingleNetwork.dataTasks;
import static com.kirill.tgbotidfc.generate.SingleNetwork.stateMachine;
import static com.kirill.tgbotidfc.generate.Utils.createMessage;
import static java.lang.Math.toIntExact;

public class InlineCreateTaskController implements InlineController {
    private TaskDTO task;

    @Override
    public List<BotApiMethod<?>> control(long chatId, String userInput, int messageID) {
        List<BotApiMethod<?>> result = new ArrayList<>();
        task = dataTasks.get(chatId);
        switch (stateMachine.getUserState(chatId)) {
            case ANSWERING_ID_CREATE -> result = parseTaskID(chatId, userInput, messageID);
            case CREATING_TASK_ASSIGNED -> result = createTaskAssigned(chatId, userInput);
        }
        dataTasks.put(chatId, task);
        return result;
    }

    private List<BotApiMethod<?>> parseTaskID(long chatId, String userInput, int messageID) {
        List<BotApiMethod<?>> result = new ArrayList<>();
        // TODO проверить существование меро И членствование юзера в меро
        // получить eventTitle
        long eventID = Long.parseLong(userInput);
        String eventTitle = "ПОЛУЧИТЬ ПО eventID: " + eventID;
        task = new TaskDTO(eventID);
        stateMachine.sendEvent(chatId, UserInput.ANSWERED_ID_CREATE);
        DeleteMessage deleteMessage = new DeleteMessage(String.valueOf(chatId), toIntExact(messageID));
        result.add(deleteMessage);
        result.add(createMessage(chatId, "Хорошо, введите название задачи для мероприятия *" + eventTitle + "*", Keyboards.createCancelKeyboard()));
        return result;
    }



    private List<BotApiMethod<?>> createTaskAssigned(long chatId, String userInput) {
        List<BotApiMethod<?>> result = new ArrayList<>();
        TaskDTO task = dataTasks.get(chatId);
        task.setAssignedID(Long.parseLong(userInput));
        stateMachine.sendEvent(chatId, UserInput.CREATE_TASK_ASSIGNED);
        result.add(createMessage(chatId, "Замечательно, введите примерную сумму", Keyboards.createCancelKeyboard()));
        return result;
    }
}
