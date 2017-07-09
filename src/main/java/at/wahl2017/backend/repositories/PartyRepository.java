package at.wahl2017.backend.repositories;

import org.springframework.data.repository.CrudRepository;

import at.wahl2017.backend.model.Party;

public interface PartyRepository extends CrudRepository<Party, Long> {

}
