package lib;

import java.util.Random;

public class ChanceHashListImpl<T extends Object> extends HashListImpl<T> implements ChanceHashList<T>{
	private static final long serialVersionUID = 1L;
	private static final Random RANDOM = new Random();
	private int totalChance;
	
	@Override
	public boolean remove(T o) {
		if (o == null) throw new NullPointerException();
		if (!hash.containsKey(o)) return false;
		totalChance -= hash.get(o);
		hash.remove(o);
		list.remove(o);
		return true;
	}

	@Override
	public T remove(int index) {
		T o = list.get(index);
		totalChance -= hash.get(o);
		hash.remove(o);
		list.remove(o);
		return o;
	}

	@Override
	public void clear() {
		super.clear();
		totalChance = 0;
	}

	@Override
	public ChanceHashList<T> clone() {
		ChanceHashListImpl<T> hash = new ChanceHashListImpl<T>();
		hash.hash = this.hash;
		hash.list = list;
		hash.totalChance = totalChance;
		return hash;
	}

	@Override
	public boolean add(T o) {
		return addChance(o, 1);
	}

	@Override
	public boolean add(T o, int index) {
		return addChance(o, index, 1);
	}

	@Override
	public boolean addChance(T o, int chance) {
		if (super.add(o)) {
			hash.put(o, chance);
			totalChance += chance;
			return true;
		}else return false;
	}

	@Override
	public boolean addChance(T o, int index, int chance) {
		if (super.add(o,index)) {
			hash.put(o, chance);
			totalChance += chance;
			return true;
		}else return false;
	}

	@Override
	public void setChance(int index, int chance) {
		T o = list.get(index);
		totalChance += (chance-hash.get(o));
		hash.put(o, chance);
	}

	@Override
	public boolean setChance(T o, int chance) {
		if (hash.containsKey(o)) {
			totalChance += (chance-hash.get(o));
			hash.put(o, chance);
			return true;
		}else return false;
	}

	@Override
	public int getChance(int index) {
		return hash.get(list.get(index));
	}

	@Override
	public int getChance(T o) {
		if (hash.containsKey(o)) return hash.get(o);
		else return -1;
	}

	@Override
	public T getRandom() {
		int select = RANDOM.nextInt(totalChance);
		for (int i=0;i<list.size();i++) {
			select -= hash.get(list.get(i));
			if (select < 0) return list.get(i);
		}
		return list.get(list.size()-1);
	}
}
