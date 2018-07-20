package com.coder520.workflow.service;

import com.coder520.workflow.entity.ReAttend;
import org.activiti.engine.task.Task;

import java.util.List;
import java.util.Map;

public interface ReAttendService {

    void startReAttendFlow(ReAttend reAttend);

    List<ReAttend> listTasks(String username);

    void approve(ReAttend reAttend);

    List<ReAttend> listReAttend(String username);
}
