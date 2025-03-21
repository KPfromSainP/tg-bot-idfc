package com.kirill.tgbotidfc.stateMachine;

import lombok.Getter;

@Getter
public enum UserInput {
    START("start"),
    MENU("хотим в меню"),

    ANSWERING_ID_GET("нажали на получение Мои задачи в мероп в меню"),
    ANSWERED_ID_GET("написали id мероприятия боту -> в меню"),

    CREATE_TASK("хотим создать таску из меню -> писать id меро"),
    ANSWERED_ID_CREATE("написали id меро боту -> писать тайтл"),
    CREATE_TASK_TITLE("написали тайтл -> писать описание"),
    CREATE_TASK_DESCRIPTION("написали описание -> писать ответственного"),
    CREATE_TASK_ASSIGNED("написали ответственного -> писать цену"),
    CREATE_TASK_PRICE("написали цену -> в меню"),

    FAST_TASK("хотим создать быструю таску из меню -> написать id меро"),
    ANSWERED_ID_CREATE_FAST("написали id меро боту -> писать тайтл"),
    CREATE_FAST_TITLE("написали тайтл -> писать прайс"),
    CREATE_FAST_PRICE("написали прайс -> в меню"),

    ;

    private final String name;

    UserInput(String name) {
        this.name = name;
    }
}
