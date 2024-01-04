package com.github.xuchen93.web.jwt;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.xuchen93.core.config.XuchenProperties;
import com.github.xuchen93.model.ex.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class JwtService {

	private static Algorithm ALGORITHM;
	private static JWTVerifier verifier;
	@Autowired
	XuchenProperties xuchenProperties;

	@PostConstruct
	public void init() {
		ALGORITHM = Algorithm.HMAC256(xuchenProperties.getJwt().getSecret());
		verifier = JWT.require(ALGORITHM).build();
		log.info("【xuchen-module-web】注入【jwt-service】");
	}

	public String generateToken(Object user) {
		ReflectUtil.setFieldValue(user, "password", null);
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
			throw new AuthException(4003, "token过期");
		} catch (Exception e) {
			throw new AuthException(4001, "token解析失败");
		}
		return JSONUtil.parseObj(jwt.getClaim("customer_user").asString());
	}
}
