package at.wahl2017.backend.repositories;

import org.springframework.data.repository.CrudRepository;

import at.wahl2017.backend.model.Candidate;

public interface CandidateRepository extends CrudRepository<Candidate, Long> {

}
