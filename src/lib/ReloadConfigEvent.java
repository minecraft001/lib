package lib;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * (����)��ȡ�����ļ���������õ��¼�,���ڸ����ָ�������
 */
public class ReloadConfigEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private String destPath;
	private String callPlugin;
	private FileConfiguration config;
	
	ReloadConfigEvent(String destPath, String callPlugin, FileConfiguration config) {
		this.destPath = destPath;
		this.callPlugin = callPlugin;
		this.config = config;
	}
	
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	public String getDestPath() {
		return destPath;
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public String getCallPlugin() {
		return callPlugin;
	}
	
}