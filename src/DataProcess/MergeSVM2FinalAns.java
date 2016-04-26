package DataProcess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import TextProcessTool.TPT;

public class MergeSVM2FinalAns {
	static String bin = "../";
	static String path = bin+"libsvm-3.21/";
	static String ID_path = bin+"ans_ID.txt";
	static String test_path = bin+"SemEval2016-Task6-subtaskA-testdata-gold.txt";
	static String NoneAnsPath = bin+"NoneLabelAns.txt";
	static HashMap<String, String> ID2Ans = new HashMap<String, String>();
	static HashMap<String, FileWriter> Topic_Fw = new HashMap<String, FileWriter>();
	static HashMap<String,Object> None_Ans = new HashMap<String, Object>();
	static HashMap<String, FileWriter> Topic_Fw_Gold = new HashMap<String, FileWriter>();
	static Vector <String> ID = new Vector<String>();
	static int nowline = 0;
	public static int readAnsByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		int line = 0;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			tempString = null;
			line = 0;
			while ((tempString = reader.readLine()) != null) {
				if (Integer.parseInt(tempString)==1)
					ID2Ans.put(ID.elementAt(nowline), "FAVOR");
				else
					if (Integer.parseInt(tempString)==0)
						ID2Ans.put(ID.elementAt(nowline), "NONE");
					else
						if (Integer.parseInt(tempString)==-1)
							ID2Ans.put(ID.elementAt(nowline), "AGAINST");
				nowline++;
			}
			reader.close();			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return line;
	}
	public static int readIDByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		int line = 0;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			tempString = null;
			line = 0;
			while ((tempString = reader.readLine()) != null) {
				ID.addElement(tempString);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return line;
	}
	public static int readNoneByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		int line = 0;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			tempString = null;
			line = 0;
			while ((tempString = reader.readLine()) != null) {
				None_Ans.put(tempString.split(" ")[0], null);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return line;
	}
	public static int readtestByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		int line = 0;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			tempString = null;
			line = 0;
			FileWriter fw = new FileWriter(bin+"FinalAns.txt");
			tempString = reader.readLine();
			String firstline = tempString;
			fw.write(tempString+"\n");
			while ((tempString = reader.readLine()) != null) {
				String topic = tempString.split("\t")[1];
				if (!Topic_Fw.containsKey(topic))
				{
					Topic_Fw.put(topic, new FileWriter(bin+topic.replaceAll(" ", "_")+".txt"));
					Topic_Fw_Gold.put(topic, new FileWriter(bin+topic.replaceAll(" ", "_")+"_gold.txt"));
					Topic_Fw.get(topic).write(firstline+"\n");
					Topic_Fw_Gold.get(topic).write(firstline+"\n");
				}
				Topic_Fw_Gold.get(topic).write(tempString+"\n");
				if (None_Ans.containsKey(tempString.split("\t")[0]))
					tempString = tempString.replaceAll("(UNKNOWN)|(AGAINST)|(FAVOR)|(NONE)", "NONE");
				else
					tempString = tempString.replaceAll("(UNKNOWN)|(AGAINST)|(FAVOR)|(NONE)", ID2Ans.get(tempString.split("\t")[0]));
				fw.write(tempString+"\n");
				Topic_Fw.get(topic).write(tempString+"\n");
			}
			reader.close();
			fw.close();
			Iterator iter = Topic_Fw.entrySet().iterator();
			while (iter.hasNext()) {
				HashMap.Entry entry = (HashMap.Entry) iter.next();
				Topic_Fw.get(entry.getKey()).close();
				Topic_Fw_Gold.get(entry.getKey()).close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		
		return line;
	}
	static public void main(String[] args)
	{
		readNoneByLines(NoneAnsPath);
		readIDByLines(ID_path);
		for (int i = 1;i<=5;i++)
			readAnsByLines(path+i+".ans");
		readtestByLines(test_path);
	}
}
