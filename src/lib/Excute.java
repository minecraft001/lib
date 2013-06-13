package lib;

import java.util.ArrayList;
import java.util.List;


import org.bukkit.Server;
import org.bukkit.entity.Player;


/**
 * cb同步显示线程,用来作中介防止异步线程出问题
 */
public class Excute implements Runnable{
	List<Cell> list;
	Server server;
	String pn;
	
	public Excute(Lib lib) {
		server = lib.getServer();
		pn = lib.getPn();
		list = new ArrayList<Cell>();
	}
	
	@Override
	public void run() {
		Cell cell;
		while (list.size() > 0) {
			cell = list.get(0);
			if (cell != null && cell.getMsg() != null) {
				if (cell.isAll()) {
					server.broadcastMessage(cell.getMsg());
				}else {
					if (cell.getSender() != null) {
						if (cell.getSender() instanceof Player) cell.getSender().sendMessage(cell.getMsg());
						else cell.getSender().sendMessage(cell.getMsg());
					}
				}
			}
			list.remove(0);
		}
	}
	
	public void add(Cell cell) {
		list.add(cell);
	}
}
