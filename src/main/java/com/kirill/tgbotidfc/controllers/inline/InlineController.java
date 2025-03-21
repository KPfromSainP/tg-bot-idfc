package com.kirill.tgbotidfc.controllers.inline;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;

import java.util.List;

public interface InlineController {
    List<BotApiMethod<?>> control(long chatId, String userInput, int messageID);
}
