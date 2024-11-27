package link.shorter.repository;

import link.shorter.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    boolean existsByUrl(String url);

    Url getUrlByUrl(String url);
}