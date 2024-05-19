package org.nahsi.service.poc.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@EnableMethodSecurity
@PreAuthorize("hasAuthority('SCOPE_MIX')") // SCOPE_{scope value in token}
public class ProtectedResourceController {

    @GetMapping("/protected")
    public String demo(BearerTokenAuthentication authentication) {

        // TODO evaluate token attributes

        Map<String, Object> tokenAttributes = authentication.getTokenAttributes();
        System.out.println(">>> subject : "+ tokenAttributes.get("sub") );
        System.out.println(">>> audience : "+ tokenAttributes.get("aud") );

        return "Protected endpoint, for scope MIX only ...";
    }
}
