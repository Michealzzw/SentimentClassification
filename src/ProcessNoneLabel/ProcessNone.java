package ProcessNoneLabel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import TextProcessTool.TPT;

public class ProcessNone {
	static String Feature_path = "NFeature.txt";
	static SentiWordNetDemoCode sentiwordnet;
	static String FeatureTest_path =  "NFeature_test.txt";
	static String Stoplist_path ="stopword-list.txt";
	public static HashMap<String,Object> StopList = new HashMap<String,Object>();
	public static HashMap<String,HashMap<String,Integer>> WordList = new HashMap<String,HashMap<String,Integer>>();
	public static int readFeatureByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		int line = 0;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			tempString = null;
			line = 0;
			while ((tempString = reader.readLine()) != null) {
				String[] list = tempString.split("\t");
				String topic = list[0];
				line = Integer.parseInt(list[1]);
				HashMap<String,	Integer> wl = new HashMap<String,Integer>();
				WordList.put(topic, wl);
				for (int i = 0; i < line; i++) {
					tempString = reader.readLine();
					list = tempString.split("\t");
					if (list[1].equals("NONE")) continue;
					for (int j = 3;j<list.length;j+=3)
					{
						if (list[j].length()>2)
						{
							String tmp = TPT.Stemming(list[j]);
							if (wl.containsKey(tmp)) wl.put(tmp, wl.get(tmp)+1);
							else wl.put(tmp, 1);
						}
					}
				}
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
	public static int readStopByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		int line = 0;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			tempString = null;
			line = 0;
			while ((tempString = reader.readLine()) != null) {
				StopList.put(TPT.Stemming(tempString), null);
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
	public static int readFeatureTestByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		int line = 0;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			tempString = null;
			line = 0;
			FileWriter fw = new FileWriter("NoneLabelAns.txt");
			while ((tempString = reader.readLine()) != null) {
				String[] list = tempString.split("\t");
				String topic = list[0];
				HashMap<String,	Integer> wl = WordList.get(topic);
				line = Integer.parseInt(list[1]);				
				for (int i = 0; i < line; i++) {
					tempString = reader.readLine();
					list = tempString.split("\t");
					boolean flag = true;				
					//if (!list[1].equals("NONE")) continue;
					int count = 0;
					for (int j = 3;j<list.length;j+=3)
					{
						if (list[j].length()>2)
						{
							String tmp = TPT.Stemming(list[j]);
							if (!StopList.containsKey(tmp)&&wl.containsKey(tmp)&&((sentiwordnet.extract(list[j], "a")==null)||Math.abs(sentiwordnet.extract(list[j], "a"))<0.2)) 
								{ count+=wl.get(tmp);}
						}
					}
					if (count<1) 
						fw.write(list[0]+" "+list[1]+"\n");
				}
			}
			reader.close();
			fw.close();
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
	public static void main(String[] args) throws IOException {
		sentiwordnet = new SentiWordNetDemoCode("SentiWordNet_3.0.0_20130122.txt");
		System.out.println("dafsasdf#a "+sentiwordnet.extract("feminists", "a"));
		System.out.println("bad#a "+sentiwordnet.extract("bad", "a"));
		System.out.println("blue#a "+sentiwordnet.extract("blue", "a"));
		System.out.println("blue#n "+sentiwordnet.extract("blue", "n"));
		readFeatureByLines(Feature_path);
		readStopByLines(Stoplist_path);
		readFeatureTestByLines(FeatureTest_path);
	}
}
