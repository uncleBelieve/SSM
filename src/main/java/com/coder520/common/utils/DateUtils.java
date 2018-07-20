package com.coder520.common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *@Author HJY
 *@Date
 *@Description:时间工具类
*/
public class DateUtils {
    private static Calendar calendar = Calendar.getInstance();//该类专门用于搞日期

     /**
     *@Author HJY
     *@Date
     *@Description 得到今天是周几
     */
      public static int getTodayWeek(){
          Calendar calendar = Calendar.getInstance(); //该类专门用于搞日期 本方法获得一个Calendar类型的通用对象
          calendar.setTime(new Date());//new Date 就是最新的时间
          int week = calendar.get(Calendar.DAY_OF_WEEK)-1;//-1后返回的是当前星期几   国外0是周日
          if(week<0) week=7;//-1为周日
          return week;
    }

    /**
     *@Author HJY
     *@Date
     *@Description 计算时间差 分钟数
     * */
     public static  int getMunite(Date startDate,Date endDate){

        long start = startDate.getTime();  //转成毫秒
        long end = endDate.getTime();
        int munite =  (int)(end-start)/(1000*60);//转分钟
        return munite;
    }


    /**
     *@Author
     *@Date
     *@Description 获取当天某个时间
     */
    public static Date getDate(int hour,int minute){

        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        return  calendar.getTime();
    }
}
