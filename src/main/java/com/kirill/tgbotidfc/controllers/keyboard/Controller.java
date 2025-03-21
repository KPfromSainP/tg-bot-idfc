package com.kirill.tgbotidfc.controllers.keyboard;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;

import java.util.List;

public interface Controller {
    List<BotApiMethod<?>> control(long chatId, String userInput);
}
