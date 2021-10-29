package cc.niushuai.misportchange.stepchange.biz;

import cc.niushuai.misportchange.common.config.RestTemplateConfig;
import cc.niushuai.misportchange.common.exception.BizException;
import cc.niushuai.misportchange.common.util.RestTemplateUtil;
import cc.niushuai.misportchange.common.util.UrlConstant;
import cc.niushuai.misportchange.stepchange.bean.MiUser;
import cc.niushuai.misportchange.stepchange.bean.Result;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.codec.EncodingException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 小米运动修改
 *
 * @author ns
 * @date 2020/11/6
 */
@Slf4j
@Component
public class MiStepBiz {

    public static final String USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 13_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/7.0.12(0x17000c2d) NetType/WIFI Language/zh_CN";

    @Resource
    private RestTemplate restTemplate;

    public void setRestTemplateBean(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public static void main(String[] args) {
        RestTemplate x = new RestTemplateConfig().restTemplate();
        MiStepBiz miStepBiz = new MiStepBiz();
        miStepBiz.restTemplate = x;
        // miStepBiz.doChange("123", 123L, "123", 123L);


        InputStream inputStream = miStepBiz.getClass().getResourceAsStream("/data_json.txt");

        List<String> lines = new ArrayList<>();
        IoUtil.readUtf8Lines(inputStream, lines);

        String dataJson = lines.get(0);
        dataJson += DateUtil.format(new Date(), "yyyy-MM-dd") + "\"}]";

        dataJson = dataJson.replace("${step}", 12321 + "").replace("${did}", "DA932FFFFE8816E7");

        HttpRequest map = HttpUtil.createPost(UrlConstant.BAND_DATA_JSON_URL + System.currentTimeMillis());
        map.form("data_json", dataJson);
        map.form("userid", "123");
        map.form("device_type", "0");
        map.form("last_sync_data_time", "1589917081");
        map.form("last_deviceid", "DA932FFFFE8816E7");

        HttpResponse execute = map.execute();

        String body = execute.body();

        System.out.println(body);

    }

    /**
     * 修改小米运动数据
     *
     * @param miUser 包含用户信息
     * @return java.lang.String
     *
     * @author ns
     * @date 2020/11/6 9:50
     **/
    public String miChange(MiUser miUser) throws Exception {

        // 1、获取access_code
        log.info("1、获取access_code");
        String code = getCode(miUser);
        log.info("code = {}", code);

        // 2、执行登陆
        log.info("2、执行登陆, 获取token");
        String[] loginRes = login(code);
        log.info("user_id = {}", loginRes[0]);
        log.info("login_token = {}", loginRes[1]);
        log.info("app_token = {}", loginRes[2]);

        // 3、获取app_token
        // log.info("3、获取app_token");
        // String appToken = getAppToken(userInfo[1]);
        // log.info("appToken = {}", appToken);

        // 4、获取淘宝的时间戳
        log.info("4、获取淘宝的时间戳");
        Long currendSecond = getTime();
        log.info("currendSecond = {}", currendSecond);

        // 5、修改步数
        log.info("5、修改步数");
        return doChange(miUser, loginRes, miUser.getStep(), currendSecond);
    }

    private String doChange(MiUser miUser, String[] loginRes, Long step, Long currentSecond) throws Exception {

        InputStream inputStream = this.getClass().getResourceAsStream("/data_json.txt");

        List<String> lines = new ArrayList<>();
        IoUtil.readUtf8Lines(inputStream, lines);

        String dataJson = lines.get(0);

        dataJson = dataJson.replace("${datetime}", DateUtil.today())
                        .replace("${step}", step + "");

        dataJson = URLDecoder.decode(dataJson, "UTF-8");

        // ==========================================================================================================

        HttpRequest post = HttpUtil.createPost(UrlConstant.BAND_DATA_JSON_URL + currentSecond);
        post.header("Content-Type", "application/x-www-form-urlencoded");
        post.header("apptoken", loginRes[2]);

        post.form("userid", loginRes[0]);
        post.form("last_sync_data_time", currentSecond - RandomUtil.randomLong(12 * 60 * 60L, 24 * 60 * 60L));
        post.form("device_type", "0");
        post.form("last_deviceid", "DA932FFFFE8816E7");
        post.form("data_json", dataJson);

        log.info("//======================================================================================================================================");
        Map<String, Object> form = post.form();
        for (String key : form.keySet()) {
            Object val = form.get(key);
            log.info("form data| key = {}, val = {}", key, val);
        }
        log.info("//======================================================================================================================================");

        HttpResponse execute = post.execute();

        log.info("doChange: {}", execute.body());

        JSONObject finalResult = JSONUtil.parseObj(execute.body());
        if (finalResult.getStr("code").equals("1") && finalResult.getStr("message").equalsIgnoreCase("success")) {
            log.info("用户: {}, 修改步数为: {}, 成功", miUser.getUsername(), miUser.getStep());
            return "";
        }

        log.info("用户: {}, 修改步数为: {}, 失败: {}", miUser.getUsername(), miUser.getStep(), execute.body());
        return execute.body();

        // ==========================================================================================================


//        // header
//        HttpHeaders httpHeaders = RestTemplateUtil.addHeader(null, "User-Agent", USER_AGENT);
//        RestTemplateUtil.addHeader(httpHeaders, "apptoken", appToken);

        // form
//        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
//        map.add("data_json", dataJson);
//        map.add("userid", userId);
//        map.add("device_type", "0");
//        map.add("last_sync_data_time", "1589917081");
//        map.add("last_deviceid", "DA932FFFFE8816E7");

//        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<MultiValueMap<String, String>>(map, httpHeaders);

        // do
//        ResponseEntity<R> responseEntity = restTemplate.postForEntity(UrlConstant.BAND_DATA_JSON_URL + timestamp, httpEntity, R.class);

//        R changeR = responseEntity.getBody();

//        log.info("changeR: {}", changeR);

//        return null;
    }

    // private String getAppToken(String loginToken) throws Exception {
    //
    //     HttpHeaders httpHeaders = RestTemplateUtil.addHeader(null, "User-Agent", USER_AGENT);
    //
    //     HttpEntity<String> requestEntity = new HttpEntity<>(null, httpHeaders);
    //     ResponseEntity<Result> responseEntity = restTemplate.exchange(UrlConstant.APP_TOKEN_URL.replace("{loginToken}", loginToken), HttpMethod.GET, requestEntity, Result.class);
    //
    //     Result appTokenR = responseEntity.getBody();
    //
    //     log.info("appTokenR: {}", appTokenR);
    //     String appTokenInfo = appTokenR.get("token_info") + "";
    //
    //     if (StrUtil.isEmpty(appTokenInfo)) {
    //         throw new BizException(50004, "获取app_token失败, 请检查账户名或密码是否正确");
    //     }
    //
    //     // = 替换为 : 便于格式化
    //     String replace = appTokenInfo.replace("=", ":");
    //     JSONObject appTokenJson = JSONUtil.parseObj(replace);
    //     log.info("app_token: {}", appTokenJson.getStr("app_token"));
    //
    //     return appTokenJson.getStr("app_token");
    // }

    private String[] login(String code) throws Exception {

        HttpHeaders httpHeaders = RestTemplateUtil.addHeader(null, "Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        RestTemplateUtil.addHeader(httpHeaders, "User-Agent", "MiFit/4.6.0 (iPhone; iOS 14.0.1; Scale/2.00");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

        map.add("app_name", "com.xiaomi.hm.health");
        map.add("app_version", "4.6.0");
        map.add("code", code);
        map.add("country_code", "CN");
        map.add("device_id", "2C8B4939-0CCD-4E94-8CBA-CB8EA6E613A1");
        map.add("device_model", "phone");
        map.add("grant_type", "access_token");
        map.add("third_name", "huami_phone");

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<MultiValueMap<String, String>>(map, httpHeaders);

        Result loginR = restTemplate.postForObject(UrlConstant.LOGIN_URL, httpEntity, Result.class);

        log.info("loginR: {}", loginR);
        String tokenInfo = loginR.get("token_info") + "";

        if (StrUtil.isEmpty(tokenInfo)) {
            throw new BizException(50003, "获取login_token失败, 请检查账户名或密码是否正确");
        }

        // = 替换为 : 便于格式化
        String replace = tokenInfo.replace("=", ":");
        JSONObject tokenJson = JSONUtil.parseObj(replace);
        log.info("login_token: {}", tokenJson.getStr("login_token"));

        return new String[]{tokenJson.getStr("user_id"), tokenJson.getStr("login_token"), tokenJson.getStr("app_token")};
    }

    private String getCode(MiUser miUser) throws Exception {

        // 构造header
        HttpHeaders httpHeaders = RestTemplateUtil.addHeader(null, "Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        RestTemplateUtil.addHeader(httpHeaders, "User-Agent", "MiFit/4.6.0 (iPhone; iOS 14.0.1; Scale/2.00");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

        map.add("client_id", "HuaMi");
        map.add("password", miUser.getPassword());
        map.add("redirect_uri", UrlConstant.REDIRECT_URL);
        map.add("token", "access");

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<MultiValueMap<String, String>>(map, httpHeaders);

        URI uri = restTemplate.postForLocation(UrlConstant.LOGIN_CODE_URL.replace("{USERNAME}", miUser.getUsername()), httpEntity);

        log.info("uri: {}", uri);

        // 得到重定向的url 获取里面的access
        // https://s3-us-west-2.amazonaws.com/hm-registration/successsignin.html?region=cn-northwest-1&access=NvtykCjHbhnFYOL8E8_SS&country_code=CN&expiration=1605504330

        // region=cn-northwest-1&access=NvtykCjHbhnFYOL8E8_SS&country_code=CN&expiration=1605504330
        String query = uri.getQuery();

        // 分隔query 获取access
        String[] split = query.split("&");
        List<String> access = Arrays.stream(split).filter(key -> key.contains("access=")).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(access)) {
            throw new BizException(50002, "获取access_code失败, 请检查账户或密码是否正确");
        }

        return access.get(0).split("=")[1];
    }


    private Long getTime() {

        try {
            Result r = restTemplate.getForObject(UrlConstant.GET_TIME_URL, Result.class);
            return JSONUtil.parseObj(r.get("data")).getLong("t") / 1000;
        } catch (RestClientException e) {
            e.printStackTrace();
            return System.currentTimeMillis() / 1000;
        }
    }
}
