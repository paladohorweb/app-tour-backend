package com.jgm.paladohorweb.tour.security.jwt;
import com.jgm.paladohorweb.tour.security.CustomUserDetailsService;
import jakarta.servlet.*; import jakarta.servlet.http.*; import java.io.IOException;
import lombok.RequiredArgsConstructor; import org.springframework.stereotype.Component;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;
@Component @RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
 private final JwtProvider jwtProvider; private final CustomUserDetailsService users;
 protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain) throws ServletException,IOException {
  String header=req.getHeader("Authorization");
  if(header!=null && header.startsWith("Bearer ")) {
   try {
    String token=header.substring(7);
    if(!jwtProvider.validateToken(token)) throw new IllegalArgumentException();
    var user=users.loadUserByUsername(jwtProvider.getEmailFromToken(token));
    if(!user.isEnabled() || !user.isAccountNonLocked()) throw new IllegalArgumentException();
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities()));
   } catch(Exception ex) {SecurityContextHolder.clearContext();res.setStatus(401);res.setContentType("application/json");res.getWriter().write("{\"message\":\"Sesión inválida o expirada\"}");return;}
  }
  chain.doFilter(req,res);
 }
}
