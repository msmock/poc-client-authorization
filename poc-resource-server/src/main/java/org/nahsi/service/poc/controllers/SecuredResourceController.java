package org.nahsi.service.poc.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableMethodSecurity
@PreAuthorize("hasAuthority('SCOPE_CUSTOM')") // SCOPE_{scope value in token}
public class SecuredResourceController {

    @GetMapping("/secured")
    public String getTokenData() {
        return "Secured endpoint, for scope CUSTOM only ...";
    }

}

