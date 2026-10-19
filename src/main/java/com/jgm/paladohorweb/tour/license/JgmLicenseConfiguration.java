package com.jgm.paladohorweb.tour.license;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@Configuration
public class JgmLicenseConfiguration {
    @Bean JgmLicenseClient jgmLicenseClient(@Value("${JGM_LICENSE_ENABLED:false}") boolean enabled,
            @Value("${JGM_CONTROL_URL:}") String url,@Value("${JGM_INTEGRATION_KEY:}") String key,ObjectMapper mapper){
        return new JgmLicenseClient(enabled,url,key,mapper);
    }
    @RestController
    public static class LicenseStatus {
        private final JgmLicenseClient client;
        public LicenseStatus(JgmLicenseClient client ){this.client=client;}
        @GetMapping("/api/jgm/license") @PreAuthorize("isAuthenticated()")
        public JgmLicenseClient.Decision status(){return client.check("default");}
    }
}
