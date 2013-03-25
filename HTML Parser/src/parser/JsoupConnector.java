package parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JsoupConnector {

	public static Document connector (String url) throws Exception {
//		Connection conn = Jsoup.connect(url).followRedirects(true);
//		conn.timeout(5000);
//		Document doc = conn.get();
		Document doc = Jsoup.connect(url).userAgent("Mozilla").get();
		return doc;
	}
}
