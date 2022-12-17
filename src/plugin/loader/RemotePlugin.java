package plugin.loader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RemotePlugin {

	private String name;
	private long timestamp = -1L;
	private URL url;
	
	/**
	 * 下载插件
	 * 
	 * @return
	 * @throws IOException
	 */
	public LocalPlugin download() throws IOException {
			
			String userHome = System.getProperty("user.home");
			File localDir = new File(LocalPlugin.LOCAL_DIR.replace("{user.home}", userHome));
			if(!localDir.exists()) {
				localDir.mkdirs();
			}
			String filename = name + "_" + timestamp;
			File file = new File(localDir.getAbsolutePath() + File.separator + filename);
			Logger.append("remote plugin url = " + url);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			Logger.append("download plugin status = " + conn.getResponseCode());
			Logger.append("download plugin size = " + conn.getContentLength());
			LoadingBar loadingBar = new LoadingBar(conn.getContentLength());
			Thread t = new Thread(loadingBar);
			t.start();
			try {
				InputStream is = conn.getInputStream();
				FileOutputStream os = new FileOutputStream(file);
				byte[] buffer = new byte[4096];
				int size = 0;
				while((size = is.read(buffer)) > 0) {
					os.write(buffer, 0, size);
					loadingBar.up(size);
				}
				is.close();
				os.flush();
				os.close();
				return new LocalPlugin(name, file);
		}finally {
			loadingBar.close();
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	/**
	 * 与本地插件比较
	 * 
	 * @param localPlugin
	 * @return
	 */
	public boolean isNewThan(LocalPlugin localPlugin) {
		Logger.append("Remote Plugin Version = " + this.timestamp);
		Logger.append("Local Plugin Version = " + localPlugin.getTimestamp());
		return this.timestamp > localPlugin.getTimestamp();
	}
}
