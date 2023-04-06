//package com.example.shopuserservice.web.security;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKeyFactory;
//import javax.crypto.spec.PBEKeySpec;
//import java.security.NoSuchAlgorithmException;
//import java.security.spec.InvalidKeySpecException;
//import java.util.Base64;
//
//@Component
//public class PBKDF2Encoder implements PasswordEncoder {
//    @Value("${security.password.encoder.secret}")
//    private String secret;
//
//    @Value("${security.password.encoder.iteration}")
//    private int iteration;
//
//    @Value("${security.password.encoder.keylength}")
//    private int keylength;
//
//    @Override
//    public String encode(CharSequence rawPassword) {
//        try {
//            byte[] result = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
//                    .generateSecret(new PBEKeySpec(rawPassword.toString().toCharArray(), secret.getBytes(), iteration, keylength))
//                    .getEncoded();
//            return Base64.getEncoder().encodeToString(result);
//        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
//            throw new RuntimeException(ex);
//        }
//    }
//
//    /**
//     * rawPassword를 encode 메소드에 전달하여 인코딩 후, 비교한다.
//     *
//     * @param rawPassword     클라리언트로부터 전달 받을 패스워드.
//     * @param encodedPassword DB에서 가져온 패스워드.
//     * @return
//     */
//    @Override
//    public boolean matches(CharSequence rawPassword, String encodedPassword) {
//        return encode(rawPassword).equals(encodedPassword);
//    }
//}