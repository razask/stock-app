package parser;

import java.util.HashMap;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import database.DBWriterCS422;

public class HandlerCS422 {

	public static void main(String[] args) {
		Document doc = null;
		Element[] rlist = null;
		String media = null;
		String source = null;
		String author = null;
		String content = null;
		String url = null;
		Double reviewScore = -1.0;
		int articleCount = 0;
		int size = 0;

		try {
			System.out.println("starting");
			try{
			doc = JsoupConnectorCS422
					.connector("http://www.metacritic.com/movie/hansel-and-gretel-witch-hunters/critic-reviews");
			media = ExtractorCS422.extractMedia(doc);
			rlist = ExtractorCS422.reviewList(doc);
			} catch (Exception e){
				e.printStackTrace();
			}
			size = rlist.length;
			for (int i = 0; i < size; i++) {
				try{
				Element currentR = rlist[i];
				source = ExtractorCS422.extractSource(currentR);
				author = ExtractorCS422.extractAuthor(currentR);
				url = ExtractorCS422.extractURL(currentR);
				content = ExtractorCS422.extractContent(url);
				reviewScore = ExtractorCS422.extractReviewScore(currentR);
				DBWriterCS422.insertArticleArchive(media, source, author, url, content, reviewScore);
				articleCount += 1;
				} catch (Exception e){
					continue;
				}
				}
			} finally {
			System.out.println("made it");
			System.out.println(articleCount + " out of " + size);
		}
	}
}
