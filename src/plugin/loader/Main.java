package plugin.loader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * loader 部分的入口
 * 
 * @author pjia
 *
 */
public class Main {
	
	public static void main(String[] args) throws IOException {
		try {
			// 生成参数对象
			Parameters parameters = new Parameters(args);
			Map<String, String> envs = parameters.toMap();
			Logger.append("envs = " + envs);
			// 生成插件描述对象
			Descriptor descriptor = new Descriptor(envs);
			// 获取服务器上的插件描述
			RemotePlugin remotePlugin = descriptor.getRemotePlugin();
			// 获取本地的插件描述
			LocalPlugin localPlugin = new LocalPlugin(envs);
			if(!localPlugin.isReady()) {
				// 如果插件没有准备好，从服务器上下载，然后启动
				remotePlugin.download().lauch(args);
			} else {
				if(remotePlugin.isNewThan(localPlugin)) {
					// 如果本地插件不是最新，下载插件，更新本地插件，然后启动
					LocalPlugin newLocalPlugin = remotePlugin.download();
					localPlugin.delete();
					newLocalPlugin.lauch(args);
				} else {
					// 如果本地是最新的，直接启动
					localPlugin.lauch(args);
				}
			}
		}catch (Throwable e) {
			// 输出异常日志
			e.printStackTrace();
			File userHomeDir = new File(System.getProperty("user.home"));
			File logFile = new File(userHomeDir.getAbsoluteFile() + "/" + "loader_" + (new Date()).getTime() + ".log");
			FileOutputStream os = new FileOutputStream(logFile);
			os.write(Logger.getContents().getBytes());
			os.write("\n".getBytes());
			os.write(e.toString().getBytes());
			os.write("\n".getBytes());
			StackTraceElement[] stackTrace = e.getStackTrace();
			if(stackTrace!=null) {
				Arrays.stream(stackTrace).forEach(item -> {
					try {
						os.write("\t".getBytes());
						os.write(item.toString().getBytes());
						os.write("\n".getBytes());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				});
			}
			os.flush();
			os.close();
		}
	}
}
