package cc.niushuai.projects.misportchange.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ns
 * @date 2020/11/6
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {

        NoRedirectClientHttpRequestFactory requestFactory = new NoRedirectClientHttpRequestFactory();

        requestFactory.setConnectTimeout(15000);
        requestFactory.setReadTimeout(15000);

        RestTemplate restTemplate = new RestTemplate(requestFactory);

        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        messageConverters.add(new WMappingJackson2HttpMessageConverter());

        // String转换器
        StringHttpMessageConverter stringConvert = new StringHttpMessageConverter();
        List<MediaType> stringMediaTypes = new ArrayList<MediaType>() {{
            //添加响应数据格式，不匹配会报401
            add(MediaType.TEXT_PLAIN);
            add(MediaType.TEXT_HTML);
            add(MediaType.APPLICATION_JSON);
        }};
        stringConvert.setSupportedMediaTypes(stringMediaTypes);
        messageConverters.add(stringConvert);

        restTemplate.setMessageConverters(messageConverters);

        return restTemplate;
    }

    public static class NoRedirectClientHttpRequestFactory extends SimpleClientHttpRequestFactory {

        @Override
        protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
            super.prepareConnection(connection, httpMethod);
            // 禁止自动重定向
//            System.out.println(connection.getURL().getPath() + " disable redirect");
            HttpURLConnection.setFollowRedirects(false);
        }
    }

    public static class WMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
        public WMappingJackson2HttpMessageConverter() {
            List<MediaType> mediaTypes = new ArrayList<>();
            mediaTypes.add(MediaType.TEXT_PLAIN);
            mediaTypes.add(MediaType.TEXT_HTML);
            mediaTypes.add(MediaType.APPLICATION_JSON);
            mediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
            setSupportedMediaTypes(mediaTypes);
        }
    }
}
