package org.nahsi.service.poc.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableMethodSecurity
@PreAuthorize("hasAuthority('SCOPE_MIX')") // SCOPE_{scope value in token}
public class ProtectedResourceController {

    @GetMapping("/protected")
    public String demo(Authentication a) {

        System.out.println(a);

        return "Protected endpoint, for scope MIX only ...";
    }
}
