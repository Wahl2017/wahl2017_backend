package at.wahl2017.backend.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

@Configuration
public class TwitterConfigurationTemplate {
	
	@Value("${spring.social.twitter.app-id}")
	private String twitterConsumerKey;
	
	@Value("${spring.social.twitter.app-secret}")
	private String twitterConsumerSecret;
	
	@Value("${twitter.access.token}")
	private String twitterAccessToken;
	
	@Value("${twitter.access.secret}")
	private String twitterAccessTokenSecret;

	@Bean
	public TwitterTemplate twitterTemplate() {		
		return new TwitterTemplate(this.twitterConsumerKey, this.twitterConsumerSecret, this.twitterAccessToken, this.twitterAccessTokenSecret);
	}
	
}
