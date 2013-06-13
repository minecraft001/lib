package lib;

public class Tps implements Runnable{
	//�����,��λ��tick,>=1
	static final int CHECK_INTERVAL = 1;
	//���¼��,��λ����,>=1
	static final int UPDATE_INTERVAL = 10;
	long start;
	int ticks;
	static double tps;
	
	/**
	 * ��������ʼ����ͬ�����tps�߳�
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
	 * ��ȡ��ǰtps
	 * @return 0-20,-1��ʾ����
	 */
	public static double getTps() {
		return tps;
	}
}
