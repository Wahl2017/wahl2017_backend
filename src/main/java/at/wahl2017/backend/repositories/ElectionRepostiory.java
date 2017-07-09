package at.wahl2017.backend.repositories;

import org.springframework.data.repository.CrudRepository;

import at.wahl2017.backend.model.Election;

public interface ElectionRepostiory extends CrudRepository<Election, Long> {

	Election findOneByShortCode(String shortCode);
	
}
