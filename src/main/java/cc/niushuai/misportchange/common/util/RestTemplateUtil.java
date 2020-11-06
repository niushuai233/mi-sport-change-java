package cc.niushuai.misportchange.common.util;

import cc.niushuai.misportchange.stepchange.bean.R;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author niushuai
 * @date 2020/5/10 13:56
 */
@Slf4j
@Configuration
public class RestTemplateUtil {

    private static RestTemplateUtil restTemplateUtil;

    @Resource
    private RestTemplate restTemplate;

    public static <T> R postWithR(String url, T param, HttpHeaders httpHeaders) {

        String paramString = JSONUtil.toJsonStr(param);

        log.info("rest send url: {}, paramString: {}", url, paramString);

        String resp = restTemplateUtil.restTemplate.postForObject(url, new HttpEntity<String>(paramString, getHttpHeaders(httpHeaders)), String.class);

        log.info("rest send url: {}, paramString: {} result: {}", url, paramString, resp);

        return JSONUtil.toBean(resp, R.class);
    }

    public static <T> String postWithString(String url, T param, HttpHeaders httpHeaders) {

        String paramString = JSONUtil.toJsonStr(param);

        String resp = restTemplateUtil.restTemplate.postForObject(url, new HttpEntity<String>(paramString, getHttpHeaders(httpHeaders)), String.class);

        return resp;
    }

    public static HttpHeaders getHttpHeaders(HttpHeaders httpHeaders) {
        if (null != httpHeaders) {
            return httpHeaders;
        }
        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Encoding", "UTF-8");
        headers.add("Content-Type", "application/json");
        headers.add("User-Agent", "clever-musu-connect");
        return headers;
    }

    public static HttpHeaders addHeader(HttpHeaders httpHeaders, String key, String val) {

        if (null == httpHeaders) {
            httpHeaders = new HttpHeaders();
        }

        httpHeaders.add(key, val);

        return httpHeaders;
    }

    @PostConstruct
    public void init() {
        restTemplateUtil = this;
        restTemplateUtil.restTemplate = restTemplate;
    }
}
