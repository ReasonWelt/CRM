package xyz.current.crm.comments.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/*
对date类型数据惊醒处理的工具类
 */
public class DateUtils {
    /***
     *对指定date对象进行格式化yyyy-Mm-dd HH:mm:ss
     * @param date
     * @return
     */
    public static String formatDateTime(Date date){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sf.format(date);
        return dateStr;
    }
}
