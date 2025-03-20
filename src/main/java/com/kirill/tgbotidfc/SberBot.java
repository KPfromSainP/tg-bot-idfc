package com.kirill.tgbotidfc;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SberBot implements SpringLongPollingBot, LongPollingUpdateConsumer {
    private final TelegramClient telegramClient;
    private final StateMachine stateMachine;

    private final String menuBtn1 = "Мои мероприятия";
    private final String menuBtn2 = "Мои задачи";
    private final String menuBtn3 = "Мои задачи на мероприятии";
    private final String menuBtn4 = "Создать задачу";
    private final String menuBtn5 = "Быстрые задачи";

    private final Map<Long, TaskDTO> dataTasks = new HashMap<>();

    List<EventDTO> myEventsDTO = new ArrayList<>(); // mock
    List<TaskDTO> myTasksDTO = new ArrayList<>(); // mock

    public SberBot() {
        telegramClient = new OkHttpTelegramClient(getBotToken());
        stateMachine = new StateMachine();
        MockData mockData = new MockData();
        myEventsDTO = mockData.getMyEventsDTO();
        myTasksDTO = mockData.getMyTasksDTO();
    }

    @Override
    public String getBotToken() {
        // захерачить токен в переменные окружения
        // export VARIABLE_NAME = {YOUR_BOT_TOKEN}
        // ||
        // idea -> edit configuration -> modify options -> environment var - > BOT_TOKEN=TOKEN
        return System.getenv("BOT_TOKEN");
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    private void execute(final SendMessage msg) {
        try {
            telegramClient.execute(msg);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void consume(List<Update> updates) {
        for (Update update : updates) {
            if (update.hasMessage()) {
                Message message = update.getMessage();
                long chatId = message.getChatId();
                String text = message.getText();

                if (text.equals("/start")) {
                    stateMachine.sendEvent(chatId, Event.START);
                    execute(sendMessage(chatId, "Добро пожаловать! Введите /menu для продолжения."));
                }
                if (text.equals("/menu")) {
                    stateMachine.sendEvent(chatId, Event.MENU);
                    execute(createMenuKeyboard(chatId));
                } else {
                    State userState = stateMachine.getUserState(chatId);
                    SendMessage msg = menuOptions(chatId, text);
                    if (msg != null) {
                        execute(msg);
                        continue;
                    }
                    if (userState == State.ANSWERING_ID_GET) {
                        // text == event_id
                        // хотим получить от бека список тасок по юзеру по мероприятию text
                        // получить список моих ивентов весь код это моки блять
                        long eventID = Long.parseLong(text);
                        EventDTO enterEvent = null;
                        for (EventDTO eventDTO : myEventsDTO) {
                            if (eventDTO.getId() == eventID) {
                                enterEvent = eventDTO;
                                break;
                            }
                        }
                        String botText;
                        if (enterEvent == null) {
                            execute(sendMessage(chatId, "Такого мероприятия нет. Введите заново."));
                            continue;
                        } else {
                            // только здесь я получил таски и то ! не только мои, а все
                            List<TaskDTO> myTasks = enterEvent.getTasks();
                            botText = "Ваши задачи на мероприятие " + enterEvent.getTitle() + ":" + System.lineSeparator();
                            StringBuilder resultSB = new StringBuilder();
                            for (PrintableDTO element : myTasks) {
                                resultSB.append('`').append(element.getId()).append("`: ").append(element.getTitle()).append(System.lineSeparator());
                            }
                            botText += resultSB;
                            botText = myTasks.isEmpty() ? "У вас нет активных задач" : botText;
                        }
                        stateMachine.sendEvent(chatId, Event.ANSWERED_ID_GET);
                        execute(sendMessage(chatId, botText));
                        execute(createMenuKeyboard(chatId));
                    } else if (userState == State.ANSWERING_ID_CREATE) {
                        // TODO и проверяем есть ли такое мероприятие в беке
                        TaskDTO task = new TaskDTO(Long.parseLong(text));
                        dataTasks.put(chatId, task);
                        stateMachine.sendEvent(chatId, Event.ANSWERED_ID_CREATE);
                        execute(sendMessage(chatId, "Хорошо, введите название задачи"));
                    } else if (userState == State.CREATING_TASK_TITLE) {
                        TaskDTO task = dataTasks.get(chatId);
                        task.setTitle(text);
                        dataTasks.put(chatId, task);
                        stateMachine.sendEvent(chatId, Event.CREATE_TASK_TITLE);
                        execute(sendMessage(chatId, "Отлично, введите описание задачи"));
                    } else if (userState == State.CREATING_TASK_DESCRIPTION) {
                        TaskDTO task = dataTasks.get(chatId);
                        task.setDescription(text);
                        dataTasks.put(chatId, task);
                        stateMachine.sendEvent(chatId, Event.CREATE_TASK_DESCRIPTION);
                        execute(sendMessage(chatId, "Превосходно, введите ID ответственного"));
                    } else if (userState == State.CREATING_TASK_ASSIGNED) {
                        TaskDTO task = dataTasks.get(chatId);
                        task.setAssignedID(Long.parseLong(text));
                        dataTasks.put(chatId, task);
                        stateMachine.sendEvent(chatId, Event.CREATE_TASK_ASSIGNED);
                        execute(sendMessage(chatId, "Замечательно, введите примерную сумму"));
                    } else if (userState == State.CREATING_TASK_PRICE) {
                        TaskDTO task = dataTasks.get(chatId);
                        task.setExpenses(Double.parseDouble(text));
                        dataTasks.put(chatId, task);
                        // TODO ОТПРАВИТЬ ТАСКУ
                        stateMachine.sendEvent(chatId, Event.CREATE_TASK_PRICE);
                        execute(sendMessage(chatId, "Задача создана!"));
                        execute(createMenuKeyboard(chatId));
                    } else if (userState == State.ANSWERING_ID_FAST) {
                        // TODO и проверяем есть ли такое мероприятие в беке
                        TaskDTO task = new TaskDTO(Long.parseLong(text));
                        dataTasks.put(chatId, task);
                        stateMachine.sendEvent(chatId, Event.ANSWERED_ID_CREATE_FAST);
                        execute(sendMessage(chatId, "Хорошо, введите название задачи"));
                    } else if (userState == State.FAST_TITLE) {
                        TaskDTO task = dataTasks.get(chatId);
                        task.setTitle(text);
                        dataTasks.put(chatId, task);
                        stateMachine.sendEvent(chatId, Event.FAST_TITLE);
                        execute(sendMessage(chatId, "Отлично, введите сумму денег, которую вы потратили"));
                    } else if (userState == State.FAST_PRICE) {
                        TaskDTO task = dataTasks.get(chatId);
                        task.setExpenses(Double.parseDouble(text));
                        dataTasks.put(chatId, task);
                        // TODO ОТПРАВИТЬ ТАСКУ
                        stateMachine.sendEvent(chatId, Event.FAST_PRICE);
                        execute(sendMessage(chatId, "Задача обработана!"));
                        execute(createMenuKeyboard(chatId));
                    }
                }
            }
        }
    }

    private SendMessage createMenuKeyboard(long chatId) {
        SendMessage message = new SendMessage("" + chatId, "Меню");

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton(menuBtn1));
        keyboard.add(row);
        row = new KeyboardRow();
        row.add(new KeyboardButton(menuBtn2));
        row.add(new KeyboardButton(menuBtn3));
        keyboard.add(row);
        row = new KeyboardRow();
        row.add(new KeyboardButton(menuBtn4));
        keyboard.add(row);
        row = new KeyboardRow();
        row.add(new KeyboardButton(menuBtn5));
        keyboard.add(row);
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(keyboard);
        keyboardMarkup.setOneTimeKeyboard(true); // Клавиатура исчезнет после нажатия
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }

    private SendMessage menuOptions(long chatId, String text) {
        switch (text) {
            case menuBtn1, menuBtn2 -> stateMachine.sendEvent(chatId, Event.MENU);
            case menuBtn3 -> stateMachine.sendEvent(chatId, Event.ANSWERING_ID_GET);
            case menuBtn4 -> stateMachine.sendEvent(chatId, Event.CREATE_TASK);
            case menuBtn5 -> stateMachine.sendEvent(chatId, Event.FAST_TASK);
        }
        if (text.equals(menuBtn1) || text.equals(menuBtn3) || text.equals(menuBtn4) || text.equals(menuBtn5)) {
            // получить список мероприятий
            String userEvents = "Ваши мероприятия: " + System.lineSeparator();
            StringBuilder resultSB = new StringBuilder();
            for (PrintableDTO element : myEventsDTO) {
                resultSB.append('`').append(element.getId()).append("`: ").append(element.getTitle()).append(System.lineSeparator());
            }
            userEvents += resultSB;
            if (text.equals(menuBtn3) || text.equals(menuBtn4) || text.equals(menuBtn5)) {
                userEvents += "Выберите номер мероприятия, для которого хотите";
                if (text.equals(menuBtn4) || text.equals(menuBtn5)) {
                    userEvents += " создать ";
                    if (text.equals(menuBtn5)) {
                        userEvents += "быструю ";
                    }
                    userEvents += "задачу";
                } else {
                    userEvents += " узнать свой список задач";
                }
            }
            userEvents = userEvents.isEmpty() ? "У вас нет активных мероприятий" : userEvents;
            return sendMessage(chatId, userEvents);
        } else if (text.equals(menuBtn2)) {
            // получить список задач всех
            String usersTasks = "Ваши задачи по всем мероприятиям: " + System.lineSeparator();
            StringBuilder resultSB = new StringBuilder();
            for (PrintableDTO element : myTasksDTO) {
                resultSB.append(element.getTitle()).append(System.lineSeparator());
            }
            usersTasks += resultSB;
            usersTasks = usersTasks.isEmpty() ? "У вас нет активных задач" : usersTasks;
            return sendMessage(chatId, usersTasks);
        }
        return null;
    }

    private SendMessage sendMessage(long chatId, String text) {
        SendMessage msg = new SendMessage("" + chatId, text);
        msg.enableMarkdown(true);
        return msg;
    }
}