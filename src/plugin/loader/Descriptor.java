package plugin.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class Descriptor {
	
	private static String URL = "http://{hostname}:{port}/plugins/descriptor.properties";
	private static String PLUGIN_URL = "http://{hostname}:{port}/plugins/{filename}";
	private Properties properties;
	private Map<String, String> envs;
	
	/**
	 * 加载服务器上的插件描述文件
	 * 
	 * @param envs
	 * @throws IOException
	 */
	public Descriptor(Map<String, String> envs) throws IOException {
		this.envs = envs;
		String hostname = envs.get(ENVS.HOSTNAME);
		String port = envs.get(ENVS.PORT);
		String descriptorURL = URL.replace("{hostname}", hostname).replace("{port}", port);
		Logger.append("descriptor url = " + descriptorURL);
		URL url = new URL(descriptorURL);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		Logger.append("descriptor status = " + conn.getResponseCode());
		Logger.append("descriptor size = " + conn.getContentLength());
		InputStream is = conn.getInputStream();
		properties = new Properties();
		properties.load(is);
		is.close();
	}
	
	/**
	 * 获取服务器的插件描述
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	public RemotePlugin getRemotePlugin() throws MalformedURLException {
		RemotePlugin remotePlugin = new RemotePlugin();
		String pluginName = envs.get(ENVS.PLUGINNAME);
		remotePlugin.setName(pluginName);
		Iterator<Object> iterator = properties.keySet().iterator();
		while(iterator.hasNext()) {
			String filename = (String)iterator.next();
			if(filename.startsWith(pluginName+"_")) {
				URL url = getUrl(filename, envs);
				String ts = filename.replace(pluginName+"_", "");
				if(Long.valueOf(ts) > remotePlugin.getTimestamp()) {
					remotePlugin.setTimestamp(Long.valueOf(ts));
					remotePlugin.setUrl(url);
				}
			}
		}
		return remotePlugin;
	}
	
	private URL getUrl(String filename, Map<String, String> envs) throws MalformedURLException {
		String hostname = envs.get(ENVS.HOSTNAME);
		String port = envs.get(ENVS.PORT);
		String url = PLUGIN_URL.replace("{hostname}", hostname).replace("{port}", port).replace("{filename}", filename);
		return new URL(url);
	}
}
