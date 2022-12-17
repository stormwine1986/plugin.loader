package plugin.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Parameters {
	

	private HashMap<String, String> p;

	/**
	 * MKSSI_NISSUE,MKSSI_PORT,MKSSI_ISSUE0,MKSSI_QUERY,MKSSI_USER,MKSSI_HOST
	 * 
	 * @param args
	 */
	public Parameters(String[] args) {
		// 来自插件参数
		p = new HashMap<>();
		p.put(ENVS.HOSTNAME, System.getenv("MKSSI_HOST"));
		p.put(ENVS.PORT, System.getenv("MKSSI_PORT"));
		p.put(ENVS.USER, System.getenv("MKSSI_USER"));
		Integer n = Integer.valueOf(System.getenv("MKSSI_NISSUE")==null?"0":System.getenv("MKSSI_NISSUE"));
		List<String> selectedIds = new ArrayList<>();
		for(int i = 0; i < n; i++) {
			selectedIds.add(System.getenv("MKSSI_ISSUE" + i));
		}
		String ids = selectedIds.stream().collect(Collectors.joining(","));
		p.put(ENVS.SELECTED, ids);
		/// ---------------------------- 来自命令行参数 ----------------------------------------------
		if(args != null) {
			List<String> list = Arrays.asList(args);
			p.put(ENVS.PLUGINNAME, findExtra("--app=", list));
			p.put(ENVS.HOSTNAME, findExtra("--host=", list)==null?p.get(ENVS.HOSTNAME):findExtra("--host=", list));
			p.put(ENVS.PORT, findExtra("--port=", list)==null?p.get(ENVS.PORT):findExtra("--port=", list));
			p.put(ENVS.USER, findExtra("--user=", list)==null?p.get(ENVS.USER):findExtra("--user=", list));
			p.put(ENVS.SELECTED, findExtra("--ids=", list)==null?p.get(ENVS.SELECTED):findExtra("--ids=", list));
		}
	}
	
	private String findExtra(String opt, List<String> list) {
		Optional<String> optional = list.stream().filter(item -> item.startsWith(opt)).findFirst();
		if(optional.isPresent()) {
			return optional.get().replace(opt, "");
		} else {
			return null;
		}
	}

	public Map<String, String> toMap() {
		return p;
	}
}
