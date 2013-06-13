package lib;

import java.io.Serializable;
import java.util.List;

/**
 * һ���б�,�ص���:����<b>��Ч��</b>,�б��<b>������</b>,���ϵ�<b>Ψһ��</b>.���洢�ռ�ռ�ø���.
 * <br>Ϊ�ʺ�mc��״��������������
 */
public interface HashList<T extends Object> extends Iterable<T>,Cloneable,Serializable{
	/**
	 * �����������Ԫ��
	 * @param o Ԫ��
	 * @return �ɹ�����true,����Ѿ��д�Ԫ���򷵻�false
	 */
	public boolean add(T o);
	
	/**
	 * ��ָ��λ������Ԫ��
	 * @param o Ԫ��
	 * @param index λ��,[0,size()]
	 * @return �ɹ�����true,����Ѿ��д�Ԫ�ط���false
	 */
	public boolean add(T o,int index);
	
	/**
	 * ��ȡָ����Ԫ��
	 * @param index λ��,��0��ʼ
	 * @return ָ��λ�õ�Ԫ��
	 */
	public T get(int index);
	
	/**
	 * ɾ��Ԫ��
	 * @param o Ҫɾ����Ԫ��
	 * @return �ɹ�����true,���򷵻�false
	 */
	public boolean remove(T o);
	
	/**
	 * ɾ��ָ��λ�õ�Ԫ��
	 * @param index λ��,[0,size()-1]
	 * @return �Ƴ���Ԫ��
	 */
	public T remove(int index);
	
	/**
	 * ���Ԫ��
	 */
	public void clear();
	
	/**
	 * ����ָ��Ԫ�ص�λ��
	 * @param o Ԫ��
	 * @return λ��,��0��ʼ,û�з���-1
	 */
	public int indexOf(T o);
	
	/**
	 * ����Ƿ���ָ��Ԫ��
	 * @param o ����Ԫ��
	 * @return ��������true,���򷵻�false
	 */
	public boolean has(T o);
	
	/**
	 * Ԫ������
	 * @return Ԫ������,��СΪ0
	 */
	public int size();
	
	/**
	 * �õ�ָ����ҳ
	 * @param page ָ����ҳ��,[1,getMaxPage(pageSize)]
	 * @param pageSize ҳ��(��ҳ)��С
	 * @return ָ��ҳ���ڵ�Ԫ���б�
	 */
	public List<T> getPage(int page,int pageSize);
	
	/**
	 * �õ����ҳ����
	 * @param pageSize ҳ��(��ҳ)��С
	 * @return ҳ����,0��ʾ��Ԫ��,��Ԫ������Сҳ����Ϊ1
	 */
	public int getMaxPage(int pageSize);
	
	/**
	 * ����
	 * @return ��ͬ���ݵĶ���
	 */
	public HashList<T> clone();
}
