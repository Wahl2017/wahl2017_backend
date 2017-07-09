package at.wahl2017.backend.services;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.rometools.rome.io.FeedException;

import at.wahl2017.backend.beans.EnrichModelJob;
import at.wahl2017.backend.beans.EnrichModelJob.EnrichModelJobState;
import at.wahl2017.backend.beans.EnrichModelLogEntry;
import at.wahl2017.backend.model.News;
import at.wahl2017.backend.model.Party;
import at.wahl2017.backend.model.Social;
import at.wahl2017.backend.repositories.NewsRepository;
import at.wahl2017.backend.repositories.PartyRepository;
import at.wahl2017.backend.repositories.SocialRepository;

@Component
public class ElectionModelService {
	
	static Logger logger = LoggerFactory.getLogger(ElectionModelService.class);
	
	@Autowired
	private RetrieveRssFeedService retrieveRssFeedService;
	
	@Autowired
	private RetrieveTweetsService retrieveTweetsService;
	
	@Autowired
	private NewsRepository newsRepository;
	
	@Autowired
	private SocialRepository socialRepository;
	
	@Autowired
	private PartyRepository partyRepository;
	
	@Autowired
	private EmailService emailService;
	
	@Value("${wahl2017.email.cron}")
	private String emailCron;
	
	@Value("${wahl2017.email.sender}")
	private String emailSender;
	
	@Async
	@Transactional
	public void enrichModelAsync(EnrichModelJob job) throws MessagingException {
		this.enrichModel(job);
	}

	public void enrichModel(EnrichModelJob job) throws MessagingException {
		job.setJobState(EnrichModelJobState.Running);
		logger.info(String.format("Started model enrichment for %s", job.getElection().getShortCode()));
		logger.info("Retrieving all news through RSS feeds", job.getElection().getShortCode());
		List<News> news;
		try {
			news = this.retrieveRssFeedService.retrieveFeeds();
			logger.info(String.format("Retrieved %d news items", news.size()));
			for(Party party : job.getElection().getParties()) {
				this.enrichParty(job, party, news);
			}
			job.addLog(EnrichModelLogEntry.info("Job successful"));
		} catch (IllegalArgumentException | IOException | FeedException e) {
			job.addLog(EnrichModelLogEntry.error(String.format("Failed with exception %s", e.getMessage())));
			job.setJobState(EnrichModelJobState.Failed);
		} finally {
			job.setJobState(EnrichModelJobState.Completed);
			job.setFinished(new Date());
			if(job.isNotifyByEmail() && !StringUtils.isEmpty(this.emailCron)) {
				HashMap<String, Object> variables = new HashMap<>();
				variables.put("job", job);
				this.emailService.send(this.emailSender, null, new String [] { this.emailCron } , null, null, "job-result", "Model Enrichment Job run: " + job.getJobState().toString(), variables);
			}
		}
	}

	public void enrichParty(EnrichModelJob job, Party party, List<News> news) {
		String info = String.format("Enriching party %s of election %s", party.getShortCode(), party.getElection().getShortCode());
		logger.info(info);
		job.addLog(EnrichModelLogEntry.info(info));
		this.enrichPartyNews(job, party, news);
		this.enrichPartySocial(job, party);
	}
	
	public void enrichPartyNews(EnrichModelJob job, Party party, List<News> news) {
		logger.info(String.format("Enriching party %s of election %s, phase 1 news items", party.getShortCode(), party.getElection().getShortCode()));
		Set<String> selectiveKeywords = party.selectiveKeywords();
		Set<String> rejectiveKeywords = party.rejectiveKeywords();
		int count = 0;
		for(News n : news) {
			if(n.filterNews(selectiveKeywords, rejectiveKeywords)) {
				if(this.addNewsToParty(job, n, party)) {
					count ++;
				}
			}
		}
		String info = String.format("Added %d news items to party %s", count, party.getShortCode());
		logger.info(info);
		if(count > 0) {
			job.addLog(EnrichModelLogEntry.info(info));
		}
	}
	
	private boolean addNewsToParty(EnrichModelJob job, News news, Party party) {
		logger.info(String.format("Assigning news article '%s' from %s to party %s", news.getHeadline(), news.getSource().toString(), party.getShortCode()));
		News onFile = this.newsRepository.findOneByOriginalUrl(news.getOriginalUrl());
		if(onFile != null) {
			news = onFile;
		}else{
			this.newsRepository.save(news);
		}
		if(!party.getNews().contains(news)) {
			party.getNews().add(news);
			news.getParties().add(party);
			
			this.partyRepository.save(party);
			job.addLog(EnrichModelLogEntry.info(String.format("Added news item: '%s' to %s", news.getHeadline(), party.getShortCode()), news.getOriginalUrl()));
			return true;
		}
		return false;
	}
	
	public void enrichPartySocial(EnrichModelJob job, Party party) {
		int count = 0;
		logger.info(String.format("Enriching party %s of election %s, phase 2 social signals of party", party.getShortCode(), party.getElection().getShortCode()));
		count += this.enrichPartySocial(job, party, this.retrieveTweetsService.retrieveTweets(party.getFrontRunner().getTwitterHandle()), false);
		logger.info(String.format("Enriching party %s of election %s, phase 2 social signals of other people", party.getShortCode(), party.getElection().getShortCode()));
		List<Social> mentions = this.retrieveTweetsService.findMentions(party.getFrontRunner().getTwitterHandle());
		count += this.enrichPartySocial(job, party, mentions, true);
		logger.info(String.format("Enriching party %s of election %s, phase 3 guessing social sentiment", party.getShortCode(), party.getElection().getShortCode()));
		party.setSocialSentiment(this.retrieveTweetsService.computeSentiment(party.getSocial()));
		this.partyRepository.save(party);
		String info = String.format("Added %d social signals to party %s", count, party.getShortCode());
		logger.info(info);
		if(count > 0) {
			job.addLog(EnrichModelLogEntry.info(info));
		}
	}
	
	public int enrichPartySocial(EnrichModelJob job, Party party, List<Social> social, boolean thirdPartyAuthor) {
		int count = 0;
		for(Social s : social) {
			logger.info(String.format("Assigning social signal '%s' from %s to party %s", s.getText(), s.getAuthor(), party.getShortCode()));
			Social onFile = this.socialRepository.findOneByOriginalUrl(s.getOriginalUrl());
			if(onFile != null) {
				s = onFile;
			}else{
				s.setThirdPartyAuthor(thirdPartyAuthor);
				this.socialRepository.save(s);
			}
			if(!party.getSocial().contains(s)) {
				party.getSocial().add(s);
				s.getParties().add(party);
				this.partyRepository.save(party);
				count ++;
				job.addLog(EnrichModelLogEntry.info(String.format("Added social signal from %s: ''%s' to %s", s.getAuthor(), s.getText(), party.getShortCode()), s.getOriginalUrl()));
			}
		}
		return count;
	}
	
}
