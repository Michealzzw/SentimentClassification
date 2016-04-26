package ClassificationAsSimilarity;

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

import PosterStemmer.Stemmer;
import TextProcessTool.TPT;

public class Twitter2Feature2Test {
	static String filename = "SemEval2016-Task6-subtaskA-testdata-gold.txt";
	static String WordList_path = "WordList.txt";
	static String Feature_path = "Feature_SVM/test_Feature_SVM";
	static HashMap<String,Vector<Integer>> Topic_ID = new HashMap<String,Vector<Integer>>(); 
	static HashMap<Integer,String> ID_Dispreposition = new HashMap<Integer,String>();
	static HashMap<Integer,String> ID_Twitter = new HashMap<Integer,String>();
	static HashMap<String,HashMap<String,Integer>> Topic_WordList = new HashMap<String,HashMap<String,Integer>>();
	static HashMap<String,HashMap<String,Integer>> Topic_WordListID = new HashMap<String,HashMap<String,Integer>>();
	// remove all @...
	// replace all unalphabet by " "
	public static int readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        int line = 0;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            tempString = reader.readLine();
            line = 0;
            while ((tempString = reader.readLine()) != null) {
            	String[] list = tempString.split("\t");
            	int id = Integer.parseInt(list[0]);
            	ID_Twitter.put(id, list[2]);
            	ID_Dispreposition.put(id, list[3]);
            	if (!Topic_ID.containsKey(list[1]))
            		Topic_ID.put(list[1], new Vector<Integer>());
            	Topic_ID.get(list[1]).add(id);
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
	public static int readWordListByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        int line = 0;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            line = 0;
            while ((tempString = reader.readLine()) != null) {
            	String topic = tempString.split("\t")[0];
            	HashMap<String,Integer>WordList = new HashMap<String,Integer>();
            	Topic_WordList.put(topic, WordList);
            	HashMap<String,Integer>WordListID = new HashMap<String,Integer>();
            	Topic_WordListID.put(topic, WordListID);
            	line = Integer.parseInt(tempString.split("\t")[1]);
            	for (int i = 0;i<line;i++)
            	{
            		tempString = reader.readLine();
            		String[] args = tempString.split("\t");
            		WordList.put(args[0], Integer.parseInt(args[1]));
            		WordListID.put(args[0], i+1);
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
	public static void main(String[] args)
	{
		readFileByLines(filename);
		readWordListByLines(WordList_path);
		try {
			Iterator iter = Topic_ID.entrySet().iterator();
			while (iter.hasNext()) {
				HashMap.Entry entry = (HashMap.Entry) iter.next();
				String topic = (String) entry.getKey();
				Vector<Integer> allID = (Vector<Integer>) entry.getValue();
				HashMap<String,Integer> WordList = Topic_WordList.get(topic);
				HashMap<String,Integer> WordListID = Topic_WordListID.get(topic);
				FileWriter fw = new FileWriter(Feature_path+'_'+topic.replaceAll(" ", "")+".txt");
				//fw.write(topic+"\t"+allID.size()+"\n");
				Vector<Integer> use4sortInt = new Vector<Integer>();
				Vector<Double> use4sortDouble = new Vector<Double>();
				int posi = 0;
				int nega = 0;
				for (int i = 0;i<allID.size();i++)
				{					
					String twitter = ID_Twitter.get(allID.elementAt(i));
					//Remove all @username
					//HashMap<String,Integer> words = TPT.Sentense2WordList(TPT.RemoveAtUsername(twitter));
					HashMap<String,Integer> words = TPT.Sentense2WordList(twitter,true);					
					//fw.write(allID.elementAt(i)+"\t"+ID_Dispreposition.get(allID.elementAt(i))+"\t"+words.size()+"\t");
					
					/*if (ID_Dispreposition.get(allID.elementAt(i)).equals("NONE"))
						continue;
					if (ID_Dispreposition.get(allID.elementAt(i)).equals("FAVOR"))
					{
						fw.write("1 ");
						posi++;
					}
					else 
						{
						fw.write("-1 ");
						nega++;
						}
					*/
					
					/*
					 * if (ID_Dispreposition.get(allID.elementAt(i)).equals("NONE"))
					{
						fw.write("1 ");
						posi++;
					}
					else 
						{
						fw.write("-1 ");
						nega++;
						}
					*/
					
					if (ID_Dispreposition.get(allID.elementAt(i)).equals("FAVOR"))
						fw.write("1 ");
					else
						if (ID_Dispreposition.get(allID.elementAt(i)).equals("AGAINST"))
							fw.write("-1 ");
					else fw.write("0 ");
					
					
					String[] allwords= new String[words.size()*3];
					int mark = 0;
					Iterator witer = words.entrySet().iterator();
					while (witer.hasNext()) {
						HashMap.Entry wentry = (HashMap.Entry) witer.next();
						String word = (String) wentry.getKey();
						int TF = (int) wentry.getValue();
						int IDF = 1;
						if (WordList.containsKey(word)) IDF = WordList.get(word)+1;
						//fw.write(word+"\t"+TF+"\t"+IDF+"\t");
						allwords[mark] = word;
						allwords[mark+1] = String.valueOf(TF);
						allwords[mark+2] = String.valueOf(IDF);
						mark+=3;						
					}
					HashMap<String, Double> now = TPT.StringProcess(allwords, (double) allID.size(), 0);
					TPT.FeatureNormalization(now);
					mark = 0;
					witer = now.entrySet().iterator();
					while (witer.hasNext()) {
						HashMap.Entry wentry = (HashMap.Entry) witer.next();
						String word = (String) wentry.getKey();
						double value = (double) wentry.getValue();
						if (WordListID.containsKey(word))
						{
						int id = WordListID.get(word);
					//	fw.write(id+":"+value+"\t");
						use4sortInt.add(mark, id);
						use4sortDouble.add(mark, value);
						mark++;
						}
					}
					for (int ii = 1;ii<mark;ii++)
						for (int jj = 0;jj<ii;jj++)
						{
							if (use4sortInt.elementAt(ii)<use4sortInt.elementAt(jj))
							{
								int tmp = use4sortInt.elementAt(ii);
								use4sortInt.set(ii, use4sortInt.elementAt(jj));
								use4sortInt.set(jj, tmp);
								double temp = use4sortDouble.elementAt(ii);
								use4sortDouble.set(ii, use4sortDouble.elementAt(jj));
								use4sortDouble.set(jj, temp);
							}
						}
					for (int ii = 0;ii<mark;ii++)
						fw.write(use4sortInt.elementAt(ii)+":"+use4sortDouble.elementAt(ii)+"\t");
					fw.write("\n");
				}
				fw.close();
				System.out.println(topic+" posi:"+posi+" nega:"+nega);
			}			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
