package infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String HMAC_ALG = "HmacSHA256";

    private final byte[] secret;
    private final long expirationMillis;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-millis:3600000}") long expirationMillis
    ) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("La clave JWT no puede ser nula o vacía");
        }
        if (secret.length() < 32) {
            throw new IllegalArgumentException("La clave JWT debe tener al menos 32 caracteres");
        }
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationMillis = expirationMillis;
    }

    public String generateToken(String subject, List<String> roles) {
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("El subject no puede ser nulo o vacío");
        }

        long iat = Instant.now().getEpochSecond();
        long exp = iat + Math.max(1, expirationMillis / 1000);

        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payloadJson = buildPayloadJson(subject, roles != null ? roles : Collections.emptyList(), iat, exp);

        String headerB64 = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));
        String payloadB64 = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));
        String signingInput = headerB64 + "." + payloadB64;

        String signature = base64UrlEncode(hmacSha256(signingInput));
        return signingInput + "." + signature;
    }

    public JwtPayload validateAndGetPayload(String rawToken) {
        String token = normalizeToken(rawToken);

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Estructura de token inválida");
        }

        String signingInput = parts[0] + "." + parts[1];
        String expectedSig = base64UrlEncode(hmacSha256(signingInput));
        if (!constantTimeEquals(expectedSig, parts[2])) {
            throw new IllegalArgumentException("Firma inválida");
        }

        String payloadJson = new String(base64UrlDecode(parts[1]), StandardCharsets.UTF_8);

        String subject = extractString(payloadJson, "sub");
        long exp = extractLong(payloadJson, "exp");

        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Token sin subject");
        }
        if (exp <= 0 || Instant.now().getEpochSecond() >= exp) {
            throw new IllegalArgumentException("Token expirado");
        }

        List<String> roles = extractStringList(payloadJson, "roles");
        return new JwtPayload(subject, roles);
    }

    private String normalizeToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException("Token vacío o nulo");
        }
        String token = rawToken.trim();
        if (token.startsWith(BEARER_PREFIX)) {
            token = token.substring(BEARER_PREFIX.length()).trim();
        }
        if (token.isBlank()) {
            throw new IllegalArgumentException("Token vacío o nulo");
        }
        return token;
    }

    private byte[] hmacSha256(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALG);
            mac.init(new SecretKeySpec(secret, HMAC_ALG));
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Error firmando JWT", e);
        }
    }

    private static String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static byte[] base64UrlDecode(String str) {
        return Base64.getUrlDecoder().decode(str);
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) return false;
        int res = 0;
        for (int i = 0; i < a.length(); i++) {
            res |= a.charAt(i) ^ b.charAt(i);
        }
        return res == 0;
    }

    private static String buildPayloadJson(String sub, List<String> roles, long iat, long exp) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"sub\":\"").append(escape(sub)).append("\",");
        sb.append("\"iat\":").append(iat).append(",");
        sb.append("\"exp\":").append(exp).append(",");
        sb.append("\"roles\":[");
        for (int i = 0; i < roles.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append("\"").append(escape(roles.get(i))).append("\"");
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String extractString(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start < 0) return null;
        start += pattern.length();
        int end = json.indexOf('"', start);
        if (end < 0) return null;
        return json.substring(start, end).replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private static long extractLong(String json, String key) {
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern);
        if (start < 0) return -1;
        start += pattern.length();
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)))) end++;
        try {
            return Long.parseLong(json.substring(start, end));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static List<String> extractStringList(String json, String key) {
        String pattern = "\"" + key + "\":[";
        int start = json.indexOf(pattern);
        if (start < 0) return Collections.emptyList();
        start += pattern.length();
        int end = json.indexOf(']', start);
        if (end < 0) return Collections.emptyList();
        String content = json.substring(start, end).trim();
        if (content.isEmpty()) return Collections.emptyList();

        String[] parts = content.split("\\s*,\\s*");
        List<String> list = new ArrayList<>(parts.length);
        for (String p : parts) {
            if (p.startsWith("\"") && p.endsWith("\"") && p.length() >= 2) {
                list.add(p.substring(1, p.length() - 1).replace("\\\"", "\"").replace("\\\\", "\\"));
            }
        }
        return list;
    }

    public record JwtPayload(String subject, List<String> roles) {
    }
}
