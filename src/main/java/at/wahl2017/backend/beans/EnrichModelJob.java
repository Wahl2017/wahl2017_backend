package at.wahl2017.backend.beans;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import at.wahl2017.backend.model.Election;
import lombok.Data;

@Data
public class EnrichModelJob {
	
	public enum EnrichModelJobState {
		Running, Completed, Failed
	}
	
	EnrichModelJobState jobState;
	
	Election election;
	
	String uuid;
	Date started;
	Date finished;
	
	boolean notifyByEmail;
	
	List<EnrichModelLogEntry> log;
	
	public EnrichModelJob(Election election) {
		this.election = election;
		this.uuid = UUID.randomUUID().toString();
		this.started = new Date();
	}
	
	public void addLog(EnrichModelLogEntry entry) {
		if(this.log == null) {
			this.log = new LinkedList<>();
		}
		this.log.add(entry);
	}
	
}
