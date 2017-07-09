package at.wahl2017.backend.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;

import at.wahl2017.backend.model.News;
import at.wahl2017.backend.model.News.NewsSource;

@Component
public class RetrieveRssFeedService {
	
	static Logger logger = LoggerFactory.getLogger(RetrieveRssFeedService.class);
	
	@Autowired
	private SentimentPipeline sentimentPipeline;
	
	@Value("${wahl2017.news.sources}")
	private String[] newsSources;
	
	@Value("${wahl2017.news.names}")
	private String[] newsSourceNames;
	
	public List<News> retrieveFeeds() throws IllegalArgumentException, IOException, FeedException {
		LinkedList<News> news = new LinkedList<>();
		for(int i=0;i<newsSources.length && i<newsSourceNames.length;i++) {
			String url = this.newsSources[i];
			String name = this.newsSourceNames[i];
			this.retrieveFeed(url, NewsSource.valueOf(name), news);
		}
		return news;
	}

	public void retrieveFeed(String url, NewsSource source, List<News> news) throws IllegalArgumentException, IOException, FeedException {
		SyndFeed feed = this.getSyndFeedForUrl(new URL(url));
		List<SyndEntry> entries = feed.getEntries();
		for(SyndEntry entry : entries) {
			News ne = new News();
			ne.setSource(source);
			ne.setHeadline(entry.getTitle());
			ne.setAuthor(entry.getAuthor());
			ne.setTimestamp(entry.getPublishedDate());
			ne.setOriginalUrl(entry.getLink());
			ne.setTeaser(this.parseContent(entry, ne.getSource()));
			ne.setSentiment(this.sentimentPipeline.computeSentiment(ne.getHeadline()));
			news.add(ne);
		}
	}
	
	private String parseContent(SyndEntry entry, NewsSource source) {
		StringBuilder sb = new StringBuilder();
		for(SyndContent content : entry.getContents()) {
			sb.append(content.getValue());
		}
		return sb.toString();
	}
	
	private SyndFeed getSyndFeedForUrl(URL url) throws IOException, IllegalArgumentException, FeedException {
		logger.info("Retrieving feed from " + url);
		SyndFeed feed = null;
		
		InputStream in = url.openConnection().getInputStream();
		SyndFeedInput feedInput = new SyndFeedInput();
		feed = feedInput.build(new InputSource(in));

		logger.info("Feed pulled and converted to SyndFeed POJO");
		return feed;
	}
	
}
