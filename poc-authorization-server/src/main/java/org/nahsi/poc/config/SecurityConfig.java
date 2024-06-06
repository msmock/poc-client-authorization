package org.nahsi.poc.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
public class SecurityConfig {

    /**
     * accept the default spring security setting
     */
    @Bean
    @Order(1)
    public SecurityFilterChain defaultSecuritySettings(HttpSecurity http)
            throws Exception {

        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());

        return http.build();
    }

    /**
     * @return the login form returned with GET requests. Can be removed for client credential flow.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain defaultFormBasedLogin(HttpSecurity http)
            throws Exception {

        http.formLogin(Customizer.withDefaults());
        http.authorizeHttpRequests(
                c -> c.anyRequest().authenticated());

        return http.build();
    }

    /**
     * Running code implementation. For production use a secure cryptographic
     * encoder.
     *
     * @return encoder used for storing and comparing passwords
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * Running code implementation. For production use a secure datastore (e.g., MongoDB)
     *
     * @return the registration data and the scope granted for the client
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository() {

        List<RegisteredClient> list = new ArrayList<>();

        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("client")
                .clientSecret("secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .tokenSettings(TokenSettings.builder()
                        // set the token type to opaque which requires introspection
                        // .accessTokenFormat(OAuth2TokenFormat.REFERENCE)
                        .accessTokenTimeToLive(Duration.ofMinutes(5))
                        .build())
                // set the scope granted for the client
                .scope("CUSTOM") // scopes must be claimed in request
                .scope("MIX")
                .build();

        list.add(client);

        RegisteredClient resourceServer = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("resource-server")
                .clientSecret("resource-server-secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope("CUSTOM")
                .build();

        list.add(resourceServer);

        return new InMemoryRegisteredClientRepository(list);
    }

    /**
     * Running code implementation. For production use the public key shall be
     * stored in a secure vault.
     *
     * @return the cryptographic key used to sign the token
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() throws NoSuchAlgorithmException {

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();

        JWKSet jwkSet = new JWKSet(rsaKey);

        return new ImmutableJWKSet<>(jwkSet);
    }

    /**
     * The general app settings to configure the endpoints the app exposes.
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }
}
