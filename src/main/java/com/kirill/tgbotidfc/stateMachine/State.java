package com.kirill.tgbotidfc.stateMachine;

import lombok.Getter;

@Getter
public enum State {
    MENU("находимся в меню ожидается чтото из меню опшеонс", Region.MENU),

    ANSWERING_ID_GET("ожидается id мероприятия чтобы вывести список тасок", Region.GET_TASKS),

    ANSWERING_ID_CREATE("ожидается id мероприятия чтобы создать в нем таску", Region.CREATE_TASK),
    CREATING_TASK_TITLE("ожидается название таски", Region.CREATE_TASK),
    CREATING_TASK_DESCRIPTION("ожидается описание таски", Region.CREATE_TASK),
    CREATING_TASK_ASSIGNED("ожидается имя ответсвенного на таску", Region.CREATE_TASK),
    CREATING_TASK_PRICE("ожидается цена таски", Region.CREATE_TASK),

    ANSWERING_ID_FAST("ожидается id мероприятия чтобы создать в нем быструю таску", Region.CREATE_FAST_TASK),
    FAST_TITLE("ожиадается тайтл для таски", Region.CREATE_FAST_TASK),
    FAST_PRICE("ожидается прайс для таски", Region.CREATE_FAST_TASK),

    ;

    private final String name;
    private final Region region;

    State(String name, Region region) {
        this.name = name;
        this.region = region;
    }
}
