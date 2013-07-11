package org.hnote.sogou;

public class Main {

	
	public static void main(String[] args) {
		SCEL dic = new SCEL("C:\\Users\\Administrator\\Desktop\\分类词库\\城市信息\\澳门\\澳门特别行政区城市信息精选.scel");
		try{
			dic.parse();
			dic.printWordList();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
