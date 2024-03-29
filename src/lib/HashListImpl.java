package lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class HashListImpl<T extends Object> implements HashList<T>{
	private static final long serialVersionUID = 1L;
	protected HashMap<T,Integer> hash;
	protected List<T> list;
	
	public HashListImpl() {
		hash = new HashMap<T, Integer>();
		list = new LinkedList<T>();
	}
	
	/**
	 * 在最后面增加元素
	 * @param o 元素
	 * @return 成功返回true,如果已经有此元素则返回false
	 */
	@Override
	public boolean add(T o) {
		if (o == null) throw new NullPointerException();
		if (hash.containsKey(o)) return false;
		try {
			hash.put(o, 0);
			list.add(o);
		} catch (Exception e) {
			remove(o);
		}
		return true;
	}

	/**
	 * 在指定位置增加元素
	 * @param o 元素
	 * @param index 位置,[0,size()]
	 * @return 成功返回true,如果已经有此元素返回false
	 */
	@Override
	public boolean add(T o,int index) {
		if (o == null) throw new NullPointerException();
		if (hash.containsKey(o)) return false;
		try {
			list.add(index,o);
			hash.put(o, 0);
		} catch (Exception e) {
			remove(o);
		}
		return true;
	}
	
	/**
	 * 获取指定的元素
	 * @param index 位置,从0开始
	 * @return 指定位置的元素
	 */
	@Override
	public T get(int index) {
		return list.get(index);
	}
	
	/**
	 * 删除元素
	 * @param o 要删除的元素
	 * @return 成功返回true,否则返回false
	 */
	@Override
	public boolean remove(T o) {
		if (!hash.containsKey(o)) return false;
		hash.remove(o);
		list.remove(list.indexOf(o));
		return true;
	}
	
	/**
	 * 删除指定位置的元素
	 * @param index 位置,[0,size()-1]
	 * @return 移除的元素
	 */
	@Override
	public T remove(int index) {
		T o = list.get(index);
		hash.remove(o);
		list.remove(index);
		return o;
	}
	
	/**
	 * 清空元素
	 */
	@Override
	public void clear() {
		hash.clear();
		list.clear();
	}
	
	/**
	 * 返回指定元素的位置
	 * @param o 元素
	 * @return 位置,从0开始,没有返回-1
	 */
	@Override
	public int indexOf(T o) {
		if (o == null) throw new NullPointerException();
		return list.indexOf(o);
	}
	
	/**
	 * 检测是否含有指定元素
	 * @param o 检测的元素
	 * @return 包含返回true,否则返回false
	 */
	@Override
	public boolean has(T o) {
		if (o == null) throw new NullPointerException();
		return hash.containsKey(o);
	}

	/**
	 * 元素数量
	 * @return 元素数量,最小为0
	 */
	@Override
	public int size() {
		return list.size();
	}

	/**
	 * 得到指定的页
	 * @param page 指定的页面,[1,getMaxPage(pageSize)]
	 * @param pageSize 页面(分页)大小
	 * @return 指定页面内的元素列表
	 */
	@Override
	public List<T> getPage(int page,int pageSize) {
		List<T> result = new ArrayList<T>();
		int maxPage = getMaxPage(pageSize);
		if (page >= 1 && page <= maxPage) {
			int begin = (page-1)*pageSize;
			int end = (page == maxPage)?list.size():page*pageSize;
			for (int i=begin;i<end;i++) result.add(list.get(i));
		}
		return result;
	}
	
	/**
	 * 得到最大页面数
	 * @param pageSize 页面(分页)大小
	 * @return 页面数,0表示无元素,有元素则最小页面数为1
	 */
	@Override
	public int getMaxPage(int pageSize) {
		if (pageSize < 0) throw new IllegalArgumentException();
		if (list.size()%pageSize == 0) return list.size()/pageSize;
		return list.size()/pageSize+1;
	}

	@Override
	public Iterator<T> iterator() {
		return list.iterator();
	}

	@Override
	public HashList<T> clone() {
		HashListImpl<T> hash = new HashListImpl<T>();
		hash.hash = this.hash;
		hash.list = list;
		return hash;
	}

	@Override
	public String toString() {
		String result = "";
		for (int i=0;i<list.size();i++) {
			if (i != 0) result += ",";
			result += list.get(i);
		}
		return result;
	}
}
