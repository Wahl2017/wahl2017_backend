package at.wahl2017.backend.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(exclude="election")
@EqualsAndHashCode(of={ "id", "version" })
@Entity
public class Party {
	
	@GeneratedValue @Id long id;
	@Version int version;

	String name;
	String shortCode;
	
	@JsonIgnore
	@ManyToOne
	Election election;
	
	@OneToMany(mappedBy="party")
	List<Candidate> candidates;
	
	@Transient
	public Candidate getFrontRunner() {
		if(this.candidates != null && this.candidates.size() > 0) {
			return this.candidates.get(0);
		}
		return null;
	}
	
	@ManyToMany(mappedBy="parties")
	Set<News> news;
	
	@ManyToMany(mappedBy="parties")
	Set<Social> social;
	
	@Embedded
	Sentiment socialSentiment;
	
	@ElementCollection
	@CollectionTable(name="party_keywords", joinColumns=@JoinColumn(name="party_id"))
	Set<String> keywords;
	
	@Transient
	private Set<String> preprocessedKeywords = null;
	
	public Party() {
		this.candidates = new ArrayList<>();
		this.news = new HashSet<>();
		this.social = new HashSet<>();
		this.keywords = new HashSet<>();
	}
	
	public Party(String shortCode, String name) {
		this();
		this.shortCode = shortCode;
		this.name = name;
	}
	
	public Set<String> processKeywords() {
		if(this.preprocessedKeywords == null) {
			this.preprocessedKeywords = new HashSet<>();
			this.preprocessedKeywords.addAll(this.keywords);
			
			this.preprocessedKeywords.add(this.name);
			this.preprocessedKeywords.add(this.shortCode);
			for(Candidate c : this.candidates) {
				this.addCandidateKeywords(c);
			}
			this.preprocessedKeywords = this.preprocessedKeywords.stream().map(s -> s.toLowerCase()).collect(Collectors.toSet());
		}
		return this.preprocessedKeywords;
	}
	
	private void addCandidateKeywords(Candidate c) {
		this.preprocessedKeywords.add(c.firstname);
		this.preprocessedKeywords.add(c.lastname);
	}
	
	public void addKeywords(String ... words) {
		this.keywords.addAll(Arrays.asList(words));
	}

	public Set<String> selectiveKeywords() {
		return this.processKeywords().stream().filter(s -> !s.startsWith("-")).collect(Collectors.toSet());
	}
	
	public Set<String> rejectiveKeywords() {
		return this.processKeywords().stream().filter(s -> s.startsWith("-")).map(s -> s.substring(1)).collect(Collectors.toSet());
	}

	public void addCandidate(Candidate candidate) {
		if(this.candidates == null) {
			this.candidates = new LinkedList<>();
		}
		this.candidates.add(candidate);
	}
	
}
