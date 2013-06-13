package lib;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Per{
	private Server server;
	private PluginManager pm;
	private static Permission permission = null;
	
	Per(Lib lib) {
		server = lib.getServer();
		pm = lib.getPm();
		setupPermissions();
	}
	
	private boolean setupPermissions(){
		if (pm.getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Permission> permissionProvider = server.getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
	
	/**
	 * 检测玩家是否有权限
	 * @param p 检测的玩家
	 * @param per 相应的权限
	 * @return 如果权限为""则必然返回true
	 */
	public boolean has(Player p, String per) {
		if (per.trim().isEmpty()) return true;
		else return permission.playerHas(p, per);
	}
	
	/**
	 * 给玩家添加指定的权限
	 * @param p 玩家
	 * @param per 权限
	 * @return 成功添加返回true
	 */
	public boolean add(Player p, String per) {
		server.dispatchCommand(server.getConsoleSender(), "pex user "+p.getName()+" add "+per);
		return true;
	}
	
	/**
	 * 去除玩家的指定权限
	 * @param p 玩家
	 * @param per 权限
	 * @return 成功去除返回true
	 */
	public boolean remove(Player p, String per) {
		server.dispatchCommand(server.getConsoleSender(), "pex user "+p.getName()+" remove "+per);
		return true;
	}
	
	public boolean inGroup(Player p,String group) {
		return permission.playerInGroup(p, group);
	}
	
	public boolean addGroup(Player p,String group) {
		return permission.playerAddGroup(p, group);
	}
}
