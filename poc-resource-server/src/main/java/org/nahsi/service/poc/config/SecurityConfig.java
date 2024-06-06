package org.nahsi.service.poc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebSecurity
@EnableWebMvc
public class SecurityConfig {

    @Value("${introspectionUri}")
    private String introspectionUri;

    @Value("${clientID}")
    private String resourceServerClientID;

    @Value("${clientSecret}")
    private String resourceServerSecret;

    // the url to lookup the public key
    @Value("${keySetURI}")
    private String keySetUri;

    @Bean
    @Order(1)
    public SecurityFilterChain setIntrospection(HttpSecurity http) throws Exception {

        // uncomment to activate introspection
        /** http.oauth2ResourceServer(
         c -> c.opaqueToken(
         o -> o.introspectionUri(introspectionUri)
         .introspectionClientCredentials(resourceServerClientID, resourceServerSecret)
         )
         );**/

        // use lookup for the public key at authZ server
        http.oauth2ResourceServer(
                c -> c.jwt(
                        j -> j.jwkSetUri(keySetUri)
                )
        );

        http.authorizeHttpRequests(
                c -> c.anyRequest().authenticated()
        );

        return http.build();
    }

}
