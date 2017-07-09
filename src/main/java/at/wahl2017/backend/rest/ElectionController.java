package at.wahl2017.backend.rest;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import at.wahl2017.backend.beans.EnrichModelJob;
import at.wahl2017.backend.beans.EnrichModelLogEntry;
import at.wahl2017.backend.model.Election;
import at.wahl2017.backend.repositories.ElectionRepostiory;
import at.wahl2017.backend.services.ElectionModelService;

@RestController
public class ElectionController {
	
	@Value("${wahl2017.election.short.code}")
	private String activeElectionShortCode;
	
	@Autowired
	private ElectionRepostiory electionRepostiory;
	
	@Autowired
	private ElectionModelService electionModelService;

	@RequestMapping(value="/api/elections/{electionShortCode}")
	public @ResponseBody Election getElection(@PathVariable String electionShortCode) {
		if("default".equals(electionShortCode)) {
			electionShortCode = this.activeElectionShortCode;
		}
		return this.electionRepostiory.findOneByShortCode(electionShortCode);
	}
	
	@RequestMapping(value="/api/elections/{electionShortCode}/enrichment")
	public @ResponseBody EnrichModelJob startModelEnrichment(@PathVariable String electionShortCode) throws MessagingException {
		Election election = this.getElection(electionShortCode);
		EnrichModelJob job = new EnrichModelJob(election);
		job.addLog(EnrichModelLogEntry.info(String.format("Enrichment triggered through REST API call to /api/elections/%s/enrichment", electionShortCode)));
		this.electionModelService.enrichModel(job);
		return job;
	}
	
}
