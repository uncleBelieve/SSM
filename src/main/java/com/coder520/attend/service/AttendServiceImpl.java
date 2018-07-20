package com.coder520.attend.service;
import com.coder520.attend.dao.AttendMapper;
import com.coder520.attend.entity.Attend;
import com.coder520.attend.vo.QueryCondition;
import com.coder520.common.page.PageQueryBean;
import com.coder520.common.utils.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service("attendServiceImpl")
public class  AttendServiceImpl implements AttendService {

    /**
     *@Author HJY
     *@Date
     *@Description:中午十二点，用来判定上下午
    */
    private static final int NOON_MINUTES =00 ;
    private static final int NOON_HOUR=12 ;
    private  Log log = LogFactory.getLog(AttendServiceImpl.class);



    /**
     * 早晚上班时间判定
     */
    private static final int MORNING_HOUR = 9;
    private static final int MORNING_MINUTE = 30;
    private static final int EVENING_HOUR = 18;
    private static final int EVENING_MINUTE = 30;

    /**
     * 缺勤一整天
     */
    private static final Integer ABSENCE_DAY =480 ;
    /**
     * 考勤异常状态
     */
    private static final Byte ATTEND_STATUS_ABNORMAL = 2;
    private static final Byte ATTEND_STATUS_NORMAL = 1;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

   @Autowired
    private AttendMapper attendMapper;

    @Override
    public void signAttend(Attend attend) throws Exception{
        try {
            Date today = new Date();//最新时间，本处为打卡时间
            attend.setAttendDate(today);
            attend.setAttendWeek((byte)DateUtils.getTodayWeek());

            //查询当天此人有没有打过卡，通过数据库查询
            Attend todayRecord=attendMapper.selectTodaySignRecord(attend.getUserId());
            Date noon = DateUtils.getDate(NOON_HOUR,NOON_MINUTES);
            Date morningAttend = DateUtils.getDate(MORNING_HOUR,MORNING_MINUTE);
            if(todayRecord==null){//打卡记录不存在
                //打卡记录还不存在
                if(today.compareTo(noon)<=0){//today 早于noon,即打卡时间早于12点，上午打卡
                    //打卡时间 早于12点 上午打卡
                    attend.setAttendMorning(today);
                    //计算打卡时间是不是迟到
                    if(today.compareTo(morningAttend)>0){
                        //大于九点半迟到了
                        attend.setAttendStatus(ATTEND_STATUS_ABNORMAL);
                        attend.setAbsence(DateUtils.getMunite(morningAttend,today));//时间差
                    }

                }else {
                    attend.setAttendEvening(today);//晚上打卡
                }
                attendMapper.insertSelective(attend);
            }else{//如果打卡记录存在
                if(today.compareTo(noon)<=0){//today 早于noon,即打卡时间早于12点，上午打卡
                    //打卡时间 早于12点 上午打卡
                    return;
                }else {
                    //晚上打卡
                    todayRecord.setAttendEvening(today);
                    //判断打卡时间是不是18.30以后是不是早退
                    Date eveningAttend = DateUtils.getDate(EVENING_HOUR,EVENING_MINUTE);
                    if(today.compareTo(eveningAttend)<0){
                        //早于下午六点半 早退
                        todayRecord.setAttendStatus(ATTEND_STATUS_ABNORMAL);
                        todayRecord.setAbsence(DateUtils.getMunite(today,eveningAttend));
                    }else {
                        todayRecord.setAttendStatus(ATTEND_STATUS_NORMAL);
                        todayRecord.setAbsence(0);
                    }
                    attendMapper.updateByPrimaryKeySelective(todayRecord);
                }
            }
            //中午十二点前打卡都算早(attend_morning)打卡 9:30以后直接异常
            // 十二点以后下午打卡，18：00之前异常 打多次卡直接覆盖之前打卡记录，只算最后一次
            // 下午打卡时应该检查与上午打卡时间差，不足8小时异常，缺席时间也要记录
        }catch (Exception e){
            log.error("用户签到异常",e);
            throw e;
        }


    }

    @Override
    public PageQueryBean listAttend(QueryCondition condition) {
        //根据条件查询count记录数目，如果有记录才查询分页数据
        int count = attendMapper.countByCondition(condition);
        PageQueryBean pageResult = new PageQueryBean();
        if(count>0){
           pageResult.setTotalRows(count);
           pageResult.setCurrentPage(condition.getCurrentPage());
           pageResult.setPageSize(condition.getPageSize());
           List<Attend> attendList =attendMapper.selectAttendPage(condition);
           pageResult.setItems(attendList);//查询出来每页的10条数据记录集合
        }
        return pageResult;//返回查询到的每页的10条结果返回给前端，postman可以用json看结果，前端就可以配合js完成，比如选中按钮高亮
      }



    /**
     * @Description: 检查考勤异常数据
     * @author: wangjianbin
     * @Param:  []
     * @Return: void
     * @Date:   15:30 2017/6/26
     */
    @Override//定时器方法
    public void checkAttend() {
        //查询缺勤用户ID 插入打卡记录  并且设置为异常 缺勤480分钟
        List<Long> userIdList =attendMapper.selectTodayAbsence();
        if(CollectionUtils.isNotEmpty(userIdList)){
            List<Attend> attendList = new ArrayList<>();//缺勤人数集合
            for(Long userId:userIdList){
                //把当天时间设置进去添加异常信息
                Attend attend = new Attend();
                attend.setUserId(userId);
                attend.setAttendDate(new Date());
                attend.setAttendWeek((byte)DateUtils.getTodayWeek());
                attend.setAbsence(ABSENCE_DAY);
                attend.setAttendStatus(ATTEND_STATUS_ABNORMAL);
                attendList.add(attend);
            }
            attendMapper.batchInsert(attendList);
        }
        // 检查晚打卡 将下班未打卡记录设置为异常
        List<Attend> absenceList = attendMapper.selectTodayEveningAbsence();
        if(CollectionUtils.isNotEmpty(absenceList)){
            for(Attend attend:absenceList){
                attend.setAbsence(ABSENCE_DAY);
                attend.setAttendStatus(ATTEND_STATUS_ABNORMAL);
                attendMapper.updateByPrimaryKeySelective(attend);
            }
        }
    }


}
