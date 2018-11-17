package ProcessNoneLabel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import TextProcessTool.TPT;

public class ProcessNone {
	static String Feature_path = "NFeature.txt";
	static SentiWordNetDemoCode sentiwordnet;
	static String FeatureTest_path =  "NFeature_test.txt";
	static String Stoplist_path ="stopword-list.txt";
	static int IDFnum = 0;
	static HashMap<String,Vector<String>> keyWordList = new HashMap<String,Vector<String>>();
	public static HashMap<String,Object> StopList = new HashMap<String,Object>();
	public static HashMap<String,Integer> IDFList = new HashMap<String,Integer>();
	public static HashMap<String,HashMap<String,Integer>> WordList = new HashMap<String,HashMap<String,Integer>>();
	public static HashMap<String,HashMap<String,Integer>> NoneWordList = new HashMap<String,HashMap<String,Integer>>();
	public static HashMap<String,HashMap<String,Integer>> WikiWordList = new HashMap<String,HashMap<String,Integer>>();
	public static int readWikiFeatureByLines(String fileName,String topic) {
		HashMap<String,Integer> hs = new HashMap<String,Integer>();
		WikiWordList.put(topic, hs);
		Pattern wdpt = Pattern.compile("[0-9a-zA-Z]+");
		File file = new File(fileName);
		BufferedReader reader = null;
		int line = 0;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			tempString = null;
			line = 0;
			while ((tempString = reader.readLine()) != null) {
				Matcher mc = wdpt.matcher(tempString);
				while (mc.find())
				{
					String wd = TPT.Stemming(mc.group().toLowerCase());
					if (!hs.containsKey(wd)) hs.put(wd, 0);
					hs.put(wd, hs.get(wd)+1);
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
				System.out.println(topic);
				HashMap<String,	Integer> nonewl = new HashMap<String,Integer>();
				NoneWordList.put(topic, nonewl);
				for (int i = 0; i < line; i++) {
					tempString = reader.readLine();
					list = tempString.split("\t");
					if (list[1].equals("NONE"))
						for (int j = 3;j<list.length;j+=3)
						{
							if (list[j].length()>2)
							{
								String tmp = TPT.Stemming(list[j]);
								if (nonewl.containsKey(tmp)) nonewl.put(tmp, nonewl.get(tmp)+1);
								else nonewl.put(tmp, 1);
								tmp = list[j];
								if (nonewl.containsKey(tmp)) nonewl.put(tmp, nonewl.get(tmp)+1);
								else nonewl.put(tmp, 1);
							}
						}
					else
					for (int j = 3;j<list.length;j+=3)
					{
						if (list[j].length()>2)
						{
							String tmp = TPT.Stemming(list[j]);
							if (wl.containsKey(tmp)) wl.put(tmp, wl.get(tmp)+1);
							else wl.put(tmp, 1);
							 tmp = (list[j]);
								if (wl.containsKey(tmp)) wl.put(tmp, wl.get(tmp)+1);
								else wl.put(tmp, 1);
							
							if (list[j].length()>5) 
								{
								tmp = list[j].substring(0, 5);
								if (wl.containsKey(tmp)) wl.put(tmp, wl.get(tmp)+1);
								else wl.put(tmp, 1);
							
								}
						}
					}
				}
				readWikiFeatureByLines(topic.replaceAll(" ", "")+"_wiki.txt",topic);
				Vector<String> keyl = new Vector<String>();
				keyWordList.put(topic, keyl);
				Iterator iter = wl.entrySet().iterator();
				while (iter.hasNext()) {
					HashMap.Entry entry = (HashMap.Entry) iter.next();
					String word = (String) entry.getKey();
					int posi = (int)entry.getValue();
					int nega = 0;
					int wiki = 0;
					double IDF = 0;
					if (nonewl.containsKey(word)) nega = nonewl.get(word);
					if (WikiWordList.get(topic).containsKey(word)) wiki = WikiWordList.get(topic).get(word);
					if (IDFList.containsKey(word)) IDF = IDFList.get(word);
					if (IDF!=0) IDF = IDF/IDFnum;
					Double tmp = sentiwordnet.extract(word, "a");
					if (tmp==null)
						tmp = sentiwordnet.extract(word, "n");
					double semti = 0.0;
					if (tmp!=null) semti = Math.abs(tmp);
					//if ((posi>15&&word.length()>2&&IDF<0.001&&!StopList.containsKey(word))||(posi-nega>40&&word.length()>2&&!StopList.containsKey(word))) keyl.addElement(word);
					//if ((posi>15&&word.length()>2&&!StopList.containsKey(word))||(posi-nega>15&&word.length()>2)) keyl.addElement(word);
					if ((posi>15)&&word.length()>2) keyl.addElement(word);
					//if (posi+wiki>20&&IDF<0.001) keyl.addElement(word);
					//if (nega<5&&posi>5&&word.length()>2) keyl.addElement(word);
					//&&wiki>10
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
				StopList.put(tempString, null);
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
	public static int readCollectionByLines(String fileName) {
		File file = new File(fileName);
		Pattern wdpt = Pattern.compile("[0-9a-zA-Z]+");
		BufferedReader reader = null;
		int line = 0;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			tempString = null;
			line = 0;
			while ((tempString = reader.readLine()) != null) {
				Matcher mc = wdpt.matcher(tempString);
				line++;
				HashMap<String,Object> wl = new HashMap<String,Object>();
				while (mc.find())
				{
					String wd = TPT.Stemming(mc.group().toLowerCase());
					if (!wl.containsKey(wd))
					{
						if (!IDFList.containsKey(wd)) IDFList.put(wd, 0);
						IDFList.put(wd, IDFList.get(wd)+1);
						wl.put(wd, null);
					}
					if (wd.length()>5) wd = wd.substring(0, 5);
					if (!wl.containsKey(wd))
					{
						if (!IDFList.containsKey(wd)) IDFList.put(wd, 0);
						IDFList.put(wd, IDFList.get(wd)+1);
						wl.put(wd, null);
					}
				}
			}
			IDFnum = line;
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
				HashMap<String,	Integer> nonewl = NoneWordList.get(topic);
				Vector<String> keyl = keyWordList.get(topic);
				for (int i = 0;i<keyl.size();i++)
					fw.write(keyl.elementAt(i)+" ");
				fw.write("\n");
				line = Integer.parseInt(list[1]);				
				for (int i = 0; i < line; i++) {
					tempString = reader.readLine();
					list = tempString.split("\t");
					boolean flag = true;				
					//if (!list[1].equals("NONE")) continue;
					int count = 0;
					int mark = 0;
					int mark2 = 0;
					double sentisum = 0.0;
					for (int j = 3;j<list.length;j+=3)
					{
						if (list[j].length()>2)
						{
							for (int k = 0;k<keyl.size();k++)
							{
								if (list[j].equals(keyl.elementAt(k)))
									//if (list[j].indexOf(keyl.elementAt(k))==0)
									{
										mark = k;
										mark2 = j;
										count++;
									}
								Double tmp = sentiwordnet.extract(list[j], "a");
								if (tmp==null)
									tmp = sentiwordnet.extract(list[j], "n");
								if (tmp!=null)
									sentisum+=Math.abs(tmp);
							}
						}
					}
					if ((sentisum<2&&count<2)||count<1)
					//if (sentisum<2&&count<1)
					{
						//if (!list[1].equals("NONE"))
							//fw.write(topic+" "+tempString+"\n");
						//if (list[1].equals("NONE"))
						
							fw.write(list[0]+" "+list[1]+"\n");
						//else
							//fw.write(topic+" "+tempString+"\n");
					}
					else
					{
						//if (list[1].equals("NONE"))
							//fw.write(topic+" "+tempString+"\n");
					}
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
		//readCollectionByLines("wiki-abstract.txt");
		sentiwordnet = new SentiWordNetDemoCode("SentiWordNet_3.0.0_20130122.txt");
		System.out.println("dafsasdf#a "+sentiwordnet.extract("feminists", "n"));
		System.out.println("bad#a "+sentiwordnet.extract("bad", "a"));
		System.out.println("blue#a "+sentiwordnet.extract("blue", "a"));
		System.out.println("blue#n "+sentiwordnet.extract("blue", "n"));
		readStopByLines(Stoplist_path);
		readFeatureByLines(Feature_path);		
		readFeatureTestByLines(FeatureTest_path);
	}
}
