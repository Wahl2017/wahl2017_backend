package at.wahl2017.backend.services;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Component;

import at.wahl2017.backend.model.Social;
import at.wahl2017.backend.model.Sentiment;
import at.wahl2017.backend.model.Social.SocialSource;

@Component
public class RetrieveTweetsService {

	private static final int PAGE_SIZE = 1000;

	static Logger logger = LoggerFactory.getLogger(RetrieveTweetsService.class);
	
	@Autowired
	private TwitterTemplate twitterTemplate;
	
	@Autowired
	private SentimentPipeline sentimentPipeline;
	
	public List<Social> retrieveTweets(String username) {
		List<Social> social = new LinkedList<>();
		List<Tweet> tweets = this.twitterTemplate.timelineOperations().getUserTimeline(username);
		for(Tweet t : tweets) {
			Social s = new Social();
			s.setSource(SocialSource.Twitter);
			s.setOriginalIdentifier(t.getIdStr());
			s.setAuthor(t.getUser().getScreenName());
			s.setTimestamp(t.getCreatedAt());
			s.setText(t.getText());
			s.setOriginalUrl("https://twitter.com/" + username + "/status/" + t.getId());
			s.setSentiment(this.sentimentPipeline.computeSentiment(t.getText()));
			social.add(s);
		}
		return social;
	}
	
	public List<Social> findMentions(String username) {
		List<Social> social = new LinkedList<>();
		SearchResults result = this.twitterTemplate.searchOperations().search("@" + username, PAGE_SIZE);
		List<Tweet> tweets = result.getTweets();
		for(Tweet t : tweets) {
			Social s = new Social();
			s.setSource(SocialSource.Twitter);
			s.setOriginalIdentifier(t.getIdStr());
			s.setAuthor(t.getUser().getScreenName());
			s.setTimestamp(t.getCreatedAt());
			s.setText(t.getText());
			s.setOriginalUrl("https://twitter.com/" + username + "/status/" + t.getId());
			s.setSentiment(this.sentimentPipeline.computeSentiment(t.getText()));
			social.add(s);
		}
		return social;
	}
	
	public Sentiment computeSentiment(Collection<Social> tweets) {
		Sentiment s = new Sentiment();
		s.setOverallSentiment(tweets.stream().mapToDouble(Social::getSentiment).average().getAsDouble());
		s.setOwnSentiment(tweets.stream().filter(t -> !t.isThirdPartyAuthor()).mapToDouble(Social::getSentiment).average().getAsDouble());
		s.setOtherSentiment(tweets.stream().filter(t -> t.isThirdPartyAuthor()).mapToDouble(Social::getSentiment).average().getAsDouble());
		return s;
	}
	
}
