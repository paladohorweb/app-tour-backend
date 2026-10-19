package com.jgm.paladohorweb.tour.license;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/** Executes after authentication and before authorization; existing role/owner rules remain authoritative. */
public final class JgmLicenseGate extends OncePerRequestFilter {
    private final JgmLicenseClient client;private final Supplier<String> tenant;private final ObjectMapper mapper;
    public JgmLicenseGate(JgmLicenseClient client,Supplier<String> tenant,ObjectMapper mapper){this.client=client;this.tenant=tenant;this.mapper=mapper;}
    protected boolean shouldNotFilter(HttpServletRequest r){String p=r.getServletPath().isEmpty()?r.getRequestURI():r.getServletPath();
        return !client.enabled()||r.getMethod().equals("OPTIONS")||!p.startsWith("/api/")||p.equals("/api/jgm/license")||p.startsWith("/api/auth/")||p.startsWith("/api/v2/privacy-requests")||p.startsWith("/api/v2/admin/privacy-requests")||p.startsWith("/api/v2/notifications")||p.equals("/api/v2/preferences")||p.matches("/api/v2/bookings/[^/]+/cancel");
    }
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)throws ServletException,IOException {
        var auth=SecurityContextHolder.getContext().getAuthentication();
        if(auth==null||!auth.isAuthenticated()||auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken){chain.doFilter(request,response);return;}
        String key;try{key=tenant.get();}catch(RuntimeException e){write(response,401,"TENANT_REQUIRED","No pudimos identificar la empresa de la sesión","");return;}
        JgmLicenseClient.Decision d=client.check(key);
        response.setHeader("X-JGM-License-State",d.state());
        if(d.portalUrl()!=null&&!d.portalUrl().isBlank())response.setHeader("X-JGM-Billing-Url",d.portalUrl());
        if(request.getMethod().equals("GET")||request.getMethod().equals("HEAD")||d.allowed()){chain.doFilter(request,response);return;}
        boolean unavailable=d.state().equals("CONTROL_UNAVAILABLE");
        write(response,unavailable?503:402,unavailable?"LICENSE_UNAVAILABLE":"SUBSCRIPTION_REQUIRED",
            unavailable?"No pudimos verificar el servicio. Las consultas siguen disponibles; reintenta en unos segundos.":"Servicio en modo consulta. Revisa la suscripción del negocio para recuperar la operación.",d.portalUrl());
    }
    private void write(HttpServletResponse response,int status,String code,String message,String portal)throws IOException{
        response.setStatus(status);response.setCharacterEncoding("UTF-8");response.setContentType("application/json");response.setHeader("Cache-Control","no-store");
        response.getWriter().write(mapper.writeValueAsString(Map.of("code",code,"message",message,"portalUrl",portal==null?"":portal)));
    }
}
