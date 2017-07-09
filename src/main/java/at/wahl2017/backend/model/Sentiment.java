package at.wahl2017.backend.model;

import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class Sentiment {

	double overallSentiment;
	
	double ownSentiment;
	double otherSentiment;
	
}
