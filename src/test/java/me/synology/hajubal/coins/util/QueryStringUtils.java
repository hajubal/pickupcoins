package me.synology.hajubal.coins.util;

import org.apache.commons.beanutils.BeanUtils;

import java.net.URLEncoder;
import java.util.Map;

public class QueryStringUtils {

    public static String toQueryString(Object obj) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : BeanUtils.describe(obj).entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value != null) {
                if (first) {
                    first = false;
                } else {
                    result.append("&");
                }

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value, "UTF-8"));
            }
        }

        return result.toString();
    }
}
