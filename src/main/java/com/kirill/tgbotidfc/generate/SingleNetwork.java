package com.kirill.tgbotidfc.generate;

import com.kirill.tgbotidfc.dto.TaskDTO;
import com.kirill.tgbotidfc.stateMachine.StateMachine;

import java.util.HashMap;
import java.util.Map;

public class SingleNetwork {
    public static Map<Long, TaskDTO> dataTasks = new HashMap<>();
    public static StateMachine stateMachine;
}
