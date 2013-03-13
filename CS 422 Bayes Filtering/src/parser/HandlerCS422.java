package parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import database.DBWriterCS422;

public class HandlerCS422 {

	public static void main(String[] args) throws IOException {
		Document doc = null;
		Element[] rlist = null;
		String media = null;
		String source = null;
		String author = null;
		String content = null;
		String url = null;
		double reviewScore = -1.0;
		int size = 0;
		double articleCount = 0;

		try {
			System.out.println("starting");

			FileReader list = new FileReader("./src/resources/workingList");
			BufferedReader bufRead = new BufferedReader(list);
			String myLine = null;
			while ((myLine = bufRead.readLine()) != null) {
				String[] parselist = myLine.split("\n");
				for (int i = 0; i < parselist.length; i++) {
					String word = parselist[i];
					System.out.println(word);
					try {
						doc = JsoupConnectorCS422
								.connector("http://www.metacritic.com/movie/"
										+ word + "/critic-reviews");
						rlist = ExtractorCS422.reviewList(doc);
						media = ExtractorCS422.extractMedia(doc);
					} catch (Exception e) {
						System.out.println(word + " failed");
					}

					size = rlist.length;
					for (int j = 0; j < size; j++) {
						try {
							Element currentR = rlist[j];
							source = ExtractorCS422.extractSource(currentR);
							author = ExtractorCS422.extractAuthor(currentR);
							url = ExtractorCS422.extractURL(currentR);
							content = ExtractorCS422.extractContent(url);
							reviewScore = ExtractorCS422
									.extractReviewScore(currentR);
							DBWriterCS422.insertArticleArchive(media, source,
									author, url, content, reviewScore);
							articleCount += 1;
							System.out.println((j + 1) + " out of " + size);
						} catch (Exception e) {
							continue;
						}
					}

				}
			}
		} finally {
			System.out.println("made it");
			System.out.println(articleCount);
		}
	}
}
