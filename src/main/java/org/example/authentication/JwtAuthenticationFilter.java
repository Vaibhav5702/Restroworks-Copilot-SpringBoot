package org.example.authentication;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.header.string}")
    public String HEADER_STRING;

    @Value("${jwt.token.prefix}")
    public String TOKEN_PREFIX;

    @Resource(name = "userService")
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenProvider jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        String header = req.getHeader(HEADER_STRING);
        String username = null;
        String authToken = null;

        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            authToken = header.replace(TOKEN_PREFIX + " ", "");

            try {
                username = jwtTokenUtil.getUsernameFromToken(authToken);
            } catch (IllegalArgumentException e) {
                logger.error("Error occurred while retrieving Username from Token", e);
            } catch (ExpiredJwtException e) {
                logger.warn("The token has expired", e);
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setContentType("application/json");
                res.getWriter().write("{\"error\": \"Token has expired\"}");
                return;
            } catch (SignatureException e) {
                logger.error("Authentication Failed. Invalid username or password.");
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setContentType("application/json");
                res.getWriter().write("{\"error\": \"Authentication Failed. Invalid username or password.\"}");
                return;
            }
        } else {
            logger.warn("Bearer string not found, ignoring the header");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = jwtTokenUtil.getAuthenticationToken(authToken, SecurityContextHolder.getContext().getAuthentication(), userDetails);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                logger.info("User authenticated: " + username + ", setting security context");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(req, res);
    }
}
