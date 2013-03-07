package FeedReader;
import java.net.URL;
import java.util.Iterator;
import parser.Handler;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class FeedReader {

	public static final String Yahoo = "http://feeds.finance.yahoo.com/rss/2.0/headline?s=intc&region=US&lang=en-US";
	
  public static void main(String[] args) throws Exception {
	int urlAmount = 1;
	URL[] feedUrl;
    feedUrl = new URL[urlAmount];
    
    feedUrl[0] = new URL(Yahoo);
    XmlReader xmlReader = null;

	for(int i=0; i < urlAmount; i++){
    	try {
     	 xmlReader = new XmlReader(feedUrl[0]);
      	SyndFeed feeder = new SyndFeedInput().build(xmlReader);

      	for (Iterator iterator = feeder.getEntries().iterator(); iterator.hasNext();) {
        	SyndEntry syndEntry = (SyndEntry) iterator.next();
        	System.out.println(syndEntry.getTitle());
			System.out.println("Link: " + syndEntry.getLink());
			System.out.println("Publish Date: " + syndEntry.getPublishedDate());
			System.out.println("Description: " + syndEntry.getDescription().getValue());
			Handler.main(syndEntry.getLink());
       	 	System.out.println();
      		}
    	} finally {
      	if (xmlReader != null)
        xmlReader.close();
    	}
    }
  }
}