package com.sq.base.config.security;

import io.jsonwebtoken.*;

import java.util.Date;

/**
 * @Description: 生成token工具类  // 类说明，在创建类时要填写
 * @ClassName: JwtTokenUtils    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/26 10:30   // 时间
 * @Version: 1.0     // 版本
 */
public class JwtTokenUtils {
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String SECRET = "jwtsecret";
    public static final String ISS = "echisan";

    private static final Long EXPIRATION = 60 * 60 * 3L; //过期时间3小时

    private static final String ROLE = "role";

    //创建token
    public static String createToken(BaseTokenInfo baseTokenInfo){
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .setClaims(baseTokenInfo.getExtraMap())
                .setIssuer(ISS)
                //这里也可以直接传tokenInfo的json串
                .setSubject(baseTokenInfo.getUserName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + baseTokenInfo.getExpiration()))
                .compact();
    }

    //从token中获取用户名(此处的token是指去掉前缀之后的)
    public static String getUserName(String token){
        String username;
        try {
            username = getTokenBody(token).getSubject();
        } catch (    Exception e){
            username = null;
        }
        return username;
    }

    public static String getUserRole(String token){
        return (String) getTokenBody(token).get(ROLE);
    }

    private static Claims getTokenBody(String token){
        Claims claims = null;
        try{
            claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        } catch(ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e){
            e.printStackTrace();
        }
        return claims;
    }

    //是否已过期
    public static boolean isExpiration(String token){
        try{
            return getTokenBody(token).getExpiration().before(new Date());
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return true;
    }
}
