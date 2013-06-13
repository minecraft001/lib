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
 * 这是一个工具类,内有一些适合cb大部分版本通用的静态方法
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
	 * <b>检查</b>及<b>生成</b>缺少的必须文件.必须从jar文件到目标路径.
	 * @param sourceJarFile 配置文件所在的jar文件
	 * @param destPath 放置配置文件的目标文件夹路径
	 * @param filter 文件过滤器,确定jar中哪些文件需要解压到目标文件夹路径
	 * @return 成功返回true,否则返回false
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
	 * 获取容器中空格子的数量
	 * @param inv
	 * @return 没有返回0
	 */
	public static int getEmptySlots(Inventory inv) {
		int sum = 0;
		for (int i=0;i<inv.getSize();i++) {
			if (inv.getItem(i) == null || inv.getItem(i).getTypeId() == 0) sum++;
		}
		return sum;
	}
	
	/**
	 * 改变精度
	 * @param num 需要改变的实数
	 * @param accuracy 精确度,表示小数点后保留的位数,>=0,小于0会被当作0
	 * @return 改变精度后的实数(返回的小数部分长度可能比精确度小)
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
	 * 转换颜色字符<br>
	 * '\&'会被转换成'&'<br>
	 * '&'会被轮换成'/u00A7'
	 * @param s 需要转换的字符串
	 * @return 转换后的字符串,如果s为null则返回null
	 */
	public static String convert(String s){
		if (s == null) return null;
		s = s.replaceAll("/&", "\u0002");
		s = s.replaceAll("&", String.valueOf(ChatColor.COLOR_CHAR));
		s = s.replaceAll("\u0002", "&");
		return s;
	}
	
	/**
	 * 转换换行符<br>
	 * '\n '会被转换成'\n'
	 * @param s 需要转换的字符串
	 * @return 转换后的字符串,如果s为null则返回null
	 */
	public static String convertBr(String s) {
		if (s == null) return null;
		s = s.replaceAll("\\n ", "\n");
		return s.replaceAll("\\Q\\n\\E", "\n");
	}
	
	/**
	 * 从'plugin.yml'里获取插件版本
	 * @param plugin 插件对应的jar文件
	 * @return 插件版本字符串,出错返回null
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
	 * 获取客户端版本
	 * @param server
	 * @return 如"1.4.7"这种模式的,出错返回null
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
	 * 获取服务器的端口
	 * @param server
	 * @return 端口
	 */
	public static int getPort(Server server) {
		return server.getPort();
	}
	
	/**
	 * 将整数转换为byte数组
	 * @param i 整数
	 * @return byte数组,长度为4
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
	 * 将byte数组转换为整数
	 * @param byteArray byte数组,长度为4
	 * @return 对应的整数
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
	 * 字节数组转换为字符串
	 * @param target 字节数组
	 * @return 字符串
	 */
	public static String charsToStr(char[] target) {
		StringBuffer buf = new StringBuffer();
		for (int i=0;i<target.length;i++) 
			buf.append(target[i]);
		return buf.toString();
	}

	/**
	 * 字符串转换为字节数组
	 * @param str 字符串
	 * @return 字节数组
	 */
	public static char[] StrToChars(String str) {
		char[] buf = new char[str.length()];
		for (int i=0;i<str.length();i++) 
			buf[i] = str.charAt(i);
		return buf;
	}
	
	/**
	 * 获取当前tps
	 * @return 0-20,-1表示暂无
	 */
	public static double getTps() {
		return Tps.getTps();
	}
	
	/**
	 * 检测玩家是否可以使用命令
	 * @param p 检测的玩家
	 * @return 可以使用命令返回true,否则表示命令速度过快,返回false
	 */
	public static boolean checkSpeed(Player p) {
		return Lib.checkSpeed(p);
	}
	
	/**
	 * 检测玩家背包是否为空
	 * @param p 检测的玩家
	 * @return 背包为空返回true,否则返回false
	 */
	public static boolean checkEmpty(Player p) {
		PlayerInventory pi = p.getInventory();
		for (int i=0;i<40;i++) if (pi.getItem(i) != null) return false;
		return true;
	}
	
	/**
	 * 检测inv中是否有指定数量的物品
	 * @param inv Inventory
	 * @param id 物品id
	 * @param amount 物品数量
	 * @return 是否含有指定数量的物品
	 */
	public static boolean hasItem(Inventory inv,int id,int amount) {
		return inv.contains(id, amount);
	}
	
	/**
	 * 从指定inv中移除指定数量的指定物品
	 * @param inv Inventory
	 * @param id 物品id
	 * @param amount 要移除的数量
	 * @param force 如果inv中物品数量不足,是否移除已经拥有的
	 * @return 如果inv中没有指定数量的指定物品,返回false
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
	 * 检测指定inv中指定id的物品的数量
	 * @param inv Inventory
	 * @param id 物品id
	 * @return 数量,>=0
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
	 * 获取物品lore中包含指定信息的行
	 * @param itemStack 物品
	 * @param message 信息
	 * @return 返回第一次检测成功的行,没有则返回null
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
	 * 获取日期时间(格式yyyy-MM-dd HH:mm)
	 * @param start 开始计算的日期
	 * @param addDay 增加的天数
	 * @param addHour 增加的小时数
	 * @param addMinute 增加的分钟数
	 * @return 出错返回null
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
	 * @return 返回当前的日期时间(格式yyyy-MM-dd HH:mm)
	 */
	public static String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(new Date());
	}
}
