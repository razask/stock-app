package parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
import org.jsoup.nodes.Document;

import de.l3s.boilerpipe.extractors.ArticleExtractor;

public class ParserCS422 {

	public Map<String, Integer> frequencies;

	public void handler(String url) throws Exception {
//		Connection conn = Jsoup.connect(url).followRedirects(true);
//		conn.timeout(5000);
//		Document doc = conn.get();
		Document doc = Jsoup.connect(url).userAgent("Mozilla").get();
//		Elements links = doc.getElementsByTag(HTML.Tag.CITE.toString());
		String formattedText = textFormatter(doc);
		String site = returnDomain(url);
		HashMap<String, Integer> ticker = tickerCounter(doc);
		HashMap<String, Integer> wordFreq = RefiningAlgorithmCS422
				.counter(formattedText);
		float result = dumbreview(formattedText);
		System.out.println(result);
	}

	private static float dumbreview(String body) throws Exception {
		String[] lines = body.split("\n");
		HashMap<String, Integer> frequencies = new HashMap<String, Integer>();
		for (int i = 0; i < lines.length; i++) {
			String[] words = lines[i].split(" ");
			for (String w : Arrays.asList(words)) {
				Integer num = frequencies.get(w);
				if (num != null) {
					frequencies.put(w, num + 1);
				} else {
					frequencies.put(w, 1);
				}
			}
		}
		float positive = positiveFilter(frequencies);
		float negative = negativeFilter(frequencies);
		System.out.println(frequencies.size());
		System.out.println(positive);
		System.out.println(negative);
		return calculator(positive, negative, frequencies.size());
	}

	private static float calculator(float positive, float negative, int size) {
		if ((positive + negative) < (size / 20)) {
			return (-1);
		}

		else {
			return positive / (positive + negative);
		}
	}

	private static int positiveFilter(HashMap<String, Integer> input)
			throws Exception {
		FileReader list = new FileReader("./src/resources/Positive");
		BufferedReader bufRead = new BufferedReader(list);
		String myLine = null;
		int result = 0;
		while ((myLine = bufRead.readLine()) != null) {
			String[] parselist = myLine.split("\n");
			for (int i = 0; i < parselist.length; i++) {
				String word = parselist[i];
				if (input.containsKey(word)) {
					result += input.get(word);
				}
			}
		}
		return result;
	}

	private static int negativeFilter(HashMap<String, Integer> input)
			throws Exception {
		FileReader list = new FileReader("./src/resources/Negative");
		BufferedReader bufRead = new BufferedReader(list);
		String myLine = null;
		int result = 0;
		while ((myLine = bufRead.readLine()) != null) {
			String[] parselist = myLine.split("\n");
			for (int i = 0; i < parselist.length; i++) {
				String word = parselist[i];
				if (input.containsKey(word)) {
					result += 1;
				}
			}
		}
		return result;
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

	private String returnDomain(String url) throws Exception {
		String site = new URL(url).getHost();
		System.out.println(site);
		return site;
	}



	private static HashMap<String, Integer> tickerCounter(Document doc) {
		HtmlToPlainText formatter = new HtmlToPlainText();
		String text = formatter.getPlainText(doc);
		String[] lines = text.split("\n");
		HashMap<String, Integer> frequencies = new HashMap<String, Integer>();
		for (int i = 0; i < lines.length; i++) {
			String[] words = lines[i].split(" ");
			for (String w : Arrays.asList(words)) {
				if (w.matches("((?-i)(?<=\\s|^)[A-Z]{1,5}(\\.[A-Z]{1,2})?(?=\\s|$))")) {
					frequencies.put(w, 1);
				} else {
				}
			}
		}
		System.out.println(frequencies);
		return frequencies;
	}
}