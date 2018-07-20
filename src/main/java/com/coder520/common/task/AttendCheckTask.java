package com.coder520.common.task;

import com.coder520.attend.service.AttendService;
import org.springframework.beans.factory.annotation.Autowired;

public class AttendCheckTask {


    @Autowired
    private AttendService attendService;


    public void checkAttend()
    {
        //首先获取今天没打卡的人，给他插入打卡记录，并且设为缺席480min

       // 如果有打卡记录检查早晚打卡
        System.out.println("定时器");
        attendService.checkAttend();
    }
}
