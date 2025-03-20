package com.kirill.tgbotidfc;

import lombok.Getter;

@Getter
public enum State {
    NO_STATE("no_state"),
    MENU("находимся в меню ожидается чтото из меню опшеонс"),
    ANSWERING_ID_GET("ожидается id мероприятия чтобы вывести список тасок"),

    ANSWERING_ID_CREATE("ожидается id мероприятия чтобы создать в нем таску"),
    CREATING_TASK_TITLE("ожидается название таски"),
    CREATING_TASK_DESCRIPTION("ожидается описание таски"),
    CREATING_TASK_ASSIGNED("ожидается имя ответсвенного на таску"),
    CREATING_TASK_PRICE("ожидается цена таски"),

    ANSWERING_ID_FAST("ожидается id мероприятия чтобы создать в нем быструю таску"),
    FAST_TITLE("ожиадается тайтл для таски"),
    FAST_PRICE("ожидается прайс для таски"),

    ;

    private final String name;

    State(String name) {
        this.name = name;
    }
}
