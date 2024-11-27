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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    @Mock
    private ShortUrlRepository shortUrlRepository;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlMapper urlMapper;
    @Mock
    private ShortUrlMapper shortUrlMapper;

    @InjectMocks
    private UrlService urlService;

    private UrlCreatedDTO urlCreatedDTO;
    private Url url;
    private ShortUrl shortUrl;
    private ShortUrlDto shortUrlDto;

    @BeforeEach
    void setUp() {
        urlCreatedDTO = new UrlCreatedDTO("http://example.com", JsonNullable.of("example"));
        url = new Url();
        url.setId(1L);
        url.setReadablePart("example");
        shortUrl = new ShortUrl();
        shortUrl.setUrl("http://localhost:8080/example");
        shortUrl.setOriginUrl(url);
        shortUrlDto = new ShortUrlDto("http://localhost:8080/example");
    }

    @Test
    void testCreate() {
        when(urlRepository.existsByUrl(urlCreatedDTO.getUrl())).thenReturn(false);
        when(urlMapper.map(urlCreatedDTO)).thenReturn(url);
        when(urlRepository.save(url)).thenReturn(url);
        when(shortUrlRepository.existsByUrl(anyString())).thenReturn(false);
        when(shortUrlMapper.map(any(ShortUrl.class))).thenReturn(shortUrlDto);
        when(shortUrlRepository.save(any(ShortUrl.class))).thenReturn(shortUrl);

        ShortUrlDto result = urlService.create(urlCreatedDTO);

        assertNotNull(result);
        assertEquals(shortUrlDto.getUrl(), result.getUrl());
        verify(urlRepository).save(url);
        verify(shortUrlRepository).save(any(ShortUrl.class));
    }

    @Test
    void testCreateWithInvalidData() {
        when(urlMapper.map(urlCreatedDTO)).thenReturn(url);
        when(urlRepository.save(url)).thenReturn(url);
        when(shortUrlRepository.existsByUrl(anyString())).thenReturn(true);

        ShortUrlException exception = assertThrows(ShortUrlException.class, () -> {
            urlService.create(urlCreatedDTO);
        });

        assertEquals("example is already in use", exception.getMessage());
    }

    @Test
    void testGetByUrl() {
        when(shortUrlRepository.findByUrl("http://localhost:8080/example")).thenReturn(Optional.of(shortUrl));

        ShortUrl result = urlService.getByUrl("example");

        assertNotNull(result);
        assertEquals(shortUrl.getUrl(), result.getUrl());
    }

    @Test
    void testGetByUrlWithNonExistsUrl() {
        when(shortUrlRepository.findByUrl("http://localhost:8080/example")).thenReturn(Optional.empty());

        ShortUrlNotFoundException exception = assertThrows(ShortUrlNotFoundException.class, () -> {
            urlService.getByUrl("example");
        });

        assertEquals("http://localhost:8080/example not found!", exception.getMessage());
    }

    @Test
    void testDeleteShortUrlById() {
        Long id = 1L;

        urlService.deleteShortUrlById(id);

        verify(shortUrlRepository).deleteById(id);
    }

    @Test
    void testDeleteOriginUrlById() {
        Long id = 1L;

        urlService.deleteOriginUrlById(id);

        verify(urlRepository).deleteById(id);
    }
}