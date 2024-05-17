# Authorization Server 

Implements client credential flow as running code sample. 

DON'T USE IN PRODUCTION AS IT IS. 

## Sample client
client_id: "client"
client_secret: "secret"
scope: "CUSTOM"

## Curl tests

### get a token

curl -X POST 'http://localhost:8080/oauth2/token?grant_type=client_credentials&scope=CUSTOM' --header 'Authorization: Basic Y2xpZW50OnNlY3JldA=='

### verify a token

curl -X POST 'http://localhost:8080/oauth2/introspect?token=RZiiLUW6uP_rGK2QMrgZUQ1ZSC5l6xGonS2H5b0YK8BppzmX8rkmY6pbKahOevN55THdRnDOuEeKNNAUmCMhs-0wSNBedUO8KZUeePw0Vo1PDiU94q1PuSJyG9olgaXh' --header 'Authorization: Basic Y2xpZW50OnNlY3JldA=='

Note: If the token is invalid (signature, lifetime, ...), the authorization server instrospection responds with: {"active": false,}