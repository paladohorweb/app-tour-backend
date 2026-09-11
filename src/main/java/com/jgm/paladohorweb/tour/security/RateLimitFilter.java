package com.jgm.paladohorweb.tour.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.concurrent.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(-110)
public class RateLimitFilter extends OncePerRequestFilter {
  private final ConcurrentHashMap<String, long[]> counters = new ConcurrentHashMap<>();

  protected void doFilterInternal(HttpServletRequest r, HttpServletResponse p, FilterChain c)
      throws ServletException, IOException {
    if (r.getMethod().equals("OPTIONS") || !r.getRequestURI().startsWith("/api/")) {
      c.doFilter(r, p);
      return;
    }
    long now = System.currentTimeMillis();
    boolean auth = r.getRequestURI().startsWith("/api/auth/");
    String key = r.getRemoteAddr() + (auth ? ":auth" : ":api");
    int max = auth ? 10 : 240;
    boolean allowed;
    synchronized (counters) {
      counters.entrySet().removeIf(e -> now - e.getValue()[0] >= 60000);
      if (counters.size() >= 10000 && !counters.containsKey(key)) {
        allowed = false;
      } else {
        long[] v = counters.computeIfAbsent(key, k -> new long[] {now, 0});
        allowed = ++v[1] <= max;
      }
    }
    if (!allowed) {
      p.setStatus(429);
      p.setHeader("Retry-After", "60");
      p.setContentType("application/json");
      p.getWriter().write("{\"message\":\"Demasiadas solicitudes. Intenta en un minuto.\"}");
      return;
    }
    c.doFilter(r, p);
  }
}
