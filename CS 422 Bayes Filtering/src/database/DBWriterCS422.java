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
	static String articleString = "INSERT INTO ARTICLE_ARCHIVE VALUES (?,?,?,?,?,?,?)";

	static PreparedStatement nullcheck = null;
	static PreparedStatement insertValue = null;
	static PreparedStatement updateValue = null;

	static String nullcheckSQL = "Select * FROM WORD_FREQ_DATA WHERE WORD=?";
	static String insertSQL = "INSERT INTO WORD_FREQ_DATA VALUES (?,?,?)";
	static String updateSQL = "UPDATE WORD_FREQ_DATA SET WORD_SCORE=?, WORD_TOTAL=? WHERE WORD=?";

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

		System.out.println("Connecting to a selected database...");
		c = DriverManager.getConnection(
				"jdbc:hsqldb:hsql://localhost/metacriticdata", "SA", "");
		c.setAutoCommit(false);
		System.out.println("Connected database successfully...");

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
			article_insert.setDouble(7, -1);
			article_insert.executeUpdate();
			c.commit();
			System.out.println("Article Archived");
			HashMap<String, Integer> wordFreq = RefiningAlgorithmCS422.counter(content);
			DBWriterCS422.refineEntry(wordFreq, score);
		}
		else {
		System.out.println("Article Already exists");
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

		System.out.println("Connecting to a selected database...");
		c = DriverManager.getConnection(
				"jdbc:hsqldb:hsql://localhost/metacriticdata", "SA", "");
		c.setAutoCommit(false);
		System.out.println("Connected database successfully...");

		for (Entry<String, Integer> entry : wordFreq.entrySet()) {
			key = entry.getKey();
			count = entry.getValue();
			double score = reviewScore.doubleValue();
			double decimalscore = score / 100;
			double cscore = decimalscore * count;
			nullcheck = c.prepareStatement(nullcheckSQL);
			nullcheck.setString(1, key);
			ResultSet rs = nullcheck.executeQuery();
			if (rs.next()) {
				double evalscore = rs.getInt(2);
				double freq = rs.getInt(3);
				double updatedeval = evalscore + cscore;
				double updatedfreq = freq + count;
				updateValue = c.prepareStatement(updateSQL);
				updateValue.setDouble(1, updatedeval);
				updateValue.setDouble(2, updatedfreq);
				updateValue.setString(3, key);
				updateValue.executeUpdate();
				c.commit();
				System.out.println("Word Updated");
			} else {
				insertValue = c.prepareStatement(insertSQL);
				insertValue.setString(1, key);
				insertValue.setDouble(2, cscore);
				insertValue.setDouble(3, count);
				insertValue.executeUpdate();
				c.commit();
				System.out.println("Word Inserted");
			}
		}

	}
}
