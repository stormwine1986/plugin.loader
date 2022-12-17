package plugin.loader;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

public class LocalPlugin {
	
	public static final String LOCAL_DIR = "{user.home}/.mks/pluginCache";
	
	private long timestamp = -1L;
	protected String name;
	protected File localFile;
	
	public LocalPlugin(Map<String, String> envs) {
		String userHome = System.getProperty("user.home");
		String localDir = LOCAL_DIR.replace("{user.home}", userHome);
		String pluginName = envs.get(ENVS.PLUGINNAME);
		name = pluginName;
		File dir = new File(localDir);
		File[] files = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.getName().startsWith(pluginName+"_");
			}
		});
		if(files==null) return;
		for(int i = 0; i < files.length; i++) {
			File file = files[i];
			String filename = file.getName();
			String ts = filename.replace(pluginName+"_", "");
			if(Long.valueOf(ts) > timestamp) {
				timestamp = Long.valueOf(ts);
				localFile = file;
			}
		}
	}
	
	public LocalPlugin(String name, File file) {
		this.name = name;
		String filename = file.getName();
		String ts = filename.replace(name+"_", "");
		timestamp = Long.valueOf(ts);
		localFile = file;
	}
	
	public boolean isReady() {
		return timestamp > 0;
	}
	
	private static String MAIN_CLASS = "plugin.Main";
	private static String MAIN_METHOD = "run";
	
	/**
	 * 启动插件
	 * 
	 * @param args
	 * @throws Exception
	 */
	public void lauch(String[] args) throws Exception {
		URL url = localFile.toURI().toURL();
		Logger.append("lauch local plugin = " + url);
		URLClassLoader classLoader = new URLClassLoader(new URL[] {url}, Thread.currentThread().getContextClassLoader());
		Thread.currentThread().setContextClassLoader(classLoader);
		Class<?> cls = classLoader.loadClass(MAIN_CLASS);
		Method method = cls.getMethod(MAIN_METHOD, String[].class);
		method.invoke(null, new Object[] {args});
	}
	
	/**
	 * 删除本地插件
	 * 
	 */
	public void delete() {
		Logger.append("deleting old local file " + localFile.getAbsolutePath());
		localFile.delete();
		timestamp = -1L;
	}

	public long getTimestamp() {
		return this.timestamp;
	}
}
