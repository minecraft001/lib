package lib;

import java.io.Serializable;

/**
 * �̳���HashList,�����˸��ݻ������ѡȡԪ�صĹ���
 */
public interface ChanceHashList<T extends Object> extends HashList<T>,Iterable<T>,Cloneable,Serializable{
	/**
	 * �����������Ԫ��,������Ĭ�ϻ���1
	 * @param o Ԫ��
	 * @return �ɹ�����true,����Ѿ��д�Ԫ���򷵻�false
	 */
	public boolean add(T o);
	
	/**
	 * ��ָ��λ������Ԫ��,������Ĭ�ϻ���1
	 * @param o Ԫ��
	 * @param index λ��,[0,size()]
	 * @return �ɹ�����true,����Ѿ��д�Ԫ�ط���false
	 */
	public boolean add(T o,int index);
	
	/**
	 * ����
	 * @return ��ͬ���ݵĶ���
	 */
	public ChanceHashList<T> clone();
	
	/**
	 * �����������Ԫ��,�����Ի���
	 * @param o Ԫ��
	 * @param chance ����,>=0
	 * @return �ɹ�����true,����Ѿ��д�Ԫ���򷵻�false
	 */
	public boolean addChance(T o,int chance);
	
	/**
	 * ��ָ��λ������Ԫ��,�����Ի���
	 * @param o Ԫ��
	 * @param index λ��,[0,size()]
	 * @param chance ����,>=0
	 * @return �ɹ�����true,����Ѿ��д�Ԫ�ط���false
	 */
	public boolean addChance(T o, int index, int chance);
	
	/**
	 * ����ָ��λ��Ԫ�صĻ���
	 * @param index ָ����λ��
	 * @param chance �µĻ���
	 */
	public void setChance(int index, int chance);

	/**
	 * ����ָ��Ԫ�صĻ���
	 * @param o ָ����Ԫ��
	 * @param chance �µĻ���
	 * @return �ɹ�����true,ʧ�ܷ���false
	 */
	public boolean setChance(T o, int chance);

	/**
	 * ��ȡָ��λ��Ԫ�صĻ���
	 * @param index λ��,��0��ʼ
	 * @return ָ��λ��Ԫ�صĻ���
	 */
	public int getChance(int index);
	
	/**
	 * ��ȡָ��Ԫ�صĻ���
	 * @param o ָ����Ԫ��
	 * @return ָ��Ԫ�صĻ���,û�з���-1
	 */
	public int getChance(T o);
	
	/**
	 * ���ݻ������ѡ��һ��Ԫ��
	 * @return ���ѡ���Ԫ��
	 */
	public T getRandom();
}
