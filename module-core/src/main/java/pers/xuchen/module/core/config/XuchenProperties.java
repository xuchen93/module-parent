package pers.xuchen.module.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "xuchen.module")
public class XuchenProperties {
    private RequestModel request = new RequestModel();
    private RedisModel redis = new RedisModel();
    private JwtModel jwt = new JwtModel();

    @Data
    public static class RequestModel{
        /**
         * 请求日志
         */
        private boolean log = false;
        private boolean bindCheck = false;
        /**
         * 请求详情
         */
        private boolean detail = false;
    }

    @Data
    public static class RedisModel{
        private String prefix;
    }

    @Data
    public static class JwtModel{
        private String tokenKey = "Authorization";
        private String secret = "defaultSecret";
        private int expiresMin = 120;

        //加密对象的字段名
        private String userId = "id";
        private String userName = "userName";
        private String nickName = "nickName";
    }
}
