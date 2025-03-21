package com.kirill.tgbotidfc.stateMachine;

import lombok.Getter;

@Getter
public enum Region {
    MENU("находимся в меню"),
    GET_TASKS("ждем id мероприятия, чтобы вывести список тасок"),
    CREATE_TASK("создаем обычную таску"),
    CREATE_FAST_TASK("создаем быструю таску")

    ;

    private final String name;

    Region(String name) {
        this.name = name;
    }
}
