package at.wahl2017.backend.services;

import org.springframework.stereotype.Component;

import at.wahl2017.backend.sentiment.SimpleSentiment;

@Component
public class SentimentPipeline {
	
	private SimpleSentiment simpleSentiment = null;
	
	private SimpleSentiment ensureInitialized() {
		if(this.simpleSentiment == null) {
			this.simpleSentiment = new SimpleSentiment();
		}
		return this.simpleSentiment;
	}
	
	public int computeSentiment(String inputText) {
		this.ensureInitialized();
		return this.simpleSentiment.computeSentiment(inputText);
	}
	
}
