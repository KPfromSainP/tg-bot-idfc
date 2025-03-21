package com.kirill.tgbotidfc;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.toIntExact;

@Component
public class SberBot implements SpringLongPollingBot, LongPollingUpdateConsumer {
    private final TelegramClient telegramClient;
    private final StateMachine stateMachine;

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

    private void execute(BotApiMethod<?> message) {
        try {
            telegramClient.execute(message);
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
                    stateMachine.sendEvent(chatId, UserInput.START);
                    execute(sendMessage(chatId, "Добро пожаловать! Введите /menu для продолжения."));
                }
                if (text.equals("/menu") || text.equalsIgnoreCase("отмена")) {
                    stateMachine.sendEvent(chatId, UserInput.MENU);
                    execute(createMenuKeyboard(chatId));
                } else {
                    // TODO получить ивент мб
                    State userState = stateMachine.getUserState(chatId);
                    SendMessage menu = menuOptions(chatId, text);
                    TaskDTO task = dataTasks.get(chatId);
                    if (menu != null) {
                        execute(menu);
                        if (stateMachine.getUserState(chatId) == State.MENU) {
                            execute(createMenuKeyboard(chatId));
                        }
                        continue;
                    } else if (userState == State.CREATING_TASK_TITLE) {
                        task.setTitle(text);
                        stateMachine.sendEvent(chatId, UserInput.CREATE_TASK_TITLE);
                        execute(sendMessage(chatId, "Отлично, введите описание задачи"));
                    } else if (userState == State.CREATING_TASK_DESCRIPTION) {
                        task.setDescription(text);
                        stateMachine.sendEvent(chatId, UserInput.CREATE_TASK_DESCRIPTION);
                        SendMessage msg = sendMessage(chatId, "Превосходно, введите имя ответственного");
                        // TODO myEventsDTO.get(0) zalupa
                        List<ParticipantDTO> participants = myEventsDTO.get(0).getParticipants();
                        String[] participantsNames = new String[participants.size()];
                        String[] participantsIDs = new String[participants.size()];
                        for (int i = 0; i < participants.size(); i++) {
                            participantsNames[i] = participants.get(i).getName();
                            participantsIDs[i] = String.valueOf(participants.get(i).getId());
                        }
                        msg.setReplyMarkup(Keyboards.createInlineKeyboard(participantsNames, participantsIDs, 2));
                        execute(msg);
                    } else if (userState == State.CREATING_TASK_PRICE) {
                        try {
                            task.setExpenses(Double.parseDouble(text));
                            // TODO ОТПРАВИТЬ ТАСКУ
                            stateMachine.sendEvent(chatId, UserInput.CREATE_TASK_PRICE);
                            execute(sendMessage(chatId, "Задача создана!"));
                            execute(createMenuKeyboard(chatId));
                        } catch (NumberFormatException e) {
                            execute(sendMessage(chatId, "Введите число, плиииз"));
                        }
                    } else if (userState == State.FAST_TITLE) {
                        task.setTitle(text);
                        stateMachine.sendEvent(chatId, UserInput.FAST_TITLE);
                        execute(sendMessage(chatId, "Отлично, введите сумму денег, которую вы потратили"));
                    } else if (userState == State.FAST_PRICE) {
                        try {
                            task.setExpenses(Double.parseDouble(text));
                            // TODO ОТПРАВИТЬ ТАСКУ
                            stateMachine.sendEvent(chatId, UserInput.FAST_PRICE);
                            execute(sendMessage(chatId, "Задача обработана!"));
                            execute(createMenuKeyboard(chatId));
                        } catch (NumberFormatException e) {
                            execute(sendMessage(chatId, "Введите число, плиииз"));
                        }
                    }
                    dataTasks.put(chatId, task);
                }
            } else if (update.hasCallbackQuery()) {
                String callData = update.getCallbackQuery().getData();
                long chatId = update.getCallbackQuery().getMessage().getChatId();
                long messageId = update.getCallbackQuery().getMessage().getMessageId();
                State userState = stateMachine.getUserState(chatId);
                if (callData.equalsIgnoreCase("break")) {
                    stateMachine.sendEvent(chatId, UserInput.MENU);
                    DeleteMessage deleteMessage = new DeleteMessage(String.valueOf(chatId), toIntExact(messageId));
                    execute(deleteMessage);
                    execute(createMenuKeyboard(chatId));
                    continue;
                }
                if (userState == State.ANSWERING_ID_GET) {
                    long eventID = Long.parseLong(callData);
                    // TODO myTasks получить хочется
                    EventDTO enterEvent = null;
                    for (EventDTO eventDTO : myEventsDTO) {
                        if (eventDTO.getId() == eventID) {
                            enterEvent = eventDTO;
                            break;
                        }
                    }
                    String botText;
                    List<TaskDTO> myTasks = myTasksDTO;
                    assert enterEvent != null;
                    botText = "Ваши задачи на мероприятие *" + enterEvent.getTitle() + "*:" + System.lineSeparator();
                    StringBuilder resultSB = new StringBuilder();
                    for (TaskDTO element : myTasks) {
                        resultSB.append('`').append(element.getId()).append("`: ").append(element.getTitle()).append(System.lineSeparator());
                    }
                    botText += resultSB;
                    botText = myTasks.isEmpty() ? "У вас нет активных задач" : botText;

                    stateMachine.sendEvent(chatId, UserInput.ANSWERED_ID_GET);
                    DeleteMessage deleteMessage = new DeleteMessage(String.valueOf(chatId), toIntExact(messageId));
                    execute(deleteMessage);
                    execute(sendMessage(chatId, botText));
                    execute(createMenuKeyboard(chatId));
                } else if (userState == State.ANSWERING_ID_CREATE || userState == State.ANSWERING_ID_FAST) {
                    // TODO проверить существование меро И членствование юзера в меро
                    long eventID = Long.parseLong(callData);
                    String eventTitle = "pizdec no such eventa";
                    for (EventDTO eventDTO : myEventsDTO) {
                        if (eventDTO.getId() == eventID) {
                            eventTitle = eventDTO.getTitle();
                            break;
                        }
                    }
                    TaskDTO task = new TaskDTO(eventID);
                    dataTasks.put(chatId, task);
                    stateMachine.sendEvent(chatId, userState == State.ANSWERING_ID_CREATE ? UserInput.ANSWERED_ID_CREATE : UserInput.ANSWERED_ID_CREATE_FAST);
                    DeleteMessage deleteMessage = new DeleteMessage(String.valueOf(chatId), toIntExact(messageId));
                    execute(deleteMessage);
                    execute(sendMessage(chatId, "Хорошо, введите название задачи для мероприятия *" + eventTitle + "*", Keyboards.createCancelKeyboard()));
                } else if (userState == State.CREATING_TASK_ASSIGNED) {
                    TaskDTO task = dataTasks.get(chatId);
                    task.setAssignedID(Long.parseLong(callData));
                    stateMachine.sendEvent(chatId, UserInput.CREATE_TASK_ASSIGNED);
                    execute(sendMessage(chatId, "Замечательно, введите примерную сумму", Keyboards.createCancelKeyboard()));
                }
            }
        }
    }

    private SendMessage createMenuKeyboard(long chatId) {
        SendMessage message = new SendMessage("" + chatId, "Меню");
        message.setReplyMarkup(Keyboards.createMenuKeyboard());
        return message;
    }

    private SendMessage menuOptions(long chatId, String text) {
        switch (text) {
            case Constants.MENU_BTN1, Constants.MENU_BTN2 -> stateMachine.sendEvent(chatId, UserInput.MENU);
            case Constants.MENU_BTN3 -> stateMachine.sendEvent(chatId, UserInput.ANSWERING_ID_GET);
            case Constants.MENU_BTN4 -> stateMachine.sendEvent(chatId, UserInput.CREATE_TASK);
            case Constants.MENU_BTN5 -> stateMachine.sendEvent(chatId, UserInput.FAST_TASK);
        }
        if (text.equals(Constants.MENU_BTN1) || text.equals(Constants.MENU_BTN3) || text.equals(Constants.MENU_BTN4) || text.equals(Constants.MENU_BTN5)) {
            // TODO получить список мероприятий (пока что мок myEventsDTO)
            if (myEventsDTO.isEmpty()) {
                return sendMessage(chatId, "У вас нет активных мероприятий");
            }
            String userEvents = "Ваши мероприятия: " + System.lineSeparator();
            StringBuilder resultSB = new StringBuilder();
            for (EventDTO element : myEventsDTO) {
                resultSB.append('`').append(element.getId()).append("`: ").append(element.getTitle()).append(System.lineSeparator());
            }
            userEvents += resultSB;
            if (text.equals(Constants.MENU_BTN3) || text.equals(Constants.MENU_BTN4) || text.equals(Constants.MENU_BTN5)) {
                userEvents += "Выберите номер мероприятия, для которого хотите";
                if (text.equals(Constants.MENU_BTN4) || text.equals(Constants.MENU_BTN5)) {
                    userEvents += " создать ";
                    if (text.equals(Constants.MENU_BTN5)) {
                        userEvents += "быструю ";
                    }
                    userEvents += "задачу";
                } else {
                    userEvents += " узнать свой список задач";
                }
                SendMessage msg = sendMessage(chatId, userEvents);
                String[] ids = new String[myEventsDTO.size()];
                for (int i = 0; i < myEventsDTO.size(); i++) {
                    ids[i] = String.valueOf(myEventsDTO.get(i).getId());
                }
                msg.setReplyMarkup(Keyboards.createInlineKeyboard(ids, ids, 5));
                return msg;
            } else {
                return sendMessage(chatId, userEvents);
            }
        } else if (text.equals(Constants.MENU_BTN2)) {
            // TODO получить список задач всех (пока что мок myTasksDTO)
            if (myTasksDTO.isEmpty()) {
                return sendMessage(chatId, "У вас нет активных задач");
            }
            String usersTasks = "Ваши задачи по всем мероприятиям: " + System.lineSeparator();
            StringBuilder resultSB = new StringBuilder();
            for (TaskDTO element : myTasksDTO) {
                resultSB.append("- ").append(element.getTitle()).append(System.lineSeparator());
            }
            usersTasks += resultSB;
            return sendMessage(chatId, usersTasks);
        }
        return null;
    }

    private SendMessage sendMessage(long chatId, String text) {
        SendMessage msg = new SendMessage("" + chatId, text);
        msg.enableMarkdown(true);
        return msg;
    }

    private SendMessage sendMessage(long chatId, String text, ReplyKeyboard keyboard) {
        SendMessage msg = sendMessage(chatId, text);
        msg.setReplyMarkup(keyboard);
        return msg;
    }
}