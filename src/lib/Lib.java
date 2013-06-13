package lib;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Lib extends JavaPlugin{
	//basic
	private String pn;
	private Server server;
	private PluginManager pm;
	private BukkitScheduler scheduler;
	/**
	 * ��������ڵ��ļ���·��
	 */
	private String mainPath;
	/**
	 * ����ļ���·��
	 */
	private String pluginPath;
	/**
	 * ��������ļ���·��
	 */
	private String dataFolder;
	private String pluginVersion;
	//need
	private Per per;
	private Eco eco;
	private Format f;
	private static CmdSpeedLimit csl;
	
	//database
//	private Dao dao;
	//config
	private Config con;
	//other
	private Tps tps;

	@Override
	public void onLoad() {
	}
	
	//�������
	@Override
	public void onEnable() {
		//basic
		initBasic();
		//need
		initNeed();
		//init config
		initConfig();
		//database
		initDatabase();
		//loadData
		loadData();
		//other
		tps = new Tps(this);
		Util.init(this);
		//metrics
		try {
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		} catch (IOException e) {
		}
		//�ɹ�����
		sendConsoleMessage(f.f(pn, "pluginEnabled", new Object[]{pn,pluginVersion}));
	}
	
	//ֹͣ���
	@Override
	public void onDisable() {
		//��ʱ��
		scheduler.cancelAllTasks();
		//���ݿ����ӹر�
//		dao.close();
		//��ʾ����ɹ�ֹͣ��Ϣ
		sendConsoleMessage(f.f(pn, "pluginDisabled", new Object[]{pn,pluginVersion}));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,String label, String[] args) {
		Player p = null;
		if (!(sender instanceof ConsoleCommandSender)) p = (Player)sender;
		String cmdName = cmd.getName();
		int length = args.length;
		if (cmdName.equalsIgnoreCase("lib")) {
			if (!(length == 1 && args[0].equalsIgnoreCase("?"))) {
				if (length == 1) {
					if (args[0].equalsIgnoreCase("reloadConfig") || args[0].equalsIgnoreCase("rc")) {
						reloadConfig(sender);
						return true;
					}
				}
			}
			sender.sendMessage(f.f(pn, "cmdHelpHeader",get(110)));
			if (p == null || per.has(p, getReloadPermission())) 
				sender.sendMessage(f.f(pn,"cmdHelpItem",new Object[]{get(115),get(116)}));
		}
		return true;
	}

	/**
	 * ��ʾ����Ϣ��ʾ������̨��
	 * @param msg ��ʾ����Ϣ
	 */
	private void sendConsoleMessage(String msg) {
		if (server.getConsoleSender() != null) server.getConsoleSender().sendMessage(msg);
		else server.getLogger().info(msg);
	}
	
	/**
	 * @return ������null
	 */
	private String getReloadPermission() {
		String result = null;
		if (con != null && con.getConfig(pn) != null) result = con.getConfig(pn).getString("per_lib_reloadConfig");
		return result;
	}
	
	/**
	 * ��ʼ����������
	 */
	private void initBasic() {
		pn = getName();
		server = getServer();
		pm = server.getPluginManager();
		scheduler = server.getScheduler();
		mainPath = System.getProperty("user.dir");
		pluginPath = getFile().getParentFile().getAbsolutePath();
		dataFolder = pluginPath+File.separator+pn;
		pluginVersion = Util.getPluginVersion(getFile());
	}
	
	private void initNeed() {
		per = new Per(this);
		eco = new Eco(this);
		f = new Format(this);
		csl = new CmdSpeedLimit(this);
	}
	
	private void initConfig() {
		con = new Config(this);//һ���������ĵ�һʵ��
		
		HashList<Pattern> filter = con.getDefaultFilter().clone();
		filter.add(Pattern.compile("hibernate.cfg.xml"));
		con.register(new File(pluginPath+File.separator+"lib.jar"), dataFolder, filter , pn);
		loadConfig(null);
	}

	private void initDatabase() {
//		dao = new Dao(this);
//		createTables();
	}
	
	

//	private void createTables() {
//	}
	
	private void loadData() {
	}

	/**
	 * �������¶�ȡ�����ļ�����
	 * @param sender
	 */
	private void reloadConfig(CommandSender sender) {
		Player p = null;
		String permission = getReloadPermission();
		if (sender instanceof Player) p = (Player)sender;
		if (p != null && !per.has(p, permission)) p.sendMessage(f.f(pn,"noPer",permission));
		else {
			if (loadConfig(sender)) sender.sendMessage(f.f(pn, "success", get(120)));
			else sender.sendMessage(f.f(pn, "fail", get(121)));
		}
	}

	/**
	 * ���¶�ȡ�����ļ�
	 * @param sender ��Ϊnull,����ָ���쳣��Ϣ�Ľ�����
	 */
	private boolean loadConfig(CommandSender sender) {
		try {
			return con.loadConfig(pn);
		} catch (InvalidConfigurationException e) {
			if (sender == null) sender = server.getConsoleSender();
			if (sender != null) sender.sendMessage(e.getMessage());
			else server.getLogger().info(e.getMessage());
			return false;
		}
	}

	private String get(int id) {
			return f.get(pn, id);
	}

	public String getMainPath() {
		return mainPath;
	}

	public String getPn() {
		return pn;
	}
	
	public String getPluginPath() {
		return pluginPath;
	}

	public PluginManager getPm() {
		return pm;
	}

	public Tps getTps() {
		return tps;
	}

	public static boolean checkSpeed(Player p) {
		if (csl == null) return true;
		else return csl.check(p);
	}
	
	public Format getFormat() {
		return f;
	}

	public Per getPer() {
		return per;
	}

	public Eco getEco() {
		return eco;
	}

	public Config getCon() {
		return con;
	}
}
