package at.wahl2017.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import at.wahl2017.backend.model.Candidate;
import at.wahl2017.backend.model.Election;
import at.wahl2017.backend.model.Party;
import at.wahl2017.backend.repositories.CandidateRepository;
import at.wahl2017.backend.repositories.ElectionRepostiory;
import at.wahl2017.backend.repositories.PartyRepository;

@Component
public class DataLoader implements ApplicationRunner {
	
	static Logger logger = LoggerFactory.getLogger(DataLoader.class);
	
	@Value("${wahl2017.election.short.code}")
	private String activeElectionShortCode;
	
	@Autowired
	private ElectionRepostiory electionRepository;
	
	@Autowired
	private PartyRepository partyRepository;
	
	@Autowired
	private CandidateRepository candidateRepository;

	@Override
	public void run(ApplicationArguments appArguments) throws Exception {
		Election election = this.electionRepository.findOneByShortCode(this.activeElectionShortCode);
		if(election == null) {
			logger.info(String.format("No election with shortcode '%s' found in database, running scenario to generate it.", this.activeElectionShortCode));
			switch (this.activeElectionShortCode) {
			case "wahl2017":
				this.generateWahl2017Sencario();
				break;
			default:
				throw new IllegalStateException(String.format("There is no scenario for shortcode %s, cannot proceed!", this.activeElectionShortCode));
			}
		}
	}

	private void generateWahl2017Sencario() {
		Election e = new Election();
		e.setTitle("Vorgezogene Nationalratswahl Österreich 2017");
		e.setShortCode("wahl2017");
		this.electionRepository.save(e);
		
		
		{ // SPÖ
			Party party = new Party("SPÖ", "Sozialdemokratische Partei Österreichs");
			party.addKeywords("VSStÖ");
			party.setElection(e);
			this.partyRepository.save(party);
			
			Candidate frontRunner = new Candidate("Christian", "Kern");
			frontRunner.setTitle("Mag.");
			frontRunner.setPressPicture("https://c1.staticflickr.com/1/732/31763441355_f20c421d55_h.jpg");
			frontRunner.setWebsite("https://spoe.at/person/bundeskanzler-christian-kern");
			frontRunner.setTwitterHandle("kernchri");
			frontRunner.setParty(party);
			this.candidateRepository.save(frontRunner);
		}
		
		{ // ÖVP
			Party party = new Party("ÖVP", "Österreichische Volkspartei");
			party.addKeywords("JVP");
			party.setElection(e);
			this.partyRepository.save(party);
			
			Candidate frontRunner = new Candidate("Sebastian", "Kurz");
			frontRunner.setPressPicture("https://d9hhrg4mnvzow.cloudfront.net/www.sebastian-kurz.at/cv/de-1/ae900157-backgrounds44.jpg");
			frontRunner.setWebsite("http://www.sebastian-kurz.at");
			frontRunner.setTwitterHandle("sebastiankurz");
			frontRunner.setParty(party);
			this.candidateRepository.save(frontRunner);
		}
		
		{ // FPÖ
			Party party = new Party("FPÖ", "Freiheitliche Partei Österreich");
			party.addKeywords("HC");
			party.setElection(e);
			this.partyRepository.save(party);
			
			Candidate frontRunner = new Candidate("Heinz-Christian", "Strache");
			frontRunner.setTitle("Mag.");
			frontRunner.setPressPicture("http://www.hcstrache.at/fileadmin/user_upload/www.hcstrache.at/images/hcstrache1.png");
			frontRunner.setWebsite("http://www.hcstrache.at");
			frontRunner.setTwitterHandle("HCStracheFP");
			frontRunner.setParty(party);
			this.candidateRepository.save(frontRunner);
		}
		
		{ // Grüne
			Party party = new Party("GRÜNE", "Die Grünen – Die Grüne Alternative");
			party.addKeywords("glawischnig");
			party.setElection(e);
			this.partyRepository.save(party);
			
			Candidate frontRunner = new Candidate("Ulrike", "Lunacek");
			frontRunner.setTitle("Mag.");
			frontRunner.setPressPicture("https://upload.wikimedia.org/wikipedia/commons/thumb/1/11/Lunacek.Ulrike-6701_%2816887025110%29.jpg/800px-Lunacek.Ulrike-6701_%2816887025110%29.jpg");
			frontRunner.setWebsite("https://archive.is/20130624235717/http://www.ulrikelunacek.eu/index.php?id=34");
			frontRunner.setTwitterHandle("UlrikeLunacek");
			frontRunner.setParty(party);
			this.candidateRepository.save(frontRunner);
			
			Candidate vice = new Candidate("Ingrid", "Felipe");
			vice.setParty(party);
			this.candidateRepository.save(vice);
		}
		
		{ // NEOS
			Party party = new Party("NEOS", "Das Neue Österreich und Liberales Forum");
			party.setElection(e);
			this.partyRepository.save(party);
			
			Candidate frontRunner = new Candidate("Matthias", "Strolz");
			frontRunner.setTitle("Mag.");
			frontRunner.setPressPicture("https://upload.wikimedia.org/wikipedia/commons/a/a6/Matthias_Strolz_20130516_cropped.jpg");
			frontRunner.setWebsite("https://strolz.eu/");
			frontRunner.setTwitterHandle("matstrolz");
			frontRunner.setParty(party);
			this.candidateRepository.save(frontRunner);
		}
				
	}

}
