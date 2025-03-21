package com.kirill.tgbotidfc;

public class Notificator {
    final SberBot bot;

    public Notificator(SberBot sberBot) {
        this.bot = sberBot;
    }

    public void notificatorEventStart(long chatId, String eventTitle) {
        bot.execute(bot.sendMessage(chatId, "У вас новое мероприятие, *" + eventTitle + "*! Не забудьте о нем!"));
    }
    public void notificatorEventEnd(long chatId, String eventTitle) {
        bot.execute(bot.sendMessage(chatId, "Мероприятие *" + eventTitle + "* закончилось!"));
    }
    public void notificatorNewTask(long chatId, String taskTitle, String taskDescription) {
        bot.execute(bot.sendMessage(chatId, "Вам назначена задача *" + taskTitle + "*! Её описание: " + taskDescription));
    }
}
