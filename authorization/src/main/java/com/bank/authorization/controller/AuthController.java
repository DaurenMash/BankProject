//package com.bank.authorization.controller;
//
//import com.bank.authorization.dto.AuthRequest;
//import com.bank.authorization.dto.AuthResponse;
//import com.bank.authorization.repository.UserRepository;
//import com.bank.authorization.service.CustomUserDetailsService;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.beans.factory.annotation.Value;
//
//import java.util.Collection;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/auth")
//public class AuthController {
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    private CustomUserDetailsService customUserDetailsService;
//
//    @Autowired
//    private UserRepository userRepository;
//@Value("${app.jwt.secret-key}")
//private String secretKey;
//
//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
//        try {
//            Authentication authenticate = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
//            SecurityContextHolder.getContext().setAuthentication(authenticate);
//
//            Collection<? extends GrantedAuthority> authorities = authenticate.getAuthorities();
//            String jwt = generateJwtToken(authRequest.getUsername(), authorities);
//
//            Map<String, Object> data = new HashMap<>();
//            data.put("jwt", jwt);
//            data.put("authorities", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
//
//            return ResponseEntity.ok(new AuthResponse(data));
//        } catch (BadCredentialsException ex) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//        }
//    }
//
//    private String generateJwtToken(String username, Collection<? extends GrantedAuthority> authorities) {
//        long now = System.currentTimeMillis();
//        Date expiryDate = new Date(now + 3600000); // Срок действия токена - 1 час
//
//        return Jwts.builder()
//                .setSubject(username)
//                .claim("authorities", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
//                .setIssuedAt(new Date(now))
//                .setExpiration(expiryDate)
//                .signWith(SignatureAlgorithm.HS512, secretKey.getBytes())
//                .compact();
//    }
//}