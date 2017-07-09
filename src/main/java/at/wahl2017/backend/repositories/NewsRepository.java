package at.wahl2017.backend.repositories;

import org.springframework.data.repository.CrudRepository;

import at.wahl2017.backend.model.News;

public interface NewsRepository extends CrudRepository<News, Long>  {

	News findOneByOriginalUrl(String originalUrl);

}
