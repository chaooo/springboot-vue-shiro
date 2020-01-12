package top.itdn.serverend.service;

import java.util.HashMap;
import java.util.Map;

/**
 * HashMap模拟用户数据
 *
 * @author : Charles
 * @date : 2020/1/12
 */
public class DataMap {
    private static Map<String, Map<String, String>> data = new HashMap<>();

    static {
        Map<String, String> data1 = new HashMap<>();
        data1.put("password", "123456");
        data1.put("role", "admin");
        data1.put("permission", "view,edit");

        Map<String, String> data2 = new HashMap<>();
        data2.put("password", "123456");
        data2.put("role", "user");
        data2.put("permission", "view");

        data.put("admin", data1);
        data.put("user1", data2);
    }

    public static Map<String, Map<String, String>> getData() {
        return data;
    }
}
