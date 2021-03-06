package uk.gov.hmcts.reform.professionalapi.configuration;

import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "security")
@Slf4j
public class AuthCheckerConfiguration {

    List<String> authorisedServices;
    List<String> authorisedRoles = new ArrayList<>();

    public List<String> getAuthorisedServices() {
        return authorisedServices;
    }

    public void setAuthorisedServices(List<String> authorisedServices) {
        this.authorisedServices = authorisedServices;
    }

    public List<String> getAuthorisedRoles() {
        return authorisedRoles;
    }

    @Bean
    public Function<HttpServletRequest, Collection<String>> authorizedServicesExtractor() {
        log.info(String.format("Configured authorised services: %s", String.join(", ", authorisedServices)));
        return any -> ImmutableSet.copyOf(authorisedServices);
    }

    @Bean
    public Function<HttpServletRequest, Collection<String>> authorizedRolesExtractor() {
        return any -> ImmutableSet.copyOf(authorisedRoles);
    }

    @Bean
    public Function<HttpServletRequest, Optional<String>> userIdExtractor() {
        return any -> Optional.empty();
    }
}
