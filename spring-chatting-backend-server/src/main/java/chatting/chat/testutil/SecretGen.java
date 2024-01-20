package chatting.chat.testutil;

import java.security.SecureRandom;

public class SecretGen {
    public static String generateRandomString(int byteLength) {
        if (byteLength <= 0 || byteLength % 2 != 0) {
            throw new IllegalArgumentException("바이트 길이는 양수이어야 하며, 짝수여야 합니다.");
        }

        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[byteLength / 2];
        secureRandom.nextBytes(randomBytes);

        // 바이트 배열을 16진수 문자열로 변환
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : randomBytes) {
            stringBuilder.append(String.format("%02x", b));
        }

        return stringBuilder.toString();
    }

}
