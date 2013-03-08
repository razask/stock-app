package database;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import parser.RefiningAlgorithmCS422;

public class DBValueCaller {

	static PreparedStatement articleReturner = null;
	static String articleURL = "Select CONTENT FROM ARTICLE_ARCHIVE WHERE URL=?";

	static PreparedStatement wordReturner = null;
	static String DBword = "Select * FROM WORD_FREQ_DATA WHERE WORD=?";

	public static void main(String[] args) throws Exception {
		String[] URLList;
		URLList = new String[18];

		URLList[0] = new String(
				"http://www.rogerebert.com/apps/pbcs.dll/article?AID=/20130227/REVIEWS/130229979/1023");
		URLList[1] = new String(
				"http://www.villagevoice.com/2013-02-27/film/jack-the-giant-slayer-is-fee-fi-fo-fun/");
		URLList[2] = new String(
				"http://www.slantmagazine.com/film/review/jack-the-giant-slayer/6837");
		URLList[3] = new String(
				"http://www.tampabay.com/things-to-do/movies/review-jack-the-giant-slayer-not-worth-a-hills-of-beans/1276654");
		URLList[4] = new String(
				"http://www.npr.org/2013/02/28/172977601/jack-the-giant-slayer-a-fun-fractured-fairy-tale");
		URLList[5] = new String(
				"http://movies.nytimes.com/2013/03/01/movies/jack-the-giant-slayer-starring-nicholas-hoult.html?ref=movies&_r=0");
		URLList[6] = new String(
				"http://www.film.com/movies/zero-dark-thirty-review");
		URLList[7] = new String(
				"http://www.empireonline.com/reviews/reviewcomplete.asp?FID=133337");
		URLList[8] = new String(
				"http://www.csmonitor.com/The-Culture/Movies/2012/1214/Jessica-Chastain-stars-in-the-troubling-infuriating-Zero-Dark-Thirty-trailer");
		URLList[9] = new String(
				"http://observer.com/2012/12/bagging-bin-laden-zero-dark-thirty/");
		URLList[10] = new String(
				"http://www.guardian.co.uk/film/2012/dec/04/zero-dark-thirty-review-kathryn-bigelow");
		URLList[11] = new String(
				"http://www.npr.org/2012/12/19/166896991/shedding-grim-light-on-a-dark-story");
		URLList[12] = new String(
				"http://www.boston.com/ae/movies/2013/01/26/beyond-the-gingerbread-house/0OntZLgV6m3lNr41c0sIUO/story.html");
		URLList[13] = new String(
				"http://entertainment.time.com/2013/01/25/a-punkster-hansel-gretel-that-witch-does-not-kill-us/");
		URLList[14] = new String(
				"http://www.slantmagazine.com/film/review/hansel-and-gretel-witch-hunters/6790");
		URLList[15] = new String(
				"http://www.nypost.com/p/entertainment/movies/grim_gretel_8qiFhJEGRzSi7x97t38aSO?utm_medium=rss&utm_content=Movies");
		URLList[16] = new String(
				"http://www.vulture.com/2013/01/movie-review-hansel-gretel-witch-hunters.html");
		URLList[17] = new String(
				"http://www.guardian.co.uk/film/2013/feb/28/hansel-gretel-witch-hunters-review");

		dataPuller(URLList);

	}

	public static void dataPuller(String[] URLList) throws Exception {

		double count = 0;
		double calvalue = 0;

		String key = null;
		double value = 0;

		double wordValue = 0;
		double wordCount = 0;
		double wordScore = 0;

		String content = null;

		Connection c = null;

		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
		} catch (Exception e) {
			System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
			e.printStackTrace();
		}

		System.out.println("Connecting to a selected database...");
		c = DriverManager.getConnection(
				"jdbc:hsqldb:hsql://localhost/metacriticdata", "SA", "");
		c.setAutoCommit(false);
		System.out.println("Connected database successfully...");

		for (int i = 0; i < URLList.length; i++) {
			articleReturner = c.prepareStatement(articleURL);
			articleReturner.setString(1, URLList[i]);
			ResultSet extractedContent = articleReturner.executeQuery();
			while (extractedContent.next()) {
				content = extractedContent.getNString(1);
			}
			HashMap<String, Integer> wordFreq = RefiningAlgorithmCS422
					.counter(content);
			for (Entry<String, Integer> entry : wordFreq.entrySet()) {
				key = entry.getKey();
				value = entry.getValue();
				wordReturner = c.prepareStatement(DBword);
				wordReturner.setString(1, key);
				ResultSet wordData = wordReturner.executeQuery();
				while (wordData.next()) {
					wordValue = wordData.getDouble(2);
					wordCount = wordData.getDouble(3);
					wordScore = wordValue / wordCount;
				}
				calvalue += wordScore * value;
				count += value;
			}
			double evalScore = calvalue / count;
			System.out.println(URLList[i]);
			System.out.println(evalScore);
		}
	}

}
