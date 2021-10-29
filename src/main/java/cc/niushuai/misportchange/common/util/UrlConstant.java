package cc.niushuai.misportchange.common.util;

/**
 * @author ns
 * @date 2020/11/6
 */
public class UrlConstant {
    // 获取code
    public static final String LOGIN_CODE_URL = "https://api-user.huami.com/registrations/+86{USERNAME}/tokens";
    public static final String REDIRECT_URL = "https://s3-us-west-2.amazonaws.com/hm-registration/successsignin.html";

    // 获取token
    public static final String LOGIN_URL = "https://account.huami.com/v2/client/login";

    // 最终请求
    public static final String BAND_DATA_JSON_URL = "https://api-mifit-cn.huami.com/v1/data/band_data.json?&t=";

    // public static final String APP_TOKEN_URL = "https://account-cn.huami.com/v1/client/app_tokens?app_name=com.xiaomi.hm.health&dn=api-user.huami.com%2Capi-mifit.huami.com%2Capp-analytics.huami.com&login_token={loginToken}&os_version=4.1.0";

    // 获取时间
    public static final String GET_TIME_URL = "http://api.m.taobao.com/rest/api3.do?api=mtop.common.getTimestamp";
}
