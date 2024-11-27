package link.shorter;

import link.shorter.dto.UrlCreatedDTO;
import link.shorter.model.ShortUrl;
import link.shorter.model.Url;
import link.shorter.repository.ShortUrlRepository;
import link.shorter.repository.UrlRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ShorterApplicationTests {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ShortUrlRepository shortUrlRepository;

	@Autowired
	private UrlRepository urlRepository;

	@Autowired
	private ObjectMapper om;

	private ShortUrl testShortUrl;

	private Url testUrl;

	@ServiceConnection
	@Container
	public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
			"postgres:latest")
			.withDatabaseName("links")
			.withUsername("admin")
			.withPassword("qwerty");

	@BeforeEach
	void setUp() {
		testUrl = new Url();
		testUrl.setUrl("http://originalUrl.com");
		urlRepository.save(testUrl);

		testShortUrl = new ShortUrl();
		testShortUrl.setUrl("http://localhost:8080/shortUrl");
		testShortUrl.setOriginUrl(testUrl);
		shortUrlRepository.save(testShortUrl);
	}

	@AfterEach
	void clean() {
		urlRepository.delete(testUrl);
		shortUrlRepository.delete(testShortUrl);
	}

	@Test
	void createShortUrl() throws Exception {
		UrlCreatedDTO dto = new UrlCreatedDTO();
		dto.setUrl("http://newOriginalUrl.com");

		mockMvc.perform(post("/")
						.contentType(MediaType.APPLICATION_JSON)
						.content(om.writeValueAsString(dto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.url").value(notNullValue()));
	}

	@Test
	void redirectToNonExistentShortUrl() throws Exception {
		String nonExistentPath = "nonExistentShortUrl";

		mockMvc.perform(get("/" + nonExistentPath))
				.andExpect(status().isNotFound());
	}

	@Test
	void redirectToOriginalUrl() throws Exception {
		mockMvc.perform(get("/" + "shortUrl"))
				.andExpect(status().isFound())
				.andExpect(header().string("Location", testUrl.getUrl()));
	}

	@Test
	void deleteShortUrl() throws Exception {
		mockMvc.perform(delete("/delete/short-url/" + testShortUrl.getId()))
				.andExpect(status().isOk());

		assertFalse(shortUrlRepository.findById(testShortUrl.getId()).isPresent());
	}

	@Test
	void deleteOriginUrl() throws Exception {
		mockMvc.perform(delete("/delete/origin-url/" + testUrl.getId()))
				.andExpect(status().isOk());

		assertFalse(urlRepository.findById(testUrl.getId()).isPresent());
	}

}
