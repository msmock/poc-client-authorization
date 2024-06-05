import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class JWSTest {


    @Test
    public void testRSA() throws JOSEException, ParseException, JsonProcessingException {

        // RSA signatures require a public and private RSA key pair,
        // the public key must be made known to the JWS recipient to
        // allow the signatures to be verified
        RSAKey rsaJWK = new RSAKeyGenerator(2048)
                .keyID("123")
                .generate();

        RSAKey rsaPublicJWK = rsaJWK.toPublicJWK();

        // Create RSA-signer with the private key
        JWSSigner signer = new RSASSASigner(rsaJWK);


        // create `ObjectMapper` instance
        ObjectMapper mapper = new ObjectMapper();

        long now = System.currentTimeMillis()/100;
        long exp = now + 12000;

        // create a JSON object
        ObjectNode accessToken = mapper.createObjectNode();
        accessToken.put("sub", "client");
        accessToken.put("aud", "client");

        accessToken.put("iat", String.valueOf(now));
        accessToken.put("nbf", String.valueOf(now));
        accessToken.put("exp", String.valueOf(exp));

        accessToken.put("iss", "http://localhost:8080");
        accessToken.put("jti", UUID.randomUUID().toString());

        ArrayNode scope = mapper.createArrayNode();
        scope.add("CUSTOM");

        accessToken.putIfAbsent("scope", scope);

        // convert `ObjectNode` to pretty-print JSON
        // without pretty-print, use `user.toString()` method
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(accessToken);

        // Prepare JWS object with simple string as payload
        JWSObject jwsObject = new JWSObject(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build(),
                new Payload(json));

        // Compute the RSA signature
        jwsObject.sign(signer);

        // To serialize to compact form, produces something like:
        // eyJhbGciOiJSUzI1NiJ9.SW4gUlNBIHdlIHRydXN0IQ.IRMQENi4nJyp4er2L
        // mZq3ivwoAjqa1uUkSBKFIX7ATndFF5ivnt-m8uApHO4kfIFOrW7w2Ezmlg3Qd
        // maXlS9DhN0nUk_hGI3amEjkKd0BWYCB8vfUbUv0XGjQip78AI4z1PrFRNidm7
        // -jPDm5Iq0SZnjKjCNS5Q15fokXZc8u0A
        String s = jwsObject.serialize();

        // To parse the JWS and verify it, e.g. on client-side
        jwsObject = JWSObject.parse(s);

        JWSVerifier verifier = new RSASSAVerifier(rsaPublicJWK);

        assertTrue(jwsObject.verify(verifier));

    }


}
