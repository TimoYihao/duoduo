package com.xxx.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;

import java.net.URLEncoder;
import java.util.*;

public class PDDUtils {

    /** 获取商品列表 */
    public static List getGoodsList(String keyword,String sort){
        List<Map> lists = new ArrayList<>();
        try {
            for (int i = 1; i < 11; i++) {
                String url = "http://gw-api.pinduoduo.com/api/router";
                StringBuilder params = new StringBuilder();
                //Map<String, Object> m = new HashMap<>();
                TreeMap<String, String> m = new TreeMap<String, String>();
                m.put("keyword",keyword);
                m.put("type","pdd.ddk.goods.search");
                m.put("client_id",DictionaryUtils.duoduo_client_id);
                m.put("timestamp",DateUtils.getTimeStamp());
                m.put("page", String.valueOf(i));
                m.put("sort_type",sort);
                Set<String> keys = m.keySet();
                for (String key : keys) {
                    params.append(key);
                    params.append("=");
                    String value = m.get(key);
                    if (key.equals("keyword")) {
                        value = URLEncoder.encode(value, "UTF-8");
                    }
                    params.append(value);
                    params.append("&");
                }
                params.append("sign="+getSign(m,DictionaryUtils.duoduo_client_secret));
                String result = HttpClientUtils.post(url, params.toString(), "application/x-www-form-urlencoded","UTF-8", 10000, 10000);
                JSONObject jsonObject = JSONObject.parseObject(result);
                jsonObject = JSONObject.parseObject(jsonObject.get("goods_search_response").toString());
                result = jsonObject.get("goods_list").toString();
                List<Map> list = JSON.parseArray(result,Map.class);
                for(Map l:list){
                    lists.add(l);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lists;
    }

    /**
     * 签名的算法
     * */
    public static String getSign(TreeMap<String,String> m, String secret) {
        StringBuilder sb = new StringBuilder(secret);
        Set<String> keys = m.keySet();
        for (String key : keys) {
            sb.append(key);
            sb.append(m.get(key));
        }
        sb.append(secret);
        return DigestUtils.md5Hex(sb.toString()).toUpperCase();
    }


    public static void main(String[] args) {
        getGoodsList("男上衣","");
    }


}
