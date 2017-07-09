package at.wahl2017.backend.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(exclude="party")
@EqualsAndHashCode(of={ "id", "version" })
@Entity
public class Candidate {
	
	@GeneratedValue @Id long id;
	@Version int version;
	
	@JsonIgnore
	@ManyToOne
	Party party;

	String title; 
	String firstname;
	String lastname;
	String postTitle;
	
	String pressPicture;
	String website;
	
	String twitterHandle;
	
	public Candidate() {
		
	}
	
	public Candidate(String firstname, String lastname) {
		this.firstname = firstname;
		this.lastname = lastname;
	}
	
}
