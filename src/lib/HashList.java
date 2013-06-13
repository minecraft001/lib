package lib;

import java.io.Serializable;
import java.util.List;

/**
 * 一种列表,特点有:检测的<b>高效性</b>,列表的<b>有序性</b>,集合的<b>唯一性</b>.但存储空间占用更多.
 * <br>为适合mc的状况而创建出来的
 */
public interface HashList<T extends Object> extends Iterable<T>,Cloneable,Serializable{
	/**
	 * 在最后面增加元素
	 * @param o 元素
	 * @return 成功返回true,如果已经有此元素则返回false
	 */
	public boolean add(T o);
	
	/**
	 * 在指定位置增加元素
	 * @param o 元素
	 * @param index 位置,[0,size()]
	 * @return 成功返回true,如果已经有此元素返回false
	 */
	public boolean add(T o,int index);
	
	/**
	 * 获取指定的元素
	 * @param index 位置,从0开始
	 * @return 指定位置的元素
	 */
	public T get(int index);
	
	/**
	 * 删除元素
	 * @param o 要删除的元素
	 * @return 成功返回true,否则返回false
	 */
	public boolean remove(T o);
	
	/**
	 * 删除指定位置的元素
	 * @param index 位置,[0,size()-1]
	 * @return 移除的元素
	 */
	public T remove(int index);
	
	/**
	 * 清空元素
	 */
	public void clear();
	
	/**
	 * 返回指定元素的位置
	 * @param o 元素
	 * @return 位置,从0开始,没有返回-1
	 */
	public int indexOf(T o);
	
	/**
	 * 检测是否含有指定元素
	 * @param o 检测的元素
	 * @return 包含返回true,否则返回false
	 */
	public boolean has(T o);
	
	/**
	 * 元素数量
	 * @return 元素数量,最小为0
	 */
	public int size();
	
	/**
	 * 得到指定的页
	 * @param page 指定的页面,[1,getMaxPage(pageSize)]
	 * @param pageSize 页面(分页)大小
	 * @return 指定页面内的元素列表
	 */
	public List<T> getPage(int page,int pageSize);
	
	/**
	 * 得到最大页面数
	 * @param pageSize 页面(分页)大小
	 * @return 页面数,0表示无元素,有元素则最小页面数为1
	 */
	public int getMaxPage(int pageSize);
	
	/**
	 * 复制
	 * @return 相同内容的对象
	 */
	public HashList<T> clone();
}
