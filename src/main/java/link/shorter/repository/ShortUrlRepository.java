package link.shorter.repository;

import link.shorter.model.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
    boolean existsByUrl(String url);
    Optional<ShortUrl> findByUrl(String url);
}