package com.jgm.paladohorweb.tour.license;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.*;
import org.springframework.mock.web.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import static org.assertj.core.api.Assertions.*;

class JgmLicenseIntegrationTest {
    final String key="a".repeat(43);final ObjectMapper mapper=new ObjectMapper();
    HttpServer server;String base;AtomicBoolean allowed=new AtomicBoolean(false);AtomicInteger status=new AtomicInteger(200),calls=new AtomicInteger();
    AtomicReference<String> product=new AtomicReference<>("GUIA_TURISTICA"),responseTenant=new AtomicReference<>("default"),seenKey=new AtomicReference<>();
    final TestClock clock=new TestClock();JgmLicenseClient client;JgmLicenseGate gate;
    static class TestClock extends Clock{Instant now=Instant.parse("2026-10-19T02:00:00Z");public ZoneId getZone(){return ZoneOffset.UTC;}public Clock withZone(ZoneId z){return this;}public Instant instant(){return now;}void advance(long seconds){now=now.plusSeconds(seconds);}}
    @BeforeEach void setup()throws Exception{
        server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
        server.createContext("/api/integrations/license",e->{try{
            calls.incrementAndGet();seenKey.set(e.getRequestHeaders().getFirst("X-JGM-Integration-Key"));
            byte[] body=mapper.writeValueAsString(Map.of("tenant",responseTenant.get(),"productCode",product.get(),"allowed",allowed.get(),"state",allowed.get()?"CURRENT":"SUSPENDED","checkedAt",clock.instant().toString(),"validUntil",clock.instant().plusSeconds(30).toString(),"portalUrl","https://jgmsolutions.example/cliente/suscripciones")).getBytes(StandardCharsets.UTF_8);
            e.getResponseHeaders().set("Content-Type","application/json");e.sendResponseHeaders(status.get(),body.length);e.getResponseBody().write(body);
        }finally{e.close();}});server.start();base="http://127.0.0.1:"+server.getAddress().getPort();
        client=new JgmLicenseClient(true,base,key,mapper,clock);gate=new JgmLicenseGate(client,()->"default",mapper);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("owner",null,List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
    }
    @AfterEach void close(){server.stop(0);SecurityContextHolder.clearContext();}
    record Result(MockHttpServletResponse response,boolean continued){}
    Result request(String method,String path)throws Exception{var req=new MockHttpServletRequest(method,path);var res=new MockHttpServletResponse();AtomicBoolean continued=new AtomicBoolean();gate.doFilter(req,res,(a,b)->continued.set(true));return new Result(res,continued.get());}
    @Test void overdueSubscriptionBlocksWritesEvenForAdminButKeepsReadOnlyAccess()throws Exception{
        var write=request("POST","/api/v2/bookings");assertThat(write.response().getStatus()).isEqualTo(402);assertThat(write.continued()).isFalse();
        assertThat(write.response().getContentAsString()).contains("SUBSCRIPTION_REQUIRED").doesNotContain(key);
        var read=request("GET","/api/v2/bookings");assertThat(read.continued()).isTrue();assertThat(read.response().getHeader("X-JGM-License-State")).isEqualTo("SUSPENDED");assertThat(calls.get()).isEqualTo(1);assertThat(seenKey.get()).isEqualTo(key);
    }
    @Test void paymentReactivationRefreshesWithinThirtySeconds()throws Exception{
        assertThat(request("POST","/api/v2/bookings").continued()).isFalse();allowed.set(true);clock.advance(31);
        assertThat(request("POST","/api/v2/bookings").continued()).isTrue();assertThat(calls.get()).isEqualTo(2);
    }
    @Test void controlOutageFailsClosedForWritesAndPreservesConsultation()throws Exception{
        status.set(503);var write=request("POST","/api/v2/bookings");assertThat(write.response().getStatus()).isEqualTo(503);assertThat(write.continued()).isFalse();assertThat(write.response().getContentAsString()).contains("LICENSE_UNAVAILABLE");assertThat(request("GET","/api/v2/bookings").continued()).isTrue();
    }
    @Test void wrongProductOrTenantCanNeverReuseAnotherEntitlement(){
        allowed.set(true);product.set("DIFFERENT_GUIA_TURISTICA");assertThat(client.check("default").allowed()).isFalse();clock.advance(3);product.set("GUIA_TURISTICA");responseTenant.set("other-company");assertThat(client.check("default").allowed()).isFalse();
    }
    @Test void paymentAndRecoveryRoutesRemainAvailableDuringSuspension()throws Exception{
        assertThat(request("POST","/api/v2/privacy-requests").continued()).isTrue();assertThat(request("POST","/api/auth/login").continued()).isTrue();assertThat(calls.get()).isZero();
    }
    @Test void absentCredentialsOrInsecureRemoteControlUrlCannotStartManagedMode(){
        assertThatThrownBy(()->new JgmLicenseClient(true,base,"",mapper)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(()->new JgmLicenseClient(true,"http://external.example",key,mapper)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(()->new JgmLicenseClient(true,"https://user:password@external.example",key,mapper)).isInstanceOf(IllegalStateException.class);
    }
    @Test void standaloneModeDoesNotRequireAnExternalService()throws Exception{
        gate=new JgmLicenseGate(new JgmLicenseClient(false,"","",mapper),()->"default",mapper);assertThat(request("POST","/api/v2/bookings").continued()).isTrue();assertThat(calls.get()).isZero();
    }
    @Test void unauthenticatedRequestsStillReachTheExistingAuthorizationLayer()throws Exception{
        SecurityContextHolder.clearContext();assertThat(request("POST","/api/v2/bookings").continued()).isTrue();assertThat(calls.get()).isZero();
    }
}
