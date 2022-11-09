package com.test.request.SystemDataQuery;

import com.google.gson.Gson;
import com.test.request.GlobalException.BusinessException;
import com.test.request.Tool.SafeTool;
import com.test.request.Tool.SafeUtilAES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class SystemDataQuery {

    @Autowired
    private SafeTool safeTool;

    public String Search(Map<String, Object> data) throws BusinessException {
        OutputStreamWriter out = null;
        BufferedReader br = null;
        StringBuilder result = new StringBuilder();
        try {
            Gson gson = new Gson();
            data = getMapValueForHashMap(data);
            String signKey = (String) data.get("signKey");
            data.remove("signKey");
            String aesKey = (String) data.get("aesKey");
            data.remove("aesKey");
            String pathUrl = (String) data.get("pathUrl");
            data.remove("pathUrl");
            String user_password = (String) data.get("user_password");
            String user_code = (String) data.get("user_code");
            if (pathUrl == null || pathUrl.length() == 0) {
                throw new BusinessException("【IP】不能为空");
            }
            if (signKey==null || signKey.length() == 0) {
                throw new BusinessException("【signKey】不能为空");
            }
            if (aesKey==null&&pathUrl.contains("/baseapp/smartAcquiringRestApi/loginVerification")) {
                throw new BusinessException("【aesKey】不能为空");
            } else if (aesKey != null) {
                if (aesKey.length() == 0&&pathUrl.contains("/baseapp/smartAcquiringRestApi/loginVerification")) {
                    throw new BusinessException("【aesKey】不能为空");
                }
            }
            data.put("ts", String.valueOf(System.currentTimeMillis() / 1000));
            if (pathUrl.contains("/baseapp/smartAcquiringRestApi/loginVerification")) {
                if (user_code == null) {
                    throw new BusinessException("请求JSON中【user_code】不能为空");
                }
                if (user_password == null) {
                    throw new BusinessException("请求JSON中【user_password】不能为空");
                }
                user_password = SafeUtilAES.Encryption(user_password,aesKey);
                user_code = SafeUtilAES.Encryption(user_code,aesKey);
                data.put("user_code",user_code);
                data.put("user_password",user_password);
            }
            String sign = safeTool.getServiceSign(data, signKey);
            data.put("sign", sign);
            URL url = new URL(pathUrl);
            //打开和url之间的连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //请求方式
            conn.setRequestMethod("POST");
            //设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:103.0) Gecko/20100101 Firefox/103.0");
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            //DoOutput设置是否向httpUrlConnection输出，DoInput设置是否从httpUrlConnection读入，此外发送post请求必须设置这两个
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
            //发送请求参数即数据
            out.write(gson.toJson(data));
            //flush输出流的缓冲
            out.flush();
            //获取URLConnection对象对应的输入流
            InputStream is = conn.getInputStream();
            //构造一个字符流缓存
            br = new BufferedReader(new InputStreamReader(is));
            String str = "";
            while ((str = br.readLine()) != null) {
                result.append(str);
            }
            System.out.println(result);
            //关闭流
            is.close();
            //断开连接，disconnect是在底层tcp socket链接空闲时才切断，如果正在被其他线程使用就不切断。
            conn.disconnect();
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                throw new BusinessException(e.getMessage());
            }
        }
        return result.toString();
    }

    public String key(Map<String, Object> data) throws BusinessException {
        String signKey = "";
        if (data.get("signKey")!=null && data.get("signKey").toString().length() > 0) {
            signKey = String.valueOf(data.get("signKey"));
        } else {
            throw new BusinessException("【signKey】不能为空");
        }
        data.remove("signKey");
        return safeTool.getServiceSign(data, signKey);
    }

    public String aes_d(Map<String, Object> data) throws BusinessException, GeneralSecurityException {
        String aesKey = "";
        if (data.get("aesKey")!=null && data.get("aesKey").toString().length() > 0) {
            aesKey = String.valueOf(data.get("aesKey"));
        } else {
            throw new BusinessException("【aesKey】不能为空");
        }
        String value = "";
        if (data.get("value")!=null && data.get("value").toString().length() > 0) {
            value = String.valueOf(data.get("value"));
        } else {
            throw new BusinessException("【value】不能为空");
        }
        return SafeUtilAES.Decryption(value,aesKey);
    }

    public String aes_e(Map<String, Object> data) throws BusinessException, GeneralSecurityException {
        String aesKey = "";
        if (data.get("aesKey")!=null && data.get("aesKey").toString().length() > 0) {
            aesKey = String.valueOf(data.get("aesKey"));
        } else {
            throw new BusinessException("【aesKey】不能为空");
        }
        String value = "";
        if (data.get("value")!=null && data.get("value").toString().length() > 0) {
            value = String.valueOf(data.get("value"));
        } else {
            throw new BusinessException("【value】不能为空");
        }
        return SafeUtilAES.Encryption(value,aesKey);
    }

    /**
     * 将Map转换为HashMap并排除空数据
     * @param dataMap
     * @return
     */
    private Map<String,Object> getMapValueForHashMap(Map<String,Object> dataMap) {
        Map<String,Object> returnMap = new HashMap<>();
        if (dataMap == null || dataMap.isEmpty()) {
            return returnMap;
        }
        for (String objKey : dataMap.keySet()) {
            Object objValue = dataMap.get(objKey);
            if (objValue instanceof Map) {
                if (((Map<String, Object>) objValue).size() == 0) {
                    continue;
                } else {
                    Map<String,Object> objValueMap = getMapValueForHashMap((Map<String, Object>) objValue);
                    if (objValueMap.size() == 0) {
                        continue;
                    } else {
                        returnMap.put(objKey, objValueMap);
                    }
                }
            } else {
                if (objValue == null || objValue.toString().length() == 0) {
                    continue;
                } else {
                    returnMap.put(objKey, objValue);
                }
            }
        }
        return returnMap;
    }

}
