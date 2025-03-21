package com.kirill.tgbotidfc.controllers.keyboard;

import com.kirill.tgbotidfc.dto.TaskDTO;
import com.kirill.tgbotidfc.stateMachine.UserInput;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;

import java.util.ArrayList;
import java.util.List;

import static com.kirill.tgbotidfc.generate.SingleNetwork.dataTasks;
import static com.kirill.tgbotidfc.generate.SingleNetwork.stateMachine;
import static com.kirill.tgbotidfc.generate.Utils.createMenuKeyboard;
import static com.kirill.tgbotidfc.generate.Utils.createMessage;

public class CreateFastTaskController implements Controller {
    private TaskDTO task;

    @Override
    public List<BotApiMethod<?>> control(long chatId, String userInput) {
        List<BotApiMethod<?>> result = new ArrayList<>();
        task = dataTasks.get(chatId);
        switch (stateMachine.getUserState(chatId)) {
            case FAST_TITLE -> result = createTaskTitle(chatId, userInput);
            case FAST_PRICE -> result = createTaskPrice(chatId, userInput);
        }
        dataTasks.put(chatId, task);
        return result;
    }

    private List<BotApiMethod<?>> createTaskTitle(long chatId, String userInput) {
        List<BotApiMethod<?>> result = new ArrayList<>();
        task.setTitle(userInput);
        stateMachine.sendEvent(chatId, UserInput.CREATE_FAST_TITLE);
        result.add(createMessage(chatId, "Отлично, введите потраченную сумму"));
        return result;
    }

    private List<BotApiMethod<?>> createTaskPrice(long chatId, String userInput) {
        List<BotApiMethod<?>> result = new ArrayList<>();
        try {
            task.setExpenses(Double.parseDouble(userInput));
        } catch (NumberFormatException e) {
            result.add(createMessage(chatId, "Введите число плииз"));
            return result;
        }        // TODO ОТПРАВИТЬ ТАСКУ
        stateMachine.sendEvent(chatId, UserInput.CREATE_FAST_PRICE);
        result.add(createMessage(chatId, "Задача создана!"));
        result.add(createMenuKeyboard(chatId));
        return result;
    }
}
