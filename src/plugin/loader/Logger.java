package plugin.loader;

public class Logger {
	
	private static StringBuffer contents = new StringBuffer();
	
	public static void append(String message) {
		contents.append(message).append("\n");
		System.out.println(message);
	}
	
	public static String getContents() {
		return contents.toString();
	}
}
