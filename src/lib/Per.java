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
	 * �������Ƿ���Ȩ��
	 * @param p �������
	 * @param per ��Ӧ��Ȩ��
	 * @return ���Ȩ��Ϊ""���Ȼ����true
	 */
	public boolean has(Player p, String per) {
		if (per.trim().isEmpty()) return true;
		else return permission.playerHas(p, per);
	}
	
	/**
	 * ��������ָ����Ȩ��
	 * @param p ���
	 * @param per Ȩ��
	 * @return �ɹ���ӷ���true
	 */
	public boolean add(Player p, String per) {
		server.dispatchCommand(server.getConsoleSender(), "pex user "+p.getName()+" add "+per);
		return true;
	}
	
	/**
	 * ȥ����ҵ�ָ��Ȩ��
	 * @param p ���
	 * @param per Ȩ��
	 * @return �ɹ�ȥ������true
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
