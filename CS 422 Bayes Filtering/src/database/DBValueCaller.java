package database;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import parser.ParserCS422;
import parser.RefiningAlgorithmCS422;

public class DBValueCaller {

	static PreparedStatement articleReturner = null;
	static String articleURL = "Select CONTENT FROM ARTICLE_ARCHIVE WHERE URL=?";

	static PreparedStatement wordReturner = null;
	static String DBword = "Select * FROM WORD_DATA WHERE WORD=?";

	public static void main(String[] args) throws Exception {

		String[] URLList;
		String jca = "JCA";
		String p = "P";
		String bow = "BOW";
		String ws = "WS";
		String y = "Y";
		String n = "N";

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

		dataPuller(URLList, p, ws, n);

	}

	public static void dataPuller(String[] URLList, String x, String y, String z)
			throws Exception {

		double count = 0;
		double calvalue = 0;

		String key = null;
		double value = 0;

		double evalScore = 0;
		double wordValue = 0;
		double wordCount = 0;
		double wordScore = 0;
		double recipScore = 0;
		double runningCount = 1;
		double recipRunningCount = 0;

		String content = null;

		ResultSet extractedContent = null;

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
			calvalue = 0;
			count = 0;
			runningCount = 0;
			articleReturner = c.prepareStatement(articleURL);
			articleReturner.setString(1, URLList[i]);
			extractedContent = articleReturner.executeQuery();
			while (extractedContent.next()) {
				content = extractedContent.getNString(1);
			}
			HashMap<String, Integer> wordFreq = RefiningAlgorithmCS422
					.counter(content);
			if (z == "Y") {
				wordFreq = RefiningAlgorithmCS422.filter(wordFreq, "POST");
			}
			for (Entry<String, Integer> entry : wordFreq.entrySet()) {
				key = entry.getKey();
				value = entry.getValue();
				wordReturner = c.prepareStatement(DBword);
				wordReturner.setString(1, key);
				ResultSet wordData = wordReturner.executeQuery();
				while (wordData.next()) {
					if (y == "BOW") {
						wordValue = wordData.getDouble(2);
						wordCount = wordData.getDouble(4);
					} else if (y == "WS") {
						wordValue = wordData.getDouble(3);
						wordCount = wordData.getDouble(5);
					}
					if (x == "JCA") {
						wordScore = wordValue / wordCount;
//						calvalue += wordScore * value;
//						count += value;
						calvalue += wordScore;
						count += 1;
					} else if (x == "P") {
						recipScore = wordCount - wordValue;
						if (wordValue == 0){
							wordScore = 0;
						}else {
						wordScore = recipScore / wordValue;
						}
						calvalue += wordScore * value;
						count += value;
						runningCount *= wordScore;

//						 if (runningCount == 0) {
//						 runningCount = wordValue / wordCount;
//						 } else {
//						 runningCount = (wordValue / wordCount)
//						 * runningCount;
//						 }
//						 recipScore = wordCount - wordValue;
//						 if (recipRunningCount == 0) {
//						 recipRunningCount = recipScore / wordCount;
//						 } else {
//						 recipRunningCount = (recipScore / wordCount)
//						 * recipRunningCount;
//						 }
					}
				}
			}
			// System.out.println(URLList[i]);

//			 if (x == "JCA") {
			 evalScore = calvalue / count;
//			evalScore = runningCount / count;
//			evalScore = 1 / (1 + runningCount);
			//
//			 } else if (x == "P") {
//			 evalScore = runningCount / (runningCount + recipRunningCount);
//			 }
			System.out.println(evalScore);

		}
		System.out.println("doneskis");
	}

}
