package com.test.request.Controller;

import com.google.gson.Gson;
import com.test.request.GlobalException.BusinessException;
import com.test.request.SystemDataQuery.SystemDataQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DataController {

    @Autowired
    private SystemDataQuery systemDataQuery;

    @PostMapping(value = "/inquiry",consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
    public String post_data(@RequestBody Map<String,Object> data) throws BusinessException {
        return systemDataQuery.Search(data);
    }

    @PostMapping(value = "/key",consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
    public String post_key(@RequestBody Map<String,Object> data) throws BusinessException {
        Gson gson = new Gson();
        Map<String,String> signKey_map = new HashMap<>();
        signKey_map.put("signKey",systemDataQuery.key(data));
        return gson.toJson(signKey_map);
    }

    @PostMapping(value = "/aesD",consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
    public String post_aesD(@RequestBody Map<String,Object> data) throws BusinessException, GeneralSecurityException {
        Gson gson = new Gson();
        Map<String,String> key_map = new HashMap<>();
        key_map.put("aesD",systemDataQuery.aes_d(data));
        return gson.toJson(key_map);
    }

    @PostMapping(value = "/aesE",consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
    public String post_aesE(@RequestBody Map<String,Object> data) throws BusinessException, GeneralSecurityException {
        Gson gson = new Gson();
        Map<String,String> key_map = new HashMap<>();
        key_map.put("aesE",systemDataQuery.aes_e(data));
        return gson.toJson(key_map);
    }

}
