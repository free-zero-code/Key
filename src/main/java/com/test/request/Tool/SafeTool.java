package com.test.request.Tool;

import com.xyzq.xzehome.sm3.SM3Digest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class SafeTool {
    public String getServiceSign(Map<String, Object> json_map, String salt) {
        String result = "";
        List<String> list = mapToList(json_map);
        Collections.sort(list);
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s);
        }
        String tmp = sb.toString() + salt;
        result = SM3Digest.sm3EncodeUtf8(tmp);
        return result;
    }

    /**
     * 处理MAP的值中存有MAP的情况
     * @param json_map
     * @return
     */
    private List<String> mapToList(Map<String, Object> json_map) {
        List<String> list = new ArrayList<String>();
        for (Map.Entry<String, Object> entry : json_map.entrySet()) {
            if (entry.getValue() instanceof Map) {
                list.addAll(mapToList((Map<String, Object>)entry.getValue()));
            } else {
                list.add(entry.getKey() + "=" + entry.getValue());
            }
        }
        return list;
    }
}

