package lib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;



/**
 * 所有注册的插件语言统一管理类,一个服务器只存在一个实例
 * <br>使用配置文件里的language配置
 */
public class Format implements Listener{
	private static final Pattern plainPattern = Pattern.compile("lang_\\d{1,5}");
	private static final Pattern formatPattern = Pattern.compile("lang-\\d{1,5}");
	
	private Lib lib;

	private HashMap<String,HashMap<Integer,String>> plainHash;//plain language
	private HashMap<String,HashMap<String,String>> formatHash;//format language
	
	Format(Lib lib) {
		this.lib = lib;
		lib.getPm().registerEvents(this, lib);
		plainHash = new HashMap<String, HashMap<Integer,String>>();
		formatHash = new HashMap<String, HashMap<String,String>>();
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	void reloadConfig(ReloadConfigEvent e) {
		if (!e.getConfig().contains("language")) return;
		String languagePath = e.getDestPath()+File.separator+e.getConfig().getString("language");
		YamlConfiguration languageConfig = new YamlConfiguration();
		try {
			languageConfig.load(languagePath);
			register(e.getCallPlugin(), languageConfig);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InvalidConfigurationException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * 获取指定普通语言文本
	 * @param pluginName 调用者所属插件的名字
	 * @param id 语言id
	 * @return 语言文本,如果没有则返回""
	 */
	public String get(String pluginName, int id) {
		if (plainHash.containsKey(pluginName)) {
			if (plainHash.get(pluginName).containsKey(id)) return plainHash.get(pluginName).get(id);
		}else {
			if (plainHash.get(lib.getPn()).containsKey(id)) return plainHash.get(lib.getPn()).get(id);
		}
		return "";
	}
	
	/**
	 * 格式转换
	 * @param pn 插件名
	 * @param type 转换类型
	 * @param args 变量列表
	 * @return 转换后的字符串
	 */
	public String f(String pn,String type,Object[] args) {
		return format(pn,type,args);
	}
	
	/**
	 * 格式转换
	 * @param pn 插件名
	 * @param type 转换类型
	 * @param args 变量列表
	 * @return 转换后的字符串
	 */
	public String f(String pn,String type,List<Object> args) {
		return format(pn,type,args.toArray());
	}
	
	/**
	 * 格式转换
	 * @param pn 插件名
	 * @param type 转换类型
	 * @param args 单变量
	 * @return 转换后的字符串
	 */
	public String f(String pn,String type,Object args) {
		if (args == null) args = "";
		Object[] ss = {args};
		return format(pn,type,ss);
	}
	
	/**
	 * 格式转换
	 * @param pn 插件名
	 * @param type 转换类型
	 * @param args 变量列表,可为null
	 * @return 转换后的字符串,出错返回""
	 */
	public String format(String pn,String type,Object[] args) {
		if (args == null) args = new Object[]{};
		String result;
		//使用插件自己的语言文件
		if (formatHash.containsKey(pn)) {
			result = formatHash.get(pn).get(type);
			if (result != null) {
				for (int i=0;i<args.length;i++){
					if (args[i] == null) args[i] = "";
					result = result.replaceAll("\\{"+i+"\\}", args[i].toString());
				}
				return result;
			}
		}
		//使用lib的语言文件
		result = formatHash.get(lib.getPn()).get(type);
		if (result != null) {
			for (int i=0;i<args.length;i++){
				if (args[i] == null) args[i] = "";
				result = result.replaceAll("\\{"+i+"\\}", args[i].toString());
			}
			return result;
		}else return "";
	}

	/**
	 * 注册<b>普通</b>语言与<b>格式</b>语言
	 * @param pn 插件名
	 * @param languageConfig 语言文件config
	 * @return 成功返回true,否则返回false
	 */
	private boolean register(String pn,YamlConfiguration languageConfig) {
		HashMap<Integer,String> hash1 = new HashMap<Integer, String>();
		HashMap<String,String> hash2 = new HashMap<String, String>();
		String name,value;
		for (String key:languageConfig.getKeys(true)) {
			if (plainPattern.matcher(key).matches()) {
				hash1.put(getId(key), Util.convertBr(Util.convert(languageConfig.getString(key))));
			}else if (formatPattern.matcher(key).matches()) {
				name = getName(languageConfig.getString(key));
				value = Util.convertBr(Util.convert(getValue(languageConfig.getString(key))));
				if (name == null || value == null || name.isEmpty()) return false;
				hash2.put(name, value);
			}
		}
		plainHash.put(pn, hash1);
		formatHash.put(pn, hash2);
		return true;
	}
	
	/**
	 * 普通语言使用
	 * @param s
	 * @return 出错返回-1
	 */
	private int getId(String s) {
		try {
			for (int i=0;i<s.length();i++) {
				if (s.charAt(i) >= '0' && s.charAt(i) <= '9') {
					return Integer.parseInt(s.substring(i, s.length()));
				}
			}
		} catch (NumberFormatException e) {
		}
		return -1;
	}
	
	/**
	 * 格式语言使用
	 * @param s
	 * @return 出错返回null
	 */
	private String getName(String s) {
		int index = s.indexOf(":");
		if (index >= 1) {
			return s.substring(0, index);
		}else return null;
	}
	
	/**
	 * 格式语言使用
	 * @param s
	 * @return 出错返回null
	 */
	private String getValue(String s) {
		int index = s.indexOf(":");
		if (index != -1) {
			return s.substring(index+1, s.length());
		}else return null;
	}
}
