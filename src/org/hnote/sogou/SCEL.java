package org.hnote.sogou;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SCEL {
	File p;
	int wordCount = 0;
	Map<Integer,String> dict = new HashMap<Integer,String>();
	Map<String,LinkedList<String>> wordList = new HashMap<String,LinkedList<String>>();
	
	SCEL(String path){
		p = new File(path);
	}
	
	void printWordListwithPinyin(){
		for(String w:wordList.keySet()){
			System.out.println(w + Arrays.asList(wordList.get(w).toArray()));
		}
	}
	
	void printWordList(){
		for(String w:wordList.keySet())
			System.out.println(w);
	}

	public void parse() throws Exception{
		
		RandomAccessFile raf = new RandomAccessFile(p,"r");
		
        byte[] str = new byte[128];
        int hzPosition = 0;
        raf.read(str, 0, 128); // \x40\x15\x00\x00\x44\x43\x53\x01

        if (str[4] == 0x44)
        {
            hzPosition = 0x2628;
        }
        if (str[4] == 0x45)
        {
            hzPosition = 0x26C4;
        }
        
        //get word count at 0x124
        raf.seek(0x124);
        wordCount = readInt(raf);

        //get pinyin position
        raf.seek(0x1544);
        
        while(true){
        	byte[] num = new byte[4];
        	raf.read(num, 0, 4);
        	int mark = num[0] + num[1]*256;
        	byte[] buff = new byte[20];
        	raf.read(buff,0,num[2]);
        	String py = getString(buff,num[2]);
        	dict.put(mark, py);
        	if(py.equals("zuo")){
        		break;
        	}
        }
        
        //get hanzi position
        raf.seek(hzPosition);
        
        while(true){
        	 byte[] num = new byte[4];
        	 raf.read(num, 0, 4);
        	 int samePYcount = num[0] + num[1]*256;
        	 int count = num[2] + num[3]*256;
        	 
        	 byte[] buff = new byte[256];
        	 for (int i = 0; i < count; i++)
        		 buff[i] = raf.readByte();

        	 List<String> wordPY = new LinkedList<String>();
        	 for (int i = 0; i < count/2; i++)
        	 {
                 int key = buff[i*2] + buff[i*2 + 1]*256;
                 wordPY.add(dict.get(key));
             }
        	 for (int s = 0; s < samePYcount; s++){ //同音词，使用前面相同的拼音
        		 raf.read(num,0,2);
        		 int hzBytecount = num[0] + num[1]*256;
        		 //System.out.println("hzBytecount:" + hzBytecount);
        		 raf.read(buff,0,hzBytecount);
        		 String word = getString(buff,hzBytecount);
        		 //System.out.println(word);
        		 raf.readShort();
        		 raf.readInt();
        		 wordList.put(word, (LinkedList<String>) wordPY);
        		 
        		 for(int i=0; i<6 ;i++){
        			 raf.readByte();
        		 }
        	 }
        	 if(raf.getFilePointer() == raf.length())
        		 break;
        }
        raf.close();
	}
	
	private int readInt(RandomAccessFile raf) throws Exception{
		byte[] buff = new byte[4];
		raf.read(buff, 0, 4);
		return (int)buff[0]& 0xFF + (((int)buff[1]& 0xFF)<<8) + (((int)buff[2]& 0xFF)<<16) | (((int)buff[3]& 0xFF)<<24);
	}
	private String getString(byte[] buff,int num) throws Exception{
		String str = new String(buff,0,num,"UTF-16LE");
		return str;
	}
	

}
