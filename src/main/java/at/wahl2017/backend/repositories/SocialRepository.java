package at.wahl2017.backend.repositories;

import org.springframework.data.repository.CrudRepository;

import at.wahl2017.backend.model.Social;

public interface SocialRepository extends CrudRepository<Social, Long> {

	Social findOneByOriginalUrl(String originalUrl);

}
