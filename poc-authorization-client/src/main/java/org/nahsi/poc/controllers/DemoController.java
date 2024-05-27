package org.nahsi.poc.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoController.class);

    private final OAuth2AuthorizedClientManager clientManager;

    public DemoController(OAuth2AuthorizedClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @GetMapping("/token")
    public String token() {

        var request = OAuth2AuthorizeRequest
                .withClientRegistrationId("1")
                .principal("client")
                .build();

        var authorizedClient = clientManager.authorize(request);

        assert authorizedClient != null;

        var token = authorizedClient.getAccessToken();

        LOGGER.debug("requested token from authorization server");
        LOGGER.debug("token type is: {}", token.getTokenType().getValue());
        LOGGER.debug("token scopes are: {}", token.getScopes());

        return token.getTokenValue();
    }
}
