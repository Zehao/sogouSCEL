package org.hnote.sogou;

public class Main {

	
	public static void main(String[] args) {
		SCEL dic = new SCEL("C:\\Users\\Administrator\\Desktop\\����ʿ�\\������Ϣ\\����\\�����ر�������������Ϣ��ѡ.scel");
		try{
			dic.parse();
			dic.printWordList();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
