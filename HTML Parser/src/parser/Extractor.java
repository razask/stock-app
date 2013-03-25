package parser;

import java.util.Arrays;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.l3s.boilerpipe.extractors.ArticleExtractor;

public class Extractor {

	public static String extractContent(String url) throws Exception {
		Document doc = Jsoup.connect(url).timeout(0).userAgent("Mozilla").get();
		return textFormatter(doc);
	}

	public static String textFormatter(Document doc) throws Exception {
		HtmlToPlainText formatter = new HtmlToPlainText();
		String plainText = formatter.getPlainText(doc);
		// System.out.println(plainText);
		String bodyText = ArticleExtractor.INSTANCE.getText(plainText);
		// System.out.println(bodyText);
		String formattedText = bodyText.toLowerCase();
		// System.out.println(formattedText);
		formattedText = formattedText.replaceAll("[^a-zA-Z\\s\\n']", "");
		// System.out.println(formattedText);
		return formattedText;
	}
	
	public static String extractTicker(String url) throws Exception {
		Document doc = Jsoup.connect(url).timeout(0).userAgent("Mozilla").get();
		return tickerCounter(doc);
	}

	public static String tickerCounter(Document doc) {
		HtmlToPlainText formatter = new HtmlToPlainText();
		String text = formatter.getPlainText(doc);
		String[] lines = text.split("\n");
		String ticker = null;
		while (ticker == null) {
			for (int i = 0; i < lines.length; i++) {
				String[] words = lines[i].split(" ");
				for (String w : Arrays.asList(words)) {
					if (w.matches("((?-i)(?<=\\s|^)[A-Z]{1,5}(\\.[A-Z]{1,2})?(?=\\s|$))")) {
						ticker = w;
					} else {
					}
				}
			}
		}
		System.out.println(ticker);
		return ticker;
	}
}
