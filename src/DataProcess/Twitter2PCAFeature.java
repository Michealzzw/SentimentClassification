package DataProcess;

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

public class Twitter2PCAFeature {
	static String filename2 = "SemEval2016-Task6-subtaskA-testdata-gold.txt";
	static String filename = "semeval2016-task6-trainingdata.txt";
	static String WordList_path = "WordList.txt";
	static String Feature_path = "PCA_Feature.txt";
	static HashMap<String,Vector<Integer>> Topic_ID = new HashMap<String,Vector<Integer>>(); 
	static HashMap<Integer,String> ID_Dispreposition = new HashMap<Integer,String>();
	static HashMap<Integer,String> ID_Twitter = new HashMap<Integer,String>();
	static HashMap<String,HashMap<String,Integer>> Topic_WordList = new HashMap<String,HashMap<String,Integer>>();
	static HashMap<String,HashMap<String,Integer>> Topic_WordListID = new HashMap<String,HashMap<String,Integer>>();
	// remove all @...
	// replace all unalphabet by " "
	public static int readFileByLines(String fileName,boolean flag) {
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
            	if (list[3].equals("NONE")&&flag) continue;
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
            	line = Integer.parseInt(tempString.split("\t")[1]);
            	HashMap<String,Integer>WordListID = new HashMap<String,Integer>();
            	Topic_WordListID.put(topic, WordListID);
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
		HashMap<String,Integer> topic_modelNum = new HashMap<String,Integer>();
		readFileByLines(filename,true);
		Iterator iter2 = Topic_ID.entrySet().iterator();
		while (iter2.hasNext()) {
			
			HashMap.Entry entry = (HashMap.Entry) iter2.next();
			String topic = (String) entry.getKey();
			topic_modelNum.put(topic, ((Vector<Integer>) entry.getValue()).size());
		}
		readFileByLines(filename2,false);
		readWordListByLines(WordList_path);
		try {
			FileWriter fw = new FileWriter(Feature_path);
			Iterator iter = Topic_ID.entrySet().iterator();
			while (iter.hasNext()) {
				
				HashMap.Entry entry = (HashMap.Entry) iter.next();
				String topic = (String) entry.getKey();
				System.out.println(topic);
				Vector<Integer> allID = (Vector<Integer>) entry.getValue();
				fw.write(allID.size()+" "+topic_modelNum.get(topic)+"\n");
				HashMap<String,Integer> WordList = Topic_WordList.get(topic);
				HashMap<String,Integer> WordListID = Topic_WordListID.get(topic);
				//fw.write(topic+"\t"+allID.size()+"\n");
				for (int i = 0;i<allID.size();i++)
				{					
					String twitter = ID_Twitter.get(allID.elementAt(i));
					String label = ID_Dispreposition.get(allID.elementAt(i));
					if (label.equals("FAVOR")) fw.write("1 ");
					else
						if (label.equals("AGAINST")) fw.write("-1 ");
						else	fw.write("0 ");
					//Remove all @username
					//HashMap<String,Integer> words = TPT.Sentense2WordList(TPT.RemoveAtUsername(twitter));
					HashMap<String,Integer> words = TPT.Sentense2WordList(twitter,true);
					Iterator witer = words.entrySet().iterator();
					//fw.write(allID.elementAt(i)+"\t"+ID_Dispreposition.get(allID.elementAt(i))+"\t"+words.size()+"\t");
					while (witer.hasNext()) {
						HashMap.Entry wentry = (HashMap.Entry) witer.next();
						String word = (String) wentry.getKey();
						int TF = (int) wentry.getValue();
						int IDF = 1;
						if (WordList.containsKey(word)) IDF = WordList.get(word)+1; else continue;
						fw.write(WordListID.get(word)+" "+TPT.TFIDFcalculate(TF, IDF, allID.size())+" ");
					}
					fw.write("\n");
				}				
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
