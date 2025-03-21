package com.kirill.tgbotidfc;

import com.kirill.tgbotidfc.controllers.inline.InlineController;
import com.kirill.tgbotidfc.controllers.inline.InlineCreateFastTaskController;
import com.kirill.tgbotidfc.controllers.inline.InlineCreateTaskController;
import com.kirill.tgbotidfc.controllers.inline.InlineGetController;
import com.kirill.tgbotidfc.controllers.keyboard.Controller;
import com.kirill.tgbotidfc.controllers.keyboard.CreateFastTaskController;
import com.kirill.tgbotidfc.controllers.keyboard.CreateTaskController;
import com.kirill.tgbotidfc.controllers.keyboard.MenuController;
import com.kirill.tgbotidfc.generate.SingleNetwork;
import com.kirill.tgbotidfc.stateMachine.StateMachine;
import com.kirill.tgbotidfc.stateMachine.UserInput;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.kirill.tgbotidfc.generate.Utils.createMenuKeyboard;

@Component
public class SberBot implements SpringLongPollingBot, LongPollingUpdateConsumer {
    private final TelegramClient telegramClient;
    private final Controller menuController;
    private final Controller createTaskController;
    private final Controller createFastTaskController;
    private final InlineController inlineGetController;
    private final InlineController inlineCreateController;
    private final InlineController inlineCreateFastController;

    public SberBot() {
        telegramClient = new OkHttpTelegramClient(getBotToken());
        menuController = new MenuController();
        createTaskController = new CreateTaskController();
        createFastTaskController = new CreateFastTaskController();
        inlineGetController = new InlineGetController();
        inlineCreateController = new InlineCreateTaskController();
        inlineCreateFastController = new InlineCreateFastTaskController();

        SingleNetwork.stateMachine = new StateMachine();
        SingleNetwork.dataTasks = new HashMap<>();
    }

    @Override
    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    public void execute(BotApiMethod<?> message) {
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void controllerRouter(List<BotApiMethod<?>> methods) {
        for (BotApiMethod<?> exec : methods) {
            execute(exec);
        }
    }

    @Override
    public void consume(List<Update> updates) {
        for (Update update : updates) {
            if (update.hasMessage()) {
                long chatId = update.getMessage().getChatId();
                String userInput = update.getMessage().getText();
                if (userInput.equals("/start") || userInput.equals("/menu") || userInput.equals("Отмена")) {
                    SingleNetwork.stateMachine.sendEvent(chatId, UserInput.MENU);
                    execute(createMenuKeyboard(chatId));
                    continue;
                }
                List<BotApiMethod<?>> executable = new ArrayList<>();
                switch (SingleNetwork.stateMachine.getUserState(chatId).getRegion()) {
                    case MENU -> executable = menuController.control(chatId, userInput);
                    case CREATE_TASK -> executable = createTaskController.control(chatId, userInput);
                    case CREATE_FAST_TASK -> executable = createFastTaskController.control(chatId, userInput);
                }
                controllerRouter(executable);
            } else if (update.hasCallbackQuery()) {
                String callData = update.getCallbackQuery().getData();
                long chatId = update.getCallbackQuery().getMessage().getChatId();
                int messageId = update.getCallbackQuery().getMessage().getMessageId();
                if (callData.equalsIgnoreCase("break")) {
                    SingleNetwork.stateMachine.sendEvent(chatId, UserInput.MENU);
                    execute(createMenuKeyboard(chatId));
                    continue;
                }
                List<BotApiMethod<?>> executable = new ArrayList<>();
                switch (SingleNetwork.stateMachine.getUserState(chatId).getRegion()) {
                    case GET_TASKS -> executable = inlineGetController.control(chatId, callData, messageId);
                    case CREATE_TASK -> executable = inlineCreateController.control(chatId, callData, messageId);
                    case CREATE_FAST_TASK -> executable = inlineCreateFastController.control(chatId, callData, messageId);
                }
                controllerRouter(executable);
            }
        }
    }
}