package at.wahl2017.backend.beans;

import java.util.Date;

import lombok.Data;

@Data
public class EnrichModelLogEntry {
	
	public enum EnrichModelLogEntryState {
		Info, Completed, Failed
	}

	EnrichModelLogEntryState state;
	Date timestamp;
	String log;
	String url;
	
	public static EnrichModelLogEntry info(String info, String url) {
		EnrichModelLogEntry entry = new EnrichModelLogEntry();
		entry.setState(EnrichModelLogEntryState.Info);
		entry.setTimestamp(new Date());
		entry.setLog(info);
		entry.setUrl(url);
		return entry;
	}
	
	public static EnrichModelLogEntry info(String info) {
		return info(info, null);
	}

	public static EnrichModelLogEntry error(String error) {
		EnrichModelLogEntry entry = new EnrichModelLogEntry();
		entry.setState(EnrichModelLogEntryState.Failed);
		entry.setTimestamp(new Date());
		entry.setLog(error);
		return entry;
	}
	
}
