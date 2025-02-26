package fr.siamois.ws.api.v1.routes;

import fr.siamois.services.ark.ArkRedirectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/ark:/{naan}/{qualifier}")
public class ArkController {

    private final ArkRedirectionService arkRedirectionService;

    public ArkController(ArkRedirectionService arkRedirectionService) {
        this.arkRedirectionService = arkRedirectionService;
    }

    @GetMapping
    public ResponseEntity<Void> redirectToRessource(@PathVariable String naan, @PathVariable String qualifier) {
        Optional<URI> uri = arkRedirectionService.getResourceUriFromArk(naan, qualifier);

        ServletUriComponentsBuilder notFoundBuilder = ServletUriComponentsBuilder.fromCurrentContextPath();
        notFoundBuilder.path("/pages/error/error-404.xhtml");

        return uri.<ResponseEntity<Void>>map(
                value -> ResponseEntity
                        .status(HttpStatus.FOUND)
                        .location(value)
                        .build()
                )
                .orElseGet(
                        () -> ResponseEntity
                                .status(HttpStatus.MOVED_PERMANENTLY)
                                .location(notFoundBuilder.build().toUri())
                                .build()
                );
    }

}
