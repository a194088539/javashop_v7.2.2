//package com.enation.app.javashop.framework.util;
//
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.JWTVerifier;
//import com.auth0.jwt.algorithms.Algorithm;
//import com.auth0.jwt.exceptions.JWTCreationException;
//import com.auth0.jwt.exceptions.JWTDecodeException;
//import com.auth0.jwt.exceptions.JWTVerificationException;
//import com.auth0.jwt.interfaces.Claim;
//import com.auth0.jwt.interfaces.DecodedJWT;
//import com.enation.app.javashop.framework.util.DateUtil;
//
//import java.io.UnsupportedEncodingException;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by kingapex on 2018/3/8.
// * jwt token工具类
// * @author kingapex
// * @version 1.0
// * @since 6.4.0
// * 2018/3/8
// */
//public class JwtUtil {
//    private static final String SECRET = "XX#$%()(#*!()!KL<><MQLMNQNQJQK sdfkjsdrow32234545fdf>?N<:{LWPW";
//
//    private static final String EXP = "exp";
//
//    private static final String PAYLOAD = "payload";
//
//    //加密，传入一个对象和有效期
//    public static   String sign( ) {
//        try {
//            Algorithm algorithm = Algorithm.HMAC256(SECRET);
//
//            Map<String, Object> headerClaims = new HashMap();
//            headerClaims.put("username", "kingapex");
//            headerClaims.put("role", "seller");
//            Date date  = DateUtil.toDate("2018-03-08 17:58","yyyy-MM-DD HH:mm");
//            String token = JWT.create()
////                    .withIssuer("auth0").withClaim("userid", "abc")
//                    .withHeader(headerClaims)
////                    .withExpiresAt(date)
//                    .sign(algorithm);
//            return  token;
//        } catch (UnsupportedEncodingException exception){
//             return null;
//        } catch (JWTCreationException exception){
//            return null;
//        }
//    }
//
//
//    public  static DecodedJWT verify(String token){
//        try {
//            Algorithm algorithm = Algorithm.HMAC256(SECRET);
//            JWTVerifier verifier = JWT.require(algorithm)
////                    .withIssuer("auth0")
//                    .build(); //Reusable verifier instance
//
//            DecodedJWT jwt = verifier.verify(token);
//
//
//            return  jwt;
//        } catch (UnsupportedEncodingException e){
//            e.printStackTrace();
//            //UTF-8 encoding not supported
//        } catch (JWTVerificationException e){
//
//            e.printStackTrace();
//        }
//
//        return null;
//
//    }
//
//    public static void main(String[] args) {
//
//        String token  = sign();
//        System.out.println(token);
//
//        verify(token);
//
//
//    }
//}
