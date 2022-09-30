package xyz.current.crm.comments.utils;

import java.util.UUID;

public class UUIDUtils {

    /**
     * 获取UUID字符串
     * @return
     */
    public static String getUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
}
