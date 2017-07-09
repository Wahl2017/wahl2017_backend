package at.wahl2017.backend.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
public class Social {

	public enum SocialSource {
		Twitter, Facebook
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
	SocialSource source;
	Date timestamp;
	String author;
	boolean thirdPartyAuthor;
	String text;
	
	public void setText(String text) {
		this.text = Text.toValid3ByteUTF8String(text);
	}
	
	String originalUrl;
	String originalIdentifier;
	
	int sentiment;
	
}
