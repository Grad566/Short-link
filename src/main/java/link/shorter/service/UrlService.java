package link.shorter.service;

import link.shorter.dto.ShortUrlDto;
import link.shorter.dto.UrlCreatedDTO;
import link.shorter.exception.ShortUrlException;
import link.shorter.exception.ShortUrlNotFoundException;
import link.shorter.mapper.ShortUrlMapper;
import link.shorter.mapper.UrlMapper;
import link.shorter.model.ShortUrl;
import link.shorter.model.Url;
import link.shorter.repository.ShortUrlRepository;
import link.shorter.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class UrlService {
    private final ShortUrlRepository shortUrlRepository;
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;
    private final ShortUrlMapper shortUrlMapper;

    public ShortUrlDto create(UrlCreatedDTO dto) {
        log.info("Creating short URL for: {}", dto.getUrl());
        Url url;

        if (urlRepository.existsByUrl(dto.getUrl())) {
            url = urlRepository.getUrlByUrl(dto.getUrl());
            log.info("Found existing URL: {}", url.getUrl());
        } else {
            url = urlMapper.map(dto);
            urlRepository.save(url);
            log.info("Saved new URL: {}", url.getUrl());
        }

        ShortUrl shortUrl = new ShortUrl();
        String shortLink = generateShortLink(url);
        log.info("Generated short link: {}", shortLink);

        if (shortUrlRepository.existsByUrl(shortLink)) {
            log.error("Short URL {} is already in use", url.getReadablePart());
            throw new ShortUrlException(url.getReadablePart() + " is already in use");
        }

        shortUrl.setUrl(shortLink);
        shortUrl.setOriginUrl(url);
        shortUrlRepository.save(shortUrl);
        log.info("Short URL created: {}", shortUrl.getUrl());

        return shortUrlMapper.map(shortUrl);
    }

    public ShortUrl getByUrl(String readablePart) {
        String shortLink = getBaseUrl() + "/" + readablePart;
        log.info("Retrieving original URL for short link: {}", shortLink);

        return shortUrlRepository.findByUrl(shortLink)
                .orElseThrow(() -> {
                    log.error("Short URL {} not found", shortLink);
                    return new ShortUrlNotFoundException(shortLink + " not found!");
                });
    }

    public void deleteShortUrlById(Long id) {
        log.info("Deleting short URL with ID: {}", id);
        shortUrlRepository.deleteById(id);
        log.info("Short URL with ID: {} deleted successfully", id);
    }

    public void deleteOriginUrlById(Long id) {
        log.info("Deleting origin URL with ID: {}", id);
        urlRepository.deleteById(id);
        log.info("Origin URL with ID: {} deleted successfully", id);
    }

    private String generateShortLink(Url origin) {
        String readablePart = origin.getReadablePart() == null || origin.getReadablePart().isEmpty()
                ? origin.getId().toString() + UUID.randomUUID().toString().replace("-", "").substring(0, 7)
                : origin.getReadablePart();

        return getBaseUrl() + "/" + readablePart;
    }

    private String getBaseUrl() {
        String host = System.getenv().getOrDefault("SERVER_HOST", "localhost:8080");
        String protocol = System.getenv().getOrDefault("PROTOCOL", "http");
        return protocol + "://" + host;
    }

}
