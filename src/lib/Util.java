package lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * ����һ��������,����һЩ�ʺ�cb�󲿷ְ汾ͨ�õľ�̬����
 */
public class Util {
	private static final String VERSION_PATTERN = "\\(MC: [0-9.]{5}\\)";
	private static Excute excute;
	
	public static void init(Lib lib) {
		Util.excute = new Excute(lib);
		lib.getServer().getScheduler().scheduleSyncRepeatingTask(lib, excute, 10, 10);
	}
	
	public static void addToSender(CommandSender sender, String msg) {
		excute.add(new Cell(false,sender,msg));
	}
	
	/**
	 * <b>���</b>��<b>����</b>ȱ�ٵı����ļ�.�����jar�ļ���Ŀ��·��.
	 * @param sourceJarFile �����ļ����ڵ�jar�ļ�
	 * @param destPath ���������ļ���Ŀ���ļ���·��
	 * @param filter �ļ�������,ȷ��jar����Щ�ļ���Ҫ��ѹ��Ŀ���ļ���·��
	 * @return �ɹ�����true,���򷵻�false
	 */
	public static boolean generateFiles(File sourceJarFile,String destPath,HashList<Pattern> filter){
		JarInputStream jis = null;
		FileOutputStream fos = null;
		try {
			new File(destPath).mkdirs();
			jis = new JarInputStream(new FileInputStream(sourceJarFile));
			JarEntry entry;
			byte[] buff = new byte[1024];
			int read;
			while ((entry = jis.getNextJarEntry()) != null) {
				String fileName = entry.getName();
				for (Pattern pattern:filter) {
					Matcher matcher = pattern.matcher(fileName);
					if (matcher.find()) {
						if (!new File(destPath+File.separator+fileName).exists()) {
							fos = new FileOutputStream(destPath+File.separator+fileName);
							while((read = jis.read(buff)) > 0) fos.write(buff, 0, read);
							fos.close();
						}
					}
				}
			}
			return true;
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		} finally {
			try {
				if (jis != null) jis.close();
			} catch (IOException e) {
				return false;
			}
			try {
				if (fos != null) fos.close();
			} catch (IOException e) {
				return false;
			}
		}
	}
	
	/**
	 * ��ȡ�����пո��ӵ�����
	 * @param inv
	 * @return û�з���0
	 */
	public static int getEmptySlots(Inventory inv) {
		int sum = 0;
		for (int i=0;i<inv.getSize();i++) {
			if (inv.getItem(i) == null || inv.getItem(i).getTypeId() == 0) sum++;
		}
		return sum;
	}
	
	/**
	 * �ı侫��
	 * @param num ��Ҫ�ı��ʵ��
	 * @param accuracy ��ȷ��,��ʾС���������λ��,>=0,С��0�ᱻ����0
	 * @return �ı侫�Ⱥ��ʵ��(���ص�С�����ֳ��ȿ��ܱȾ�ȷ��С)
	 */
	public static double getDouble(double num,int accuracy) {
		if (accuracy < 0) accuracy = 0;
		String s = String.valueOf(num);
		if (s.split("\\.").length == 2) {
			String[] ss = s.split("\\.");
			return Double.parseDouble(ss[0]+"."+ss[1].substring(0, Math.min(accuracy, ss[1].length())));
		}else return num;
	}
	
	/**
	 * ת����ɫ�ַ�<br>
	 * '\&'�ᱻת����'&'<br>
	 * '&'�ᱻ�ֻ���'/u00A7'
	 * @param s ��Ҫת�����ַ���
	 * @return ת������ַ���,���sΪnull�򷵻�null
	 */
	public static String convert(String s){
		if (s == null) return null;
		s = s.replaceAll("/&", "\u0002");
		s = s.replaceAll("&", String.valueOf(ChatColor.COLOR_CHAR));
		s = s.replaceAll("\u0002", "&");
		return s;
	}
	
	/**
	 * ת�����з�<br>
	 * '\n '�ᱻת����'\n'
	 * @param s ��Ҫת�����ַ���
	 * @return ת������ַ���,���sΪnull�򷵻�null
	 */
	public static String convertBr(String s) {
		if (s == null) return null;
		s = s.replaceAll("\\n ", "\n");
		return s.replaceAll("\\Q\\n\\E", "\n");
	}
	
	/**
	 * ��'plugin.yml'���ȡ����汾
	 * @param plugin �����Ӧ��jar�ļ�
	 * @return ����汾�ַ���,������null
	 */
	public static String getPluginVersion(File plugin) {
		JarInputStream jis = null;
		try {
			jis = new JarInputStream(new FileInputStream(plugin));
			JarEntry entry;
			while ((entry = jis.getNextJarEntry()) != null) {
				String fileName = entry.getName();
				if (fileName.equalsIgnoreCase("plugin.yml")) {
					YamlConfiguration config = new YamlConfiguration();
					config.load(jis);
					return config.getString("version",null);
				}
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} catch (InvalidConfigurationException e) {
		} finally {
			try {
				if (jis != null) jis.close();
			} catch (IOException e) {
				return null;
			}
		}
		return null;
	}
	
	/**
	 * ��ȡ�ͻ��˰汾
	 * @param server
	 * @return ��"1.4.7"����ģʽ��,������null
	 */
	public static String getMcVersion(Server server) {
		try {
			Pattern p = Pattern.compile(VERSION_PATTERN);
			Matcher m = p.matcher(server.getBukkitVersion());
			if (m.find()) {
				String result = m.group();
				if (result != null && !result.trim().isEmpty()) return result.substring(result.indexOf(" ")+1,result.indexOf(")"));
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * ��ȡ�������Ķ˿�
	 * @param server
	 * @return �˿�
	 */
	public static int getPort(Server server) {
		return server.getPort();
	}
	
	/**
	 * ������ת��Ϊbyte����
	 * @param i ����
	 * @return byte����,����Ϊ4
	 */
	public static byte[] intToByteArray(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}
	
	/**
	 * ��byte����ת��Ϊ����
	 * @param byteArray byte����,����Ϊ4
	 * @return ��Ӧ������
	 */
	public static int byteArrayToInt(byte[] byteArray) {
		int result = 0;
		int b0 = byteArray[0];
		int b1 = byteArray[1];
		int b2 = byteArray[2];
		int b3 = byteArray[3];
		result = result | (b0 << 24);
		result = result | (b1 << 16 & 0x00FF0000);
		result = result | (b2 << 8 & 0x0000FF00);
		result = result | b3 & 0x000000FF;
		return result;
	}
	
	/**
	 * �ֽ�����ת��Ϊ�ַ���
	 * @param target �ֽ�����
	 * @return �ַ���
	 */
	public static String charsToStr(char[] target) {
		StringBuffer buf = new StringBuffer();
		for (int i=0;i<target.length;i++) 
			buf.append(target[i]);
		return buf.toString();
	}

	/**
	 * �ַ���ת��Ϊ�ֽ�����
	 * @param str �ַ���
	 * @return �ֽ�����
	 */
	public static char[] StrToChars(String str) {
		char[] buf = new char[str.length()];
		for (int i=0;i<str.length();i++) 
			buf[i] = str.charAt(i);
		return buf;
	}
	
	/**
	 * ��ȡ��ǰtps
	 * @return 0-20,-1��ʾ����
	 */
	public static double getTps() {
		return Tps.getTps();
	}
	
	/**
	 * �������Ƿ����ʹ������
	 * @param p �������
	 * @return ����ʹ�������true,�����ʾ�����ٶȹ���,����false
	 */
	public static boolean checkSpeed(Player p) {
		return Lib.checkSpeed(p);
	}
	
	/**
	 * �����ұ����Ƿ�Ϊ��
	 * @param p �������
	 * @return ����Ϊ�շ���true,���򷵻�false
	 */
	public static boolean checkEmpty(Player p) {
		PlayerInventory pi = p.getInventory();
		for (int i=0;i<40;i++) if (pi.getItem(i) != null) return false;
		return true;
	}
	
	/**
	 * ���inv���Ƿ���ָ����������Ʒ
	 * @param inv Inventory
	 * @param id ��Ʒid
	 * @param amount ��Ʒ����
	 * @return �Ƿ���ָ����������Ʒ
	 */
	public static boolean hasItem(Inventory inv,int id,int amount) {
		return inv.contains(id, amount);
	}
	
	/**
	 * ��ָ��inv���Ƴ�ָ��������ָ����Ʒ
	 * @param inv Inventory
	 * @param id ��Ʒid
	 * @param amount Ҫ�Ƴ�������
	 * @param force ���inv����Ʒ��������,�Ƿ��Ƴ��Ѿ�ӵ�е�
	 * @return ���inv��û��ָ��������ָ����Ʒ,����false
	 */
	public static boolean removeItem(Inventory inv,int id,int amount,boolean force) {
		if (amount <= 0) return true;
		if (hasItem(inv, id, amount)) {
			for (int i=0;i<inv.getSize();i++) {
				if (inv.getItem(i) != null && inv.getItem(i).getTypeId() == id) {
					if (amount >= inv.getItem(i).getAmount()) {
						amount -= inv.getItem(i).getAmount();
						inv.setItem(i, null);
					}else {
						inv.getItem(i).setAmount(inv.getItem(i).getAmount()-amount);
						amount = 0;
					}
					if (amount <= 0) break;
				}
			}
			return true;
		}else if (force) {
			for (int i=0;i<inv.getSize();i++) {
				if (inv.getItem(i) != null && inv.getItem(i).getTypeId() == id) {
					if (amount >= inv.getItem(i).getAmount()) {
						amount -= inv.getItem(i).getAmount();
						inv.setItem(i, null);
					}else {
						inv.getItem(i).setAmount(inv.getItem(i).getAmount()-amount);
						amount = 0;
					}
					if (amount <= 0) break;
				}
			}
			return false;
		}else return false;
	}
	
	/**
	 * ���ָ��inv��ָ��id����Ʒ������
	 * @param inv Inventory
	 * @param id ��Ʒid
	 * @return ����,>=0
	 */
	public static int getAmount(Inventory inv,int id) {
		int sum = 0;
		for (int i=0;i<inv.getSize();i++) {
			if (inv.getItem(i) != null && inv.getItem(i).getTypeId() == id) {
				sum += inv.getItem(i).getAmount();
			}
		}
		return sum;
	}
	
	/**
	 * ��ȡ��Ʒlore�а���ָ����Ϣ����
	 * @param itemStack ��Ʒ
	 * @param message ��Ϣ
	 * @return ���ص�һ�μ��ɹ�����,û���򷵻�null
	 */
	public static String getLine(ItemStack itemStack, String message) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta == null) return null;
		List<String> lore = itemMeta.getLore();
		if (lore == null) return null;
		for (String s:lore) {
			if (s.indexOf(message) != -1) return s;
		}
		return null;
	}
	
	/**
	 * ��ȡ����ʱ��(��ʽyyyy-MM-dd HH:mm)
	 * @param start ��ʼ���������
	 * @param addDay ���ӵ�����
	 * @param addHour ���ӵ�Сʱ��
	 * @param addMinute ���ӵķ�����
	 * @return ������null
	 */
	public static String getDateTime(Date start, int addDay, int addHour, int addMinute) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Calendar calendar= Calendar.getInstance();
		calendar.setTime(start);
		calendar.add(Calendar.DAY_OF_MONTH, addDay);
		calendar.add(Calendar.HOUR_OF_DAY, addHour);
		calendar.add(Calendar.MINUTE, addMinute);
		return sdf.format(calendar.getTime());
	}
	
	/**
	 * @return ���ص�ǰ������ʱ��(��ʽyyyy-MM-dd HH:mm)
	 */
	public static String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(new Date());
	}
}
