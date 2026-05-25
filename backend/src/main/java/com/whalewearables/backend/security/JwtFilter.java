package com.whalewearables.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;


    public JwtFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService)
    {
        this.jwtUtil = jwtUtil;
        // Note: UserDetailsService intentionally removed from filter.
        // Roles come from JWT claims → no DB call per request (true stateless).
        // Trade-off: role changes take effect only on token refresh.
        // If you need instant role revocation, re-add loadUserByUsername() here.
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        String token = extractTokenFromCookies(req);


        if (token != null && jwtUtil.validateToken(token)) {
            String username = jwtUtil.extractUsername(token);
            // ← Roles from JWT claims, not DB — true stateless RBAC
            List<GrantedAuthority> authorities = jwtUtil.extractAuthorities(token);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        chain.doFilter(req, res);
    }

    private String extractTokenFromCookies(HttpServletRequest req) {
        if (req.getCookies() == null) return null;
        for (Cookie c : req.getCookies()) {
            if ("jwt".equals(c.getName())) return c.getValue();
        }
        return null;
    }
}
