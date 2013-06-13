package lib;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Eco{
	private Server server;
	private PluginManager pm;
	private static Economy econ = null;
	
	Eco(Lib lib) {
		server = lib.getServer();
		pm = lib.getPm();
		setupEconomy();
	}
	
	private boolean setupEconomy() {
	    if (pm.getPlugin("Vault") == null) return false;
	    RegisteredServiceProvider<Economy> rsp = server.getServicesManager().getRegistration(Economy.class);
	    if (rsp == null) return false;
	    econ = rsp.getProvider();
	    return econ != null;
	}

	/**
	 * @return 返回-1如果玩家不存在
	 */
	public double get(String name) {
		if (server.getOfflinePlayer(name).hasPlayedBefore()) return econ.getBalance(name);
		else return -1;
	}
	
	/**
	 * @return 返回-1如果玩家不存在
	 */
	public double get(Player p) {
		return get(p.getName());
	}
	
	/**
	 * @return 如果amount<0返回false否则返回true
	 */
	public boolean set(String name,int amount) {
		return set(name,(double)amount);
	}
	/**
	 * @return 如果amount<0返回false否则返回true
	 */
	public boolean set(Player p,int amount) {
		return set(p.getName(),amount);
	}
	/**
	 * @return 如果amount<0返回false否则返回true
	 */
	public boolean set(String name,double amount) {
		if (amount < 0) return false;
		if (!econ.hasAccount(name)) return false;
		double num = econ.getBalance(name);
		if (num < 0) {
			econ.depositPlayer(name, -num);
		}else if (num > 0) {
			econ.withdrawPlayer(name, num);
		}
		econ.depositPlayer(name, amount);
		return true;
	}
	/**
	 * @return 如果amount<0返回false否则返回true
	 */
	public boolean set(Player p,double amount) {
		return set(p.getName(),amount);
	}
	
	/**
	 * @return 如果amount<0返回false否则返回结果
	 */
	public boolean add(String name,int amount) {
		return add(name,(double)amount);
	}
	/**
	 * @return 如果amount<0返回false否则返回true
	 */
	public boolean add(Player p,int amount) {
		return add(p.getName(),amount);
	}
	/**
	 * @return 如果amount<0返回false否则返回true
	 */
	public boolean add(String name,double amount) {
		if (amount < 0) return false;
		if (!econ.hasAccount(name)) return false;
		return set(name, econ.getBalance(name)+amount);
	}
	/**
	 * @return 如果amount<0返回false否则返回true
	 */
	public boolean add(Player p,double amount) {
		return add(p.getName(),amount);
	}
	
	/**
	 * @return 如果amount<0返回false否则返回true
	 */
	public boolean del(String name,int amount) {
		return del(name,(double)amount);
	}
	/**
	 * @return 如果amount<0返回false否则返回true
	 */
	public boolean del(Player p,int amount) {
		return del(p.getName(),amount);
	}
	/**
	 * @return 如果amount<0返回false否则返回true
	 */
	public boolean del(String name,double amount) {
		if (amount < 0) return false;
		if (!econ.hasAccount(name)) return false;
		return set(name,econ.getBalance(name)-amount);
	}
	/**
	 * @return 如果amount<0返回false否则返回true
	 */
	public boolean del(Player p,double amount) {
		return del(p.getName(),amount);
	}
}
