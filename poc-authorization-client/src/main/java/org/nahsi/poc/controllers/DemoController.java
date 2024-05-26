package org.nahsi.poc.controllers;

import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

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
        return authorizedClient.getAccessToken().getTokenValue();
    }
}
