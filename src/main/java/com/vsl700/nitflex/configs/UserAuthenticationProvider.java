package com.vsl700.nitflex.configs;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.vsl700.nitflex.models.Privilege;
import com.vsl700.nitflex.models.User;
import com.vsl700.nitflex.models.dto.UserDTO;
import com.vsl700.nitflex.repo.UserRepository;
import com.vsl700.nitflex.services.UserService;
import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@PropertySource("classpath:application.security.properties")
@Component
public class UserAuthenticationProvider {
    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @PostConstruct
    protected void init() {
        // this is to avoid having the raw secret key available in the JVM
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String login) {
        Date now = new Date();
        //Date validity = new Date(now.getTime() + 3600000L * 24 * 28); // 28 days

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create()
                .withSubject(login)
                .withIssuedAt(now)
                //.withExpiresAt(validity)
                .sign(algorithm);
    }

    public Authentication validateToken(String token) throws JWTVerificationException { // TODO Add exception handler for JWTVerificationException
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        JWTVerifier verifier = JWT.require(algorithm)
                .build();

        DecodedJWT decoded = verifier.verify(token);

        User user = userRepo.findByUsername(decoded.getSubject()).orElseThrow(); // TODO Add custom exception
        List<Privilege> userPrivileges = user.getRole().getPrivileges();
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return new UsernamePasswordAuthenticationToken(userDTO, null, userPrivileges.stream()
                .map(p -> new SimpleGrantedAuthority("ROLE_" + p.getName()))
                .toList());
    }
}
