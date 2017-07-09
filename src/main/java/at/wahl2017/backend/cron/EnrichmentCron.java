package at.wahl2017.backend.cron;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import at.wahl2017.backend.beans.EnrichModelJob;
import at.wahl2017.backend.beans.EnrichModelLogEntry;
import at.wahl2017.backend.model.Election;
import at.wahl2017.backend.repositories.ElectionRepostiory;
import at.wahl2017.backend.services.ElectionModelService;

@Component
public class EnrichmentCron {
	
	@Value("${wahl2017.election.short.code}")
	private String activeElectionShortCode;
	
	@Autowired
	private ElectionModelService electionModelService;
	
	@Autowired
	private ElectionRepostiory electionRepostiory;

	@Scheduled(fixedDelay=1000*60*60)
	@Transactional
	public void scheduleModelEnrichment() throws MessagingException {
		Election election = this.electionRepostiory.findOneByShortCode(this.activeElectionShortCode);
		EnrichModelJob job = new EnrichModelJob(election);
		job.setNotifyByEmail(true);
		job.addLog(EnrichModelLogEntry.info(String.format("Enrichment triggered by scheduled job", this.activeElectionShortCode)));
		this.electionModelService.enrichModel(job);
	}
	
}
