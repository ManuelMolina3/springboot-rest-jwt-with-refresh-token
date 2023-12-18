package net.openwebinars.springboot.restjwt.security.jwt.refresh;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import net.openwebinars.springboot.restjwt.user.model.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repositorio;

    @Value{"${jwt.refresh.duration}"}
    private int durationInMin;

    public Optional<RefreshToken> findByToken (String token){
    return repositorio.findByToken(token);
    }
    public int deleteByUser(User user){
        return repositorio.deleteByUser(user);
    }
    public RefreshToken createdRefreshToken (User user){
        RefreshToken refreshToken= new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusSeconds(durationInMin*60));
        refreshToken= repositorio.save( refreshToken);
        return refreshToken;
    }
    public RefreshToken verify (RefreshToken refreshToken){
        if (refreshToken.getExpiryDate().compareTo(Instant.now())>0){
            repositorio.delete(refreshToken);
            throw new RefreshTokenException("Expired refresh token: " + refreshToken.getToken() + "Please, login again");
        }
        return refreshToken;

    }
}