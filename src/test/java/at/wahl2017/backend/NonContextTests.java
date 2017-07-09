package at.wahl2017.backend;

import org.junit.Test;

import at.wahl2017.backend.sentiment.SimpleSentiment;

public class NonContextTests {

	@Test
	public void computeSentiment() {
		SimpleSentiment simpleSentiment = new SimpleSentiment();
		
		System.out.println(simpleSentiment.computeSentiment("So ein arschloch"));
	}
	
	@Test
	public void comptueSentimentKernTweet() {
		String tweetText = "Gratulation zur erfolgreichen Premiere!";
		
		SimpleSentiment simpleSentiment = new SimpleSentiment();
		System.out.println(simpleSentiment.computeSentiment(tweetText));
	}

	
}
