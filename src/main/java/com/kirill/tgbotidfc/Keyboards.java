package com.kirill.tgbotidfc;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class Keyboards {
    public static ReplyKeyboardMarkup createMenuKeyboard() {
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton(Constants.MENU_BTN1));
        keyboard.add(row);
        row = new KeyboardRow();
        row.add(new KeyboardButton(Constants.MENU_BTN2));
        row.add(new KeyboardButton(Constants.MENU_BTN3));
        keyboard.add(row);
        row = new KeyboardRow();
        row.add(new KeyboardButton(Constants.MENU_BTN4));
        keyboard.add(row);
        row = new KeyboardRow();
        row.add(new KeyboardButton(Constants.MENU_BTN5));
        keyboard.add(row);
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(keyboard);
        keyboardMarkup.setOneTimeKeyboard(true); // Клавиатура исчезнет после нажатия
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    public static ReplyKeyboardMarkup createCancelKeyboard() {
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("Отмена"));
        keyboard.add(row);
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(keyboard);
        keyboardMarkup.setOneTimeKeyboard(true); // Клавиатура исчезнет после нажатия
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup createInlineKeyboard(String[] inlineElementsName, String[] inlineData, int width) {
        List<InlineKeyboardRow> keyboard = new ArrayList<>();
        InlineKeyboardRow row;
        InlineKeyboardButton btn;
        for (int i = 0; i < inlineElementsName.length; i += width) {
            row = new InlineKeyboardRow();
            for (int j = i; j < i + width && j < inlineElementsName.length; j++) {
                btn = new InlineKeyboardButton(String.valueOf(inlineElementsName[j]));
                btn.setCallbackData(String.valueOf(inlineData[j]));
                row.add(btn);
            }
            keyboard.add(row);
        }
        row = new InlineKeyboardRow();
        btn = new InlineKeyboardButton("Отмена");
        btn.setCallbackData("break");
        row.add(btn);
        keyboard.add(row);
        return new InlineKeyboardMarkup(keyboard);
    }
}
