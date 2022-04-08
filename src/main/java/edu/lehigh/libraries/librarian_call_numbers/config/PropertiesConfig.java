package edu.lehigh.libraries.librarian_call_numbers.config;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix="librarian-call-numbers")
@Validated
@Getter @Setter
public class PropertiesConfig {

    @AssertTrue
    @NotNull
    private Boolean enabled;

    private Folio folio;
    
    @Getter @Setter
    public static class Folio {

        /**
         * FOLIO API username
         */
        private String username;

        /**
         * FOLIO API password
         */
        private String password;

        /**
         * FOLIO API tenant ID
         */
        private String tenantId;

        /**
         * FOLIO API base OKAPI url
         */
        private String okapiBaseUrl;
    }

}
