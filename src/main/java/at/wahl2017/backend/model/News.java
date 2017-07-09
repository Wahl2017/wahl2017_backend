package at.wahl2017.backend.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import at.wahl2017.backend.tools.Text;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(exclude="parties")
@EqualsAndHashCode(of={ "id", "version" })
@Entity
public class News {

	public enum NewsSource {
		Standard, Presse, Nachrichten, Krone, Kurier,
	}
	
	@GeneratedValue @Id long id;
	@Version int version;
	
	@JsonIgnore
	@ManyToMany
	Set<Party> parties;
	
	public Set<Party> getParties() {
		if(this.parties == null) {
			this.parties = new HashSet<>();
		}
		return this.parties;
	}
	
	@Enumerated(EnumType.STRING)
	NewsSource source;
	Date timestamp;
	
	String author;
	String headline;
	@Lob
	String teaser;
	
	String originalUrl;
	int sentiment;
	
	public boolean filterNews(Set<String> selectiveKeywords, Set<String> rejectiveKeywords) {
		String text = Text.strip((this.headline + " " + this.teaser).toLowerCase());
		String[] tokens = text.split(" ");
		boolean hasSelective = false;
		for(String token : tokens) {
			if(selectiveKeywords.contains(token)) {
				hasSelective = true;
			}
			if(rejectiveKeywords.contains(token)) {
				return false;
			}
		}
		
		return hasSelective;
	}
	
}
