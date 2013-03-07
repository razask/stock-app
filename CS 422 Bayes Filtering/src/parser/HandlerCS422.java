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

		try {
			System.out.println("starting");
			doc = JsoupConnectorCS422
					.connector("http://www.metacritic.com/movie/jack-the-giant-slayer/critic-reviews");
			media = ExtractorCS422.extractMedia(doc);
			rlist = ExtractorCS422.reviewList(doc);
			int size = rlist.length;
			for (int i = 0; i < size; i++) {
				Element currentR = rlist[i];
				source = ExtractorCS422.extractSource(currentR);
				author = ExtractorCS422.extractAuthor(currentR);
				url = ExtractorCS422.extractURL(currentR);
				content = ExtractorCS422.extractContent(url);
				reviewScore = ExtractorCS422.extractReviewScore(currentR);
				DBWriterCS422.insertArticleArchive(media, source, author, url, content, reviewScore);
				HashMap<String, Integer> wordFreq = RefiningAlgorithmCS422.counter(content);
				DBWriterCS422.refineEntry(wordFreq, reviewScore);
			}
			System.out.println("made it");
		} catch (Exception e) {
			System.out.println("bad url");
			e.printStackTrace();
		}
	}
}
