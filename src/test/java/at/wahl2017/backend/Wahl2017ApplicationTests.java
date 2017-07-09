package at.wahl2017.backend;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.rometools.rome.io.FeedException;

import at.wahl2017.backend.beans.EnrichModelJob;
import at.wahl2017.backend.model.Election;
import at.wahl2017.backend.model.News;
import at.wahl2017.backend.repositories.ElectionRepostiory;
import at.wahl2017.backend.services.ElectionModelService;
import at.wahl2017.backend.services.RetrieveRssFeedService;
import at.wahl2017.backend.services.RetrieveTweetsService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Wahl2017ApplicationTests {

	@Test
	public void contextLoads() {
	}
	
	@Autowired
	private ElectionRepostiory electionRepostiory;
	
	@Value("${wahl2017.election.short.code}")
	private String activeElectionShortCode;
	
	@Autowired
	private RetrieveRssFeedService retrieveRssFeedService;
	
	@Test
	public void rssFeed() throws IllegalArgumentException, IOException, FeedException {
		List<News> retrievedFeeds = this.retrieveRssFeedService.retrieveFeeds();
		for(News n : retrievedFeeds) {
			System.out.println("" + n.getSentiment() + ": " + n.getHeadline());
		}
	}
	
	@Autowired
	private RetrieveTweetsService retrieveTweetsService;
	
	@Test
	public void tweets() {
		String kernFeed = "kernchri";
		this.retrieveTweetsService.retrieveTweets(kernFeed);
	}
	
	@Autowired
	private ElectionModelService electionModelService;
	
	@Test
	public void enrichScenario() throws IllegalArgumentException, IOException, FeedException, MessagingException {
		Election election = this.electionRepostiory.findOneByShortCode(this.activeElectionShortCode);
		EnrichModelJob job= new EnrichModelJob(election);
		this.electionModelService.enrichModel(job);
		System.out.println(election);
	}
	
}
