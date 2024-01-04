package pers.xuchen.module.web.jwt;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.xuchen.module.core.config.XuchenProperties;
import pers.xuchen.module.core.model.ex.AuthException;

import javax.annotation.PostConstruct;

@Service
public class JwtService {

    @Autowired
    XuchenProperties xuchenProperties;

    private static Algorithm ALGORITHM;
    private static JWTVerifier verifier;

    @PostConstruct
    public void init() {
        ALGORITHM = Algorithm.HMAC256(xuchenProperties.getJwt().getSecret());
        verifier = JWT.require(ALGORITHM).build();
    }

    public String generateToken(Object user) {
        DateTime expireTime = DateUtil.offsetMinute(DateUtil.date(), xuchenProperties.getJwt().getExpiresMin());
        String token = JWT.create()
                .withClaim("customer_user", JSONUtil.toJsonStr(user))
                .withIssuer("moduleServer")
                .withSubject("moduleSubject")
                .withIssuedAt(DateUtil.date())
                //过期时间
                .withExpiresAt(expireTime)
                .sign(ALGORITHM);
        return token;
    }

    public JSONObject parseToken(String token) {
        DecodedJWT jwt;
        try {
            jwt = verifier.verify(token);
        } catch (TokenExpiredException tokenExpiredException) {
            throw new AuthException(4000, "token过期");
        } catch (Exception e) {
            throw new AuthException(4001, "token解析失败");
        }
        return JSONUtil.parseObj(jwt.getClaim("customer_user").asString());
    }
}
