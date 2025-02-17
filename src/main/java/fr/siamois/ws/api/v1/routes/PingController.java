package fr.siamois.ws.api.v1.routes;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PingController {

    @GetMapping(value = "ping", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> ping() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("message", "pong");
        return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(builder.build().toString());
    }

}
