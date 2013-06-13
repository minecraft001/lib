package lib;

import java.util.HashMap;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitScheduler;

public class CmdSpeedLimit implements Listener{
	static final int INIT_INTERVAL = 500;
	Lib lib;
	Server server;
	String pn;
	BukkitScheduler scheduler;
	Format f;
	Cmd cmd;
	
	public CmdSpeedLimit(Lib lib) {
		this.lib = lib;
		server = lib.getServer();
		pn = lib.getPn();
		scheduler = server.getScheduler();
		f = lib.getFormat();
		server.getPluginManager().registerEvents(this, lib);
		
		cmd = new Cmd();
		for (Player p:server.getOnlinePlayers()) cmd.join(p);
	}
	
	@EventHandler(priority=EventPriority.LOWEST,ignoreCancelled=true)
	public void playerJoin(PlayerJoinEvent e) {
		cmd.join(e.getPlayer());
	}
	
	@EventHandler(priority=EventPriority.LOWEST,ignoreCancelled=true)
	public void playerQuit(PlayerQuitEvent e) {
		cmd.leave(e.getPlayer());
	}
	
	@EventHandler(priority=EventPriority.LOWEST,ignoreCancelled=true)
	public void playerKick(PlayerKickEvent e) {
		cmd.leave(e.getPlayer());
	}
	
	public boolean check(Player p) {
		return cmd.check(p);
	}
	
	class Cmd {
		HashMap<Player,Boolean> canCmdHash;
		
		public Cmd() {
			canCmdHash = new HashMap<Player,Boolean>();
		}
		
		private void join(Player p) {
			canCmdHash.put(p, true);
		}
		
		private void leave(Player p) {
			canCmdHash.remove(p);
		}
		
		private boolean check(Player p) {
			if (p == null) throw new NullPointerException();
			boolean result = false;
			if (canCmdHash.containsKey(p)) result = canCmdHash.get(p);
			canCmdHash.put(p, false);
			int cmdInterval = INIT_INTERVAL;
			if (lib.getConfig() != null && lib.getCon().getConfig(pn) != null && lib.getCon().getConfig(pn).contains("cmdInterval")) cmdInterval = lib.getCon().getConfig(pn).getInt("cmdInterval");
			scheduler.scheduleSyncDelayedTask(lib, new ReCanCmd(p), cmdInterval/50);
			if (!result) p.sendMessage(f.f(pn,"fail",get(125)));
			return result;
		}

		private String get(int id) {
			return f.get(pn, id);
		}

		class ReCanCmd implements Runnable {
			Player p;
			public ReCanCmd(Player p) {this.p = p;}
			@Override
			public void run() {
				if (p == null) return;
				if (p.isOnline()) canCmdHash.put(p, true);
				else canCmdHash.remove(p);
			}
		}
	}
}
