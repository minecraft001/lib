package lib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;



/**
 * 所有注册的插件配置文件统一管理类,一个服务器只存在一个实例
 */
public class Config{
	class ConfigMeta {
		private File sourceJarFile;
		private String destPath;//存放配置文件的路径
		private HashList<Pattern> filter;
		private String pluginName;
		public ConfigMeta(File sourceJarFile, String destPath,HashList<Pattern> filter, String pluginName) {
			this.sourceJarFile = sourceJarFile;
			this.destPath = destPath;
			this.filter = filter;
			this.pluginName = pluginName;
		}
		public File getSourceJarFile() {
			return sourceJarFile;
		}
		public String getDestPath() {
			return destPath;
		}
		public HashList<Pattern> getFilter() {
			return filter;
		}
		public String getPluginName() {
			return pluginName;
		}
	}
	
	private HashList<Pattern> defaultFilter;
	private Lib lib;
	//插件名,ConfigItem
	private HashMap<String,ConfigMeta> configItemHash;
	private HashMap<String,YamlConfiguration> configHash;
	
	Config(Lib lib) {
		defaultFilter = new HashListImpl<Pattern>();
		defaultFilter.add(Pattern.compile("config.yml"));
		defaultFilter.add(Pattern.compile("config_[a-zA-Z]+.yml"));
		defaultFilter.add(Pattern.compile("language.yml"));
		defaultFilter.add(Pattern.compile("language_[a-zA-Z]+.yml"));
		defaultFilter.add(Pattern.compile("hibernate.cfg.xml"));
		
		this.lib = lib;
		
		configItemHash = new HashMap<String, Config.ConfigMeta>();
		configHash = new HashMap<String, YamlConfiguration>();
	}
	
	public HashList<Pattern> getDefaultFilter() {
		return defaultFilter.clone();
	}

	/**
	 * 注册配置文件,接受统一管理
	 * <br>注册后即检测生成配置文件
	 * <br>第一次注册后需要调用一次loadConfig(pluginName)否则不会读取配置
	 * <br>一个插件只需注册一次
	 * @param sourceJarFile 配置文件所在的jar文件
	 * @param destPath 放置配置文件的目标文件夹路径
	 * @param filter 文件过滤器,确定jar中哪些文件需要解压到目标文件夹路径,为null表示使用默认过滤器
	 * @param pluginName 注册的插件名
	 */
	public void register(File sourceJarFile,String destPath,HashList<Pattern> filter,String pluginName) {
		if (filter == null) filter = defaultFilter;
		ConfigMeta configItem = new ConfigMeta(sourceJarFile, destPath, filter, pluginName);
		configItemHash.put(pluginName, configItem);
		Util.generateFiles(sourceJarFile,destPath,filter);
	}

	/**
	 * <b>生成</b>并(重新)<b>读取</b>配置文件,同时发出重新读取配置文件事件
	 * <br>会自动重新注册语言文件管理
	 * <br>可以多次调用,每次调用相当于重新读取配置文件
	 * <br>如果出现异常则会保持原来的配置,也不会发出重新读取配置文件事件
	 * @param pluginName 插件名
	 * @return 成功返回 true,否则返回false
	 * @throws InvalidConfigurationException 配置文件异常
	 */
	public boolean loadConfig(String pluginName) throws InvalidConfigurationException {
		ConfigMeta configItem = configItemHash.get(pluginName);
		if (configItem == null) return false;
		Util.generateFiles(configItem.getSourceJarFile(),configItem.getDestPath(),configItem.getFilter());
		try {
			YamlConfiguration config = new YamlConfiguration();
			config.load(configItem.getDestPath()+File.separator+"config.yml");
			configHash.put(pluginName, config);
			ReloadConfigEvent e = new ReloadConfigEvent(configItem.getDestPath(),configItem.getPluginName(),config);
			lib.getPm().callEvent(e);
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 获取插件对应的配置文件
	 * <br>推荐不要获取后保存副本,而是每次使用都重新获取的方法
	 * <br>这样不易出错,否则就需要监听配置文件重新读取事件
	 * @param pluginName 插件名
	 * @return 对应的读取到内存中的配置文件,出错返回null
	 */
	public YamlConfiguration getConfig(String pluginName) {
		return configHash.get(pluginName);
	}
}
