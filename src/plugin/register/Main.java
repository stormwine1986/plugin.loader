package plugin.register;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;

/**
 * reg 部分的入口
 * 
 * @author pjia
 *
 */
public class Main {
	/**
	 * --server=<Server Home>
	 * --plugin=<Jarfile Path>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			if(args == null) {
				throw new IllegalArgumentException("Usage: --server=<Server Home> --plugin=<Jarfile Path>");
			}
			String serverOption = findServerOption(args);
			String pluginOption = findPluginOption(args);
			if(serverOption.equals("") || pluginOption.equals("")) {
				throw new IllegalArgumentException("Usage: --server=<Server Home> --plugin=<Jarfile Path>");
			}
			File installDir = new File(serverOption);
			File pluginsHome = new File(installDir.getAbsolutePath() + "/data/public_html/plugins");
			if(!pluginsHome.exists()) {
				// 初始化目录
				pluginsHome.mkdirs();
			}
			File pluginFile = new File(pluginOption);
			String pluginName = pluginFile.getName().replace(".jar", "");
			long timestamp = (new Date()).getTime();
			String deployName = pluginName + "_" + timestamp;
			// 先尝试找到已部署的插件，如果存在，在新插件部署后，删除它；不存在，直接安装新插件即可；
			File deployedPluginFile = getDeployedPluginFile(pluginsHome, pluginName);
			File newerPluginFile = new File(pluginsHome + "/" + deployName);
			installPlugin(pluginFile, newerPluginFile);
			if(deployedPluginFile!=null) {
				deployedPluginFile.delete();
			}
			// --------------------------------------------------------------------------------
			// 最后更新插件清单
			updateDescriptor(pluginsHome);
		}catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static void updateDescriptor(File pluginsHome) throws IOException {
		File descriptorFile = new File(pluginsHome.getAbsolutePath() + "/descriptor.properties");
		Properties properties = new Properties();
		pluginsHome.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File time, String name) {
				if(!"descriptor.properties".equals(name)) {
					properties.setProperty(name, "");					
				}
				return true;
			}
		});
		properties.store(new FileOutputStream(descriptorFile), "");
	}

	private static void installPlugin(File pluginFile, File deployedPluginFile) throws IOException {
		FileInputStream is = new FileInputStream(pluginFile);
		FileOutputStream os = new FileOutputStream(deployedPluginFile);
		byte[] buffer = new byte[4096];
		int size = -1;
		while((size = is.read(buffer)) > 0) {
			os.write(buffer, 0, size);
		}
		os.flush();
		os.close();
		is.close();
	}

	private static File getDeployedPluginFile(File pluginsHome, String pluginName) {
		File[] files = pluginsHome.listFiles(new FileFilter() {
			@Override
			public boolean accept(File item) {
				return item.getName().startsWith(pluginName+"_");
			}
		});
		return (files==null || files.length == 0)? null:Arrays.asList(files).get(0);
	}

	private static String findServerOption(String[] args) {
		Optional<String> optional = Arrays.stream(args).filter(item -> item.startsWith("--server=")).findFirst();
		if(optional.isPresent()) {
			return optional.get().replace("--server=", "");
		} else {
			return "";
		}
	}
	
	private static String findPluginOption(String[] args) {
		Optional<String> optional = Arrays.stream(args).filter(item -> item.startsWith("--plugin=")).findFirst();
		if(optional.isPresent()) {
			return optional.get().replace("--plugin=", "");
		} else {
			return "";
		}
	}
}
