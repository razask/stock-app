package parser;

import java.util.Date;

import com.sun.syndication.feed.synd.SyndEntry;

import database.DBWriter;

public class Handler {

	public static void main(SyndEntry syndEntry) throws Exception {
		String ticker = null;
		String article = null;
		String author = null;
		String url = null;
		String description = null;
		Date date = null;
		String content = null;
		try {
			ticker = Extractor.extractTicker(syndEntry.getLink());
			article = syndEntry.getTitle();
			author = syndEntry.getAuthor();
			url = syndEntry.getLink();
			description = syndEntry.getDescription().getValue();
			date = syndEntry.getPublishedDate();
			content = Extractor.extractContent(syndEntry.getLink());

		} catch (Exception e) {
			System.out.println("bad RSS");
			e.printStackTrace();
		}
		System.out.println(ticker);
		System.out.println("---------------");
		System.out.println(article);
		System.out.println("---------------");
		System.out.println(author);
		System.out.println("---------------");
		System.out.println(url);
		System.out.println("---------------");
		System.out.println(description);
		System.out.println("---------------");
		System.out.println(date);
		System.out.println("---------------");
		System.out.println(content);
		System.out.println("---------------");

		
//		DBWriter.insertArticleArchive(ticker, article, author, url, description, date, content,
//				-1.0);
	}
}
