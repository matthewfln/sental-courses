package security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenUtil {

    private static final Logger LOGGER = LogManager.getLogger(JwtTokenUtil.class);
    private static final String SECRET_STRING = "VerySecretKeyForOurSenlaBookstoreProjectMustBeLongEnough";
    private final Key key = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());
    private final long validityInMilliseconds = 3600000; // 1 час

    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            LOGGER.error("Невалидный или истекший JWT токен: {}", e.getMessage());
            return false;
        }
    }
}
