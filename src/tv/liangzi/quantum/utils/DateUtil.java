package tv.liangzi.quantum.utils;

/**
 * Created by invinjun on 2015/6/3.
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    private static SimpleDateFormat sf = null;

    /*获取系统时间 格式为："yyyy/MM/dd "*/
    public static String getCurrentDate() {
        Date d = new Date();
        sf = new SimpleDateFormat("yyyy年MM月dd日");
        return sf.format(d);
    }

    /*时间戳转换成字符窜*/
    public static String getDateToString(long time) {
        Date d = new Date(time);
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(d);
        String week=new SimpleDateFormat("EEEE").format(calendar.getTime());
        return week;
    }
    /*时间戳转换成字符窜*/
    public static String getDateToHourString(long time) {
        Date d = new Date(time);
        sf = new SimpleDateFormat("HH:mm");
        return sf.format(d);
    }

//    /**
//     * 获得一个日期所在的周的星期几的日期，如要找出2002年2月3日所在周的星期一是几号
//     *
//     * @param sdate
//     * @param num
//     * @return
//     */
//    public static String getWeek(String sdate, String num) {
//        // 再转换为时间
//        Date dd = strToDateLong(sdate);
//        Calendar c = Calendar.getInstance();
//        c.setTime(dd);
//        if (num.equals("1")) // 返回星期一所在的日期
//            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
//        else if (num.equals("2")) // 返回星期二所在的日期
//            c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
//        else if (num.equals("3")) // 返回星期三所在的日期
//            c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
//        else if (num.equals("4")) // 返回星期四所在的日期
//            c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
//        else if (num.equals("5")) // 返回星期五所在的日期
//            c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
//        else if (num.equals("6")) // 返回星期六所在的日期
//            c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
//        else if (num.equals("0")) // 返回星期日所在的日期
//            c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
//        return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
//    }
//
    /**
     * 根据一个日期，返回是星期几的字符串
     *
     * @param
     * @return
     */
    public static String getTodayWeek() {
        // 再转换为时间
        Date date = new Date(getCurrentDate());
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // int hour=c.get(Calendar.DAY_OF_WEEK);
        // hour中存的就是星期几了，其范围 1~7
        // 1=星期日 7=星期六，其他类推
        return new SimpleDateFormat("EEEE").format(c.getTime());
    }
    public static String getWeekStr(String sdate){
        String str = "";
//        str = .getWeek(sdate);
        if("1".equals(str)){
            str = "星期日";
        }else if("2".equals(str)){
            str = "星期一";
        }else if("3".equals(str)){
            str = "星期二";
        }else if("4".equals(str)){
            str = "星期三";
        }else if("5".equals(str)){
            str = "星期四";
        }else if("6".equals(str)){
            str = "星期五";
        }else if("7".equals(str)){
            str = "星期六";
        }
        return str;
    }

    /*将字符串转为时间戳*/
    public static long getStringToDate(String time) {
        sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        try{
            Calendar c = Calendar.getInstance();
           int year = c.get(Calendar.YEAR);
            date = sf.parse(year+"-"+time);
           long dates= Calendar.getInstance().getTimeInMillis();
        } catch(ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * 直接获取时间戳
     * @return
     */
    public static String getTimeStamp() {
        String currentDate = getCurrentDate();
        sf = new SimpleDateFormat("EEEE");
        Date date = new Date();
        try{
            date = sf.parse(currentDate);
        } catch(ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(date.getTime());
    }

}
