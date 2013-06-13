package lib;

import org.bukkit.command.CommandSender;

public class Cell {
	private boolean all;
	private CommandSender sender;
	private String msg;
	public Cell(boolean all, CommandSender sender, String msg) {
		this.all = all;
		this.sender = sender;
		this.msg = msg;
	}
	public boolean isAll() {
		return all;
	}
	public CommandSender getSender() {
		return sender;
	}
	public String getMsg() {
		return msg;
	}
}
