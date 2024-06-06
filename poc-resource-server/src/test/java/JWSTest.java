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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * JWS playground test
 */
public class JWSTest {


    @Test
    public void testRSA() throws JOSEException, ParseException, JsonProcessingException {

        // RSA signatures require a public and private RSA key pair,
        // the public key must be made known to the JWS recipient to
        // allow the signatures to be verified
        RSAKey rsaKey = new RSAKeyGenerator(2048)
                .keyID("a917cf8c-f06d-40fc-82d8-26891237c681")
                .generate();

        // Create RSA-signer with the private key
        JWSSigner signer = new RSASSASigner(rsaKey);

        String json = buildPayload();

        // Prepare JWS object with simple string as payload
        JWSObject jwsObject = new JWSObject(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaKey.getKeyID()).build(),
                new Payload(json));

        // Compute the RSA signature
        jwsObject.sign(signer);

        // Serialize to compact form
        String serialized = jwsObject.serialize();

        // get the public key
        RSAKey rsaPublicKey = rsaKey.toPublicJWK();

        assertTrue(isVerified(serialized, rsaPublicKey));
    }

    /**
     * verify the signature of the token
     *
     * @param serialized   the serialized form of the JWS token
     * @param rsaPublicKey the public key
     * @return true, if verified
     */
    private static boolean isVerified(String serialized, RSAKey rsaPublicKey) throws ParseException, JOSEException {

        // To parse the JWS and verify it, e.g. on client-side
        JWSObject jwsObjectToCheck = JWSObject.parse(serialized);

        JWSVerifier verifier = new RSASSAVerifier(rsaPublicKey);

        return jwsObjectToCheck.verify(verifier);
    }


    private static String buildPayload() throws JsonProcessingException {

        // create `ObjectMapper` instance
        ObjectMapper mapper = new ObjectMapper();

        Instant now = Instant.now(); // unix time in seconds
        Instant expires = Instant.now().plus(1, ChronoUnit.HOURS);

        // create the JSON object
        ObjectNode accessToken = mapper.createObjectNode();
        accessToken.put("sub", "client");
        accessToken.put("aud", "client");

        accessToken.put("iat", now.getEpochSecond());
        accessToken.put("nbf", now.getEpochSecond());
        accessToken.put("exp", expires.getEpochSecond());

        accessToken.put("iss", "http://localhost:8080");
        accessToken.put("jti", UUID.randomUUID().toString());

        ArrayNode scope = mapper.createArrayNode();
        scope.add("CUSTOM");

        accessToken.putIfAbsent("scope", scope);

        // convert `ObjectNode` to pretty-print JSON
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(accessToken);
    }


}
