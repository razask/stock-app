package parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.l3s.boilerpipe.extractors.ArticleExtractor;

public class ExtractorCS422 {

	public static void extractor(Object[] rlist) throws Exception {
		int size = rlist.length;
		for (int i = 0; i > size; i++) {

		}
	}

	public static String extractSource(Element e) throws Exception {
		return e.getElementsByClass("source").text();
	}

	public static String extractAuthor(Element e) throws Exception {
		return e.getElementsByClass("author").text();
	}

	public static String extractURL(Element e) throws Exception {
		Elements cssClass = e.getElementsByClass("external");
		return  cssClass.attr("href");
	}
	
	public static Double extractReviewScore(Element e) throws Exception {
		String grade = e.getElementsByClass("review_grade").text();
		return Double.parseDouble(grade);
	}

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

	public static Element[] reviewList(Document doc) throws Exception {
		Elements allReviews = doc.getElementsByClass("critic_review");
		Element[] listReviews = allReviews.toArray(new Element[0]);
		return listReviews;
	}

	public static String extractMedia(Document doc) throws Exception {
		return doc.getElementsByClass("product_title").text();
	}
}
