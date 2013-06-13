package lib;

public class Tps implements Runnable{
	//检测间隔,单位是tick,>=1
	static final int CHECK_INTERVAL = 1;
	//更新间隔,单位是秒,>=1
	static final int UPDATE_INTERVAL = 10;
	long start;
	int ticks;
	static double tps;
	
	/**
	 * 构建并开始运行同步检测tps线程
	 * @param lib
	 */
	public Tps(Lib lib) {
		tps = -1;
		lib.getServer().getScheduler().scheduleSyncRepeatingTask(lib, this, CHECK_INTERVAL, CHECK_INTERVAL);
	}

	@Override
	public void run() {
		if (start == 0)	start = System.currentTimeMillis();
		else ticks += CHECK_INTERVAL;
		if (System.currentTimeMillis() - start >= UPDATE_INTERVAL*1000) {
			start = System.currentTimeMillis();
			tps = Util.getDouble((double)ticks/UPDATE_INTERVAL,2);
			if (tps > 20) tps = 20;
			ticks = 0;
		}
	}
	
	/**
	 * 获取当前tps
	 * @return 0-20,-1表示暂无
	 */
	public static double getTps() {
		return tps;
	}
}
