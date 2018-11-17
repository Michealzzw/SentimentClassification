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

public class Twitter2SplitText4LSA {
	static String bin = "";
	static String filename = "semeval2016-task6-trainingdata.txt";
	static String test_path = bin+"SemEval2016-Task6-subtaskA-testdata-gold-origin.txt";
	static HashMap<String, FileWriter> Topic_Fw = new HashMap<String, FileWriter>();
	static int nowline = 0;
	public static int readtestByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		int line = 0;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			tempString = null;
			line = 0;
			tempString = reader.readLine();
			//String firstline = tempString;
			while ((tempString = reader.readLine()) != null) {
				String topic = tempString.split("\t")[1];
				if (!Topic_Fw.containsKey(topic))
				{
					Topic_Fw.put(topic, new FileWriter(bin+topic.replaceAll(" ", "_")+"_LSA.txt"));					
					//Topic_Fw.get(topic).write(firstline+"\n");
				}
				Topic_Fw.get(topic).write(tempString.split("\t")[2]+"\n");
			}
			reader.close();
			reader = new BufferedReader(new FileReader(filename));
			line = 0;
			tempString = reader.readLine();
			//String firstline = tempString;
			while ((tempString = reader.readLine()) != null) {
				String topic = tempString.split("\t")[1];
				if (!Topic_Fw.containsKey(topic))
				{
					Topic_Fw.put(topic, new FileWriter(bin+topic.replaceAll(" ", "_")+"_LSA.txt"));					
					//Topic_Fw.get(topic).write(firstline+"\n");
				}
				Topic_Fw.get(topic).write(tempString.split("\t")[2]+"\n");
			}
			reader.close();
			Iterator iter = Topic_Fw.entrySet().iterator();
			while (iter.hasNext()) {
				HashMap.Entry entry = (HashMap.Entry) iter.next();
				Topic_Fw.get(entry.getKey()).close();
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
		readtestByLines(test_path);
	}
}
