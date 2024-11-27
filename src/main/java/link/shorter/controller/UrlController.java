package link.shorter.controller;

import link.shorter.dto.ShortUrlDto;
import link.shorter.dto.UrlCreatedDTO;
import link.shorter.model.ShortUrl;
import link.shorter.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping()
@Slf4j
public class UrlController {
    private final UrlService urlService;

    @PostMapping()
    public ShortUrlDto create(@RequestBody UrlCreatedDTO dto) {
        log.info("Received request to create short URL for: {}", dto.getUrl());
        ShortUrlDto shortUrlDto = urlService.create(dto);
        log.info("Short URL created: {}", shortUrlDto.getUrl());
        return shortUrlDto;
    }

    @GetMapping(path = "/{path}")
    public ResponseEntity<Void> redirect(@PathVariable String path) {
        log.info("Received request to redirect for path: {}", path);
        ShortUrl shortUrl = urlService.getByUrl(path);
        log.info("Redirecting to original URL: {}", shortUrl.getOriginUrl().getUrl());

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(shortUrl.getOriginUrl().getUrl()))
                .build();
    }

    @DeleteMapping(path = "/delete/short-url/{id}")
    public void deleteShortUrl(@PathVariable Long id) {
        log.info("Received request to delete short URL with ID: {}", id);
        urlService.deleteShortUrlById(id);
        log.info("Short URL with ID: {} deleted successfully", id);
    }

    @DeleteMapping(path = "/delete/origin-url/{id}")
    public void deleteOriginUrl(@PathVariable Long id) {
        log.info("Received request to delete origin URL with ID: {}", id);
        urlService.deleteOriginUrlById(id);
        log.info("Origin URL with ID: {} deleted successfully", id);
    }
}
