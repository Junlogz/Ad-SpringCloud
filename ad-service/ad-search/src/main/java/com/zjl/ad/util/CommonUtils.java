package com.zjl.ad.util;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author: JunLog
 * @Description: *
 * Date: 2022/7/26 19:30
 */
public class CommonUtils {

    public static <K, V> V getorCreate(K key, Map<K, V> map,
                                       Supplier<V> factory) {
        return map.computeIfAbsent(key, k -> factory.get());
    }

    /**
     * 关联的string使用 例如create_id-unit_Id关联一起做key
     * @param args
     * @return
     */
    public static String stringConcat(String... args) {

        StringBuilder result = new StringBuilder();
        for (String arg : args) {
            result.append(arg);
            result.append("-");
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

}
