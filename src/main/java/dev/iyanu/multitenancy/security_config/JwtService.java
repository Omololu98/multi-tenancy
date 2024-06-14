package dev.iyanu.multitenancy.security_config;

import dev.iyanu.multitenancy.tenant.users.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
       final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }

    public String extractTenantId(String token){
        return extractClaim(token, Claims::getIssuer);
    }

    private Date extractExpirationDate(String token){
        return extractClaim(token,Claims::getExpiration);
    }

    public boolean isTokenExpired(String token){
        return extractExpirationDate(token).before(new Date());
    }

    public String generateToken(User user){
        return buildJwt(new HashMap<>(),user);
    }

    public String generateToken(Map<String,Object> extractedClaims, User user){
        return buildJwt(extractedClaims,user);
    }

    public String buildJwt(Map<String, Object> extractedClaims, User user){
        return Jwts.builder()
                .claims(extractedClaims)
                .issuer(user.getTenantId())
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1800000))
                .signWith(getSignKey())
                .compact();
    }

    private SecretKey getSignKey(){
        final String SECRET_KEY = "llFn1dkt7pHzRL1FG5sF39eMxHZMrsrae4g33RQwqB7XT3vlWUT8";
        byte[] key = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(key);

    }
}
