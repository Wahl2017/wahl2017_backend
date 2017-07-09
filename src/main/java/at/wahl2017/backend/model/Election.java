package at.wahl2017.backend.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of={ "id", "version" })
@Entity
public class Election {
	
	@GeneratedValue @Id long id;
	@Version int version;

	String title;
	@Column(nullable=false, unique=true)
	String shortCode;
	
	@OneToMany(mappedBy="election")
	List<Party> parties;
	
	
	
}
