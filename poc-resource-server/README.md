# OAuth Resource Server 

Implements client credential flow as running code sample. 

DON'T USE IN PRODUCTION AS IT IS. 

## Sample client
client_id: "client"
client_secret: "secret"
scope: "CUSTOM"

## Curl tests

### call /demo

First, get an OAuth token from the authorization server: 

curl -X POST 'http://localhost:8080/oauth2/token?grant_type=client_credentials&scope=CUSTOM' --header 'Authorization: Basic Y2xpZW50OnNlY3JldA=='

Second, use the token in Postman as Bearer token. 








