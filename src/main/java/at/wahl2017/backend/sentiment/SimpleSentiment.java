package at.wahl2017.backend.sentiment;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleSentiment {

	private Set<String> positiveSentimentWords;
	
	private Set<String> negatieSentimentWords;
	
	public SimpleSentiment() {
		this.positiveSentimentWords = new HashSet<>();
		this.loadResourceToSet("/sentiment/Freude.txt", this.positiveSentimentWords);
		this.loadResourceToSet("/sentiment/Ueberraschung.txt", this.positiveSentimentWords);
		
		this.negatieSentimentWords = new HashSet<>();
		this.loadResourceToSet("/sentiment/Ekel.txt", this.negatieSentimentWords);
		this.loadResourceToSet("/sentiment/Furcht.txt", this.negatieSentimentWords);
		this.loadResourceToSet("/sentiment/Trauer.txt", this.negatieSentimentWords);
		this.loadResourceToSet("/sentiment/Verachtung.txt", this.negatieSentimentWords);
		this.loadResourceToSet("/sentiment/Wut.txt", this.negatieSentimentWords);
	}
	
	private void loadResourceToSet(String resource, Set<String> set) {
		InputStream in = this.getClass().getResourceAsStream(resource);
		BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
		Set<String> collectedSet = buffer.lines().map(s -> s.toLowerCase()).collect(Collectors.toSet());
		set.addAll(collectedSet);
	}
	
	public int computeSentiment(String inputText) {
		int sentiment = 0;
		if(inputText != null) {
			inputText = inputText.toLowerCase();
			String[] tokens = inputText.split(" ");
			for(String t : tokens) {
				if(this.positiveSentimentWords.contains(t)) {
					sentiment ++;
				}else if(this.negatieSentimentWords.contains(t)) {
					sentiment --;
				}
			}
		}
			
		return sentiment;
	}
	
}
