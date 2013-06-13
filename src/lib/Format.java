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
 * ����ע��Ĳ������ͳһ������,һ��������ֻ����һ��ʵ��
 * <br>ʹ�������ļ����language����
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
	 * ��ȡָ����ͨ�����ı�
	 * @param pluginName �������������������
	 * @param id ����id
	 * @return �����ı�,���û���򷵻�""
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
	 * ��ʽת��
	 * @param pn �����
	 * @param type ת������
	 * @param args �����б�
	 * @return ת������ַ���
	 */
	public String f(String pn,String type,Object[] args) {
		return format(pn,type,args);
	}
	
	/**
	 * ��ʽת��
	 * @param pn �����
	 * @param type ת������
	 * @param args �����б�
	 * @return ת������ַ���
	 */
	public String f(String pn,String type,List<Object> args) {
		return format(pn,type,args.toArray());
	}
	
	/**
	 * ��ʽת��
	 * @param pn �����
	 * @param type ת������
	 * @param args ������
	 * @return ת������ַ���
	 */
	public String f(String pn,String type,Object args) {
		if (args == null) args = "";
		Object[] ss = {args};
		return format(pn,type,ss);
	}
	
	/**
	 * ��ʽת��
	 * @param pn �����
	 * @param type ת������
	 * @param args �����б�,��Ϊnull
	 * @return ת������ַ���,������""
	 */
	public String format(String pn,String type,Object[] args) {
		if (args == null) args = new Object[]{};
		String result;
		//ʹ�ò���Լ��������ļ�
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
		//ʹ��lib�������ļ�
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
	 * ע��<b>��ͨ</b>������<b>��ʽ</b>����
	 * @param pn �����
	 * @param languageConfig �����ļ�config
	 * @return �ɹ�����true,���򷵻�false
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
	 * ��ͨ����ʹ��
	 * @param s
	 * @return ������-1
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
	 * ��ʽ����ʹ��
	 * @param s
	 * @return ������null
	 */
	private String getName(String s) {
		int index = s.indexOf(":");
		if (index >= 1) {
			return s.substring(0, index);
		}else return null;
	}
	
	/**
	 * ��ʽ����ʹ��
	 * @param s
	 * @return ������null
	 */
	private String getValue(String s) {
		int index = s.indexOf(":");
		if (index != -1) {
			return s.substring(index+1, s.length());
		}else return null;
	}
}
