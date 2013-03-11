package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import parser.RefiningAlgorithmCS422;

public class DBWriterCS422 {

	static PreparedStatement article_insert = null;
	static PreparedStatement article_check = null;

	static String articlePresent = "Select * FROM ARTICLE_ARCHIVE WHERE URL=?";
	static String articleString = "INSERT INTO ARTICLE_ARCHIVE VALUES (?,?,?,?,?,?)";

	static PreparedStatement nullcheck = null;
	static PreparedStatement insertValue = null;
	static PreparedStatement updateValue = null;

	static String nullcheckSQL = "Select * FROM WORD_DATA WHERE WORD=?";
	static String insertSQL = "INSERT INTO WORD_DATA VALUES (?,?,?,?,?)";
	static String updateSQL = "UPDATE WORD_DATA SET BOWSCORE=?, WSSCORE=?, BOWCOUNT=?, WSCOUNT=? WHERE WORD=?";

	public static void insertArticleArchive(String media, String source,
			String author, String url, String content, Double score)
			throws Exception {
		Connection c = null;
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
		} catch (Exception e) {
			System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
			e.printStackTrace();
		}

//		System.out.println("Connecting to a selected database...");
		c = DriverManager.getConnection(
				"jdbc:hsqldb:hsql://localhost/metacriticdata", "SA", "");
		c.setAutoCommit(false);
//		System.out.println("Connected database successfully...");

		article_check = c.prepareStatement(articlePresent);
		article_check.setString(1, url);
		ResultSet rs = article_check.executeQuery();
		if (!rs.next()) {

			article_insert = c.prepareStatement(articleString);
			article_insert.setString(1, media);
			article_insert.setString(2, source);
			article_insert.setString(3, author);
			article_insert.setString(4, url);
			article_insert.setString(5, content);
			article_insert.setDouble(6, score);
			article_insert.executeUpdate();
			c.commit();
//			System.out.println("Article Archived");
			HashMap<String, Integer> wordFreq = RefiningAlgorithmCS422
					.counter(content);
			refineEntry(wordFreq, score);
		} else {
//			System.out.println("Article Already exists");
		}
	}

	public static void refineEntry(HashMap<String, Integer> wordFreq,
			Double reviewScore) throws SQLException {
		String key = null;
		double count = -1;

		Connection c = null;
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
		} catch (Exception e) {
			System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
			e.printStackTrace();
			return;
		}

//		System.out.println("Connecting to a selected database...");
		c = DriverManager.getConnection(
				"jdbc:hsqldb:hsql://localhost/metacriticdata", "SA", "");
		c.setAutoCommit(false);
//		System.out.println("Connected database successfully...");

		for (Entry<String, Integer> entry : wordFreq.entrySet()) {
			key = entry.getKey();
			count = entry.getValue();
			double score = reviewScore.doubleValue();
			double decimalscore = score / 100;
			double BOWscore = decimalscore * count;
			nullcheck = c.prepareStatement(nullcheckSQL);
			nullcheck.setString(1, key);
			ResultSet rs = nullcheck.executeQuery();
			if (rs.next()) {
				double evalscoreBOW = rs.getDouble(2);
				double freqBOW = rs.getDouble(4);
				double updatedevalBOW = evalscoreBOW + BOWscore;
				double updatedfreqBOW = freqBOW + count;
				double evalscoreWS = rs.getDouble(3);
				double freqWS = rs.getDouble(5);
				double updatedevalWS = evalscoreWS + decimalscore;
				double updatedfreqWS = freqWS + 1;
				updateValue = c.prepareStatement(updateSQL);
				updateValue.setDouble(1, updatedevalBOW);
				updateValue.setDouble(3, updatedfreqBOW);
				updateValue.setDouble(2, updatedevalWS);
				updateValue.setDouble(4, updatedfreqWS);
				updateValue.setString(5, key);
				updateValue.executeUpdate();
				c.commit();
//				System.out.println("Word Updated");
			} else {
				insertValue = c.prepareStatement(insertSQL);
				insertValue.setString(1, key);
				insertValue.setDouble(2, BOWscore);
				insertValue.setDouble(4, count);
				insertValue.setDouble(3, decimalscore);
				insertValue.setDouble(5, 1);
				insertValue.executeUpdate();
				c.commit();
//				System.out.println("Word Inserted");
			}
		}

	}
}
