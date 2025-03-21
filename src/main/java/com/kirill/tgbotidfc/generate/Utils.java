package com.kirill.tgbotidfc.generate;

import com.kirill.tgbotidfc.dto.Printable;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.List;

public class Utils {
    public static String[] createIDArrayFromPrintable(List<? extends Printable> lst) {
        String[] stringIDs = new String[lst.size()];
        for (int i = 0; i < lst.size(); i++) {
            stringIDs[i] = String.valueOf(lst.get(i).getId());
        }
        return stringIDs;
    }
    public static String createTableString(List<? extends Printable> lst) {
        StringBuilder resultSB = new StringBuilder();
        for (Printable element : lst) {
            resultSB.append(String.format(Constants.TABLE_VAL, element.getId(), element.getTitle()));
        }
        return resultSB.toString();
    }
    public static SendMessage createMessage(long chatId, String text) {
        SendMessage msg = new SendMessage("" + chatId, text);
        msg.enableMarkdown(true);
        return msg;
    }

    public static SendMessage createMessage(long chatId, String text, ReplyKeyboard keyboard) {
        SendMessage msg = createMessage(chatId, text);
        msg.setReplyMarkup(keyboard);
        return msg;
    }

    public static SendMessage createMenuKeyboard(long chatId) {
        SendMessage message = new SendMessage("" + chatId, "Меню");
        message.setReplyMarkup(Keyboards.createMenuKeyboard());
        return message;
    }
}
