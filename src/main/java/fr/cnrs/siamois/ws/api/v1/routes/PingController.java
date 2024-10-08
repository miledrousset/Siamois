package fr.cnrs.siamois.ws.api.v1.routes;

import fr.cnrs.siamois.ws.api.helper.CustomMediaType;
import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(methods = { RequestMethod.GET })
public class PingController {

    @GetMapping(value = "ping", produces = CustomMediaType.APPLICATION_JSON_UTF_8)
    public ResponseEntity<String> ping() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("message", "pong");
        return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(builder.build().toString());
    }

}
