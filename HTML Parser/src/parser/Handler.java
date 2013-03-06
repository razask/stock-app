package parser;

public class Handler {

	public static void main(String url) {
		Parser parser = new Parser();
		try {
			parser.handler(
					url
					);
		} catch (Exception e) {
			System.out.println("bad url");
			e.printStackTrace();
		}
	}
}
