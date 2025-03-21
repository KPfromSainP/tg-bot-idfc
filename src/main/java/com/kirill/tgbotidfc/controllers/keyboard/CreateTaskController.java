package com.kirill.tgbotidfc.controllers.keyboard;

import com.kirill.tgbotidfc.generate.Keyboards;
import com.kirill.tgbotidfc.generate.MockData;
import com.kirill.tgbotidfc.dto.ParticipantDTO;
import com.kirill.tgbotidfc.dto.TaskDTO;
import com.kirill.tgbotidfc.stateMachine.UserInput;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;

import static com.kirill.tgbotidfc.generate.SingleNetwork.dataTasks;
import static com.kirill.tgbotidfc.generate.SingleNetwork.stateMachine;
import static com.kirill.tgbotidfc.generate.Utils.createMenuKeyboard;
import static com.kirill.tgbotidfc.generate.Utils.createMessage;

public class CreateTaskController implements Controller {
    private TaskDTO task;

    @Override
    public List<BotApiMethod<?>> control(long chatId, String userInput) {
        List<BotApiMethod<?>> result = new ArrayList<>();
        task = dataTasks.get(chatId);
        switch (stateMachine.getUserState(chatId)) {
            case CREATING_TASK_TITLE -> result = createTaskTitle(chatId, userInput);
            case CREATING_TASK_DESCRIPTION -> result = createTaskDescription(chatId, userInput);
            case CREATING_TASK_PRICE -> result = createTaskPrice(chatId, userInput);
        }
        dataTasks.put(chatId, task);
        return result;
    }

    private List<BotApiMethod<?>> createTaskTitle(long chatId, String userInput) {
        List<BotApiMethod<?>> result = new ArrayList<>();
        task.setTitle(userInput);
        stateMachine.sendEvent(chatId, UserInput.CREATE_TASK_TITLE);
        result.add(createMessage(chatId, "Отлично, введите описание задачи", Keyboards.createCancelKeyboard()));
        return result;
    }

    private List<BotApiMethod<?>> createTaskDescription(long chatId, String userInput) {
        List<BotApiMethod<?>> result = new ArrayList<>();
        task.setDescription(userInput);
        stateMachine.sendEvent(chatId, UserInput.CREATE_TASK_DESCRIPTION);
        SendMessage msg = createMessage(chatId, "Превосходно, введите имя ответственного");
        // TODO myEventsDTO.get(0)
        List<ParticipantDTO> participants = MockData.getEvents().get(0).getParticipants();
        String[] participantsNames = new String[participants.size()];
        String[] participantsIDs = new String[participants.size()];
        for (int i = 0; i < participants.size(); i++) {
            participantsNames[i] = participants.get(i).getName();
            participantsIDs[i] = String.valueOf(participants.get(i).getId());
        }
        msg.setReplyMarkup(Keyboards.createInlineKeyboard(participantsNames, participantsIDs, 2));
        result.add(msg);
        return result;
    }

    private List<BotApiMethod<?>> createTaskPrice(long chatId, String userInput) {
        List<BotApiMethod<?>> result = new ArrayList<>();
        try {
            task.setExpenses(Double.parseDouble(userInput));
        } catch (NumberFormatException e) {
            result.add(createMessage(chatId, "Введите число плииз"));
            return result;
        }
        // TODO ОТПРАВИТЬ ТАСКУ
        stateMachine.sendEvent(chatId, UserInput.CREATE_TASK_PRICE);
        result.add(createMessage(chatId, "Задача создана!"));
        result.add(createMenuKeyboard(chatId));
        return result;
    }
}
