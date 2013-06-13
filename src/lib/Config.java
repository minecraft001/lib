package lib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;



/**
 * ����ע��Ĳ�������ļ�ͳһ������,һ��������ֻ����һ��ʵ��
 */
public class Config{
	class ConfigMeta {
		private File sourceJarFile;
		private String destPath;//��������ļ���·��
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
	//�����,ConfigItem
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
	 * ע�������ļ�,����ͳһ����
	 * <br>ע��󼴼�����������ļ�
	 * <br>��һ��ע�����Ҫ����һ��loadConfig(pluginName)���򲻻��ȡ����
	 * <br>һ�����ֻ��ע��һ��
	 * @param sourceJarFile �����ļ����ڵ�jar�ļ�
	 * @param destPath ���������ļ���Ŀ���ļ���·��
	 * @param filter �ļ�������,ȷ��jar����Щ�ļ���Ҫ��ѹ��Ŀ���ļ���·��,Ϊnull��ʾʹ��Ĭ�Ϲ�����
	 * @param pluginName ע��Ĳ����
	 */
	public void register(File sourceJarFile,String destPath,HashList<Pattern> filter,String pluginName) {
		if (filter == null) filter = defaultFilter;
		ConfigMeta configItem = new ConfigMeta(sourceJarFile, destPath, filter, pluginName);
		configItemHash.put(pluginName, configItem);
		Util.generateFiles(sourceJarFile,destPath,filter);
	}

	/**
	 * <b>����</b>��(����)<b>��ȡ</b>�����ļ�,ͬʱ�������¶�ȡ�����ļ��¼�
	 * <br>���Զ�����ע�������ļ�����
	 * <br>���Զ�ε���,ÿ�ε����൱�����¶�ȡ�����ļ�
	 * <br>��������쳣��ᱣ��ԭ��������,Ҳ���ᷢ�����¶�ȡ�����ļ��¼�
	 * @param pluginName �����
	 * @return �ɹ����� true,���򷵻�false
	 * @throws InvalidConfigurationException �����ļ��쳣
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
	 * ��ȡ�����Ӧ�������ļ�
	 * <br>�Ƽ���Ҫ��ȡ�󱣴渱��,����ÿ��ʹ�ö����»�ȡ�ķ���
	 * <br>�������׳���,�������Ҫ���������ļ����¶�ȡ�¼�
	 * @param pluginName �����
	 * @return ��Ӧ�Ķ�ȡ���ڴ��е������ļ�,������null
	 */
	public YamlConfiguration getConfig(String pluginName) {
		return configHash.get(pluginName);
	}
}
