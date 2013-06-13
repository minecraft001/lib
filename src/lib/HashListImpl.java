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
	 * �����������Ԫ��
	 * @param o Ԫ��
	 * @return �ɹ�����true,����Ѿ��д�Ԫ���򷵻�false
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
	 * ��ָ��λ������Ԫ��
	 * @param o Ԫ��
	 * @param index λ��,[0,size()]
	 * @return �ɹ�����true,����Ѿ��д�Ԫ�ط���false
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
	 * ��ȡָ����Ԫ��
	 * @param index λ��,��0��ʼ
	 * @return ָ��λ�õ�Ԫ��
	 */
	@Override
	public T get(int index) {
		return list.get(index);
	}
	
	/**
	 * ɾ��Ԫ��
	 * @param o Ҫɾ����Ԫ��
	 * @return �ɹ�����true,���򷵻�false
	 */
	@Override
	public boolean remove(T o) {
		if (!hash.containsKey(o)) return false;
		hash.remove(o);
		list.remove(list.indexOf(o));
		return true;
	}
	
	/**
	 * ɾ��ָ��λ�õ�Ԫ��
	 * @param index λ��,[0,size()-1]
	 * @return �Ƴ���Ԫ��
	 */
	@Override
	public T remove(int index) {
		T o = list.get(index);
		hash.remove(o);
		list.remove(index);
		return o;
	}
	
	/**
	 * ���Ԫ��
	 */
	@Override
	public void clear() {
		hash.clear();
		list.clear();
	}
	
	/**
	 * ����ָ��Ԫ�ص�λ��
	 * @param o Ԫ��
	 * @return λ��,��0��ʼ,û�з���-1
	 */
	@Override
	public int indexOf(T o) {
		if (o == null) throw new NullPointerException();
		return list.indexOf(o);
	}
	
	/**
	 * ����Ƿ���ָ��Ԫ��
	 * @param o ����Ԫ��
	 * @return ��������true,���򷵻�false
	 */
	@Override
	public boolean has(T o) {
		if (o == null) throw new NullPointerException();
		return hash.containsKey(o);
	}

	/**
	 * Ԫ������
	 * @return Ԫ������,��СΪ0
	 */
	@Override
	public int size() {
		return list.size();
	}

	/**
	 * �õ�ָ����ҳ
	 * @param page ָ����ҳ��,[1,getMaxPage(pageSize)]
	 * @param pageSize ҳ��(��ҳ)��С
	 * @return ָ��ҳ���ڵ�Ԫ���б�
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
	 * �õ����ҳ����
	 * @param pageSize ҳ��(��ҳ)��С
	 * @return ҳ����,0��ʾ��Ԫ��,��Ԫ������Сҳ����Ϊ1
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
