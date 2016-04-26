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

public class WordList {
	static String filename = "semeval2016-task6-trainingdata.txt";
	static String WordList_path = "WordList.txt";
	static HashMap<String,Vector<Integer>> Topic_ID = new HashMap<String,Vector<Integer>>(); 
	static HashMap<Integer,String> ID_Dispreposition = new HashMap<Integer,String>();
	static HashMap<Integer,String> ID_Twitter = new HashMap<Integer,String>();
	static HashMap<String,HashMap<String,Integer>> Topic_WordList = new HashMap<String,HashMap<String,Integer>>();
	// remove all @...
	// replace all unalphabet by " "
	public static int readFileIdByLines(String fileName) {
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

	public static void main(String[] args)
	{
		readFileIdByLines(filename);
		
		try {
			FileWriter fw = new FileWriter(WordList_path);
			Iterator iter = Topic_ID.entrySet().iterator();
			while (iter.hasNext()) {
				HashMap.Entry entry = (HashMap.Entry) iter.next();
				String topic = (String) entry.getKey();
				Vector<Integer> allID = (Vector<Integer>) entry.getValue();
				HashMap<String,Integer> WordList = new HashMap<String,Integer>();
				Topic_WordList.put(topic,WordList);		
				for (int i = 0;i<allID.size();i++)
				{
					String twitter = ID_Twitter.get(allID.elementAt(i));
					//Remove all @username
					Vector<String> words = TPT.Sentense2Words(TPT.RemoveAtUsername(twitter));
					for (int i1 = 0;i1<words.size();i1++)
					{
						String word = words.elementAt(i1);
						if (!WordList.containsKey(word))
							WordList.put(word, 0);
						WordList.put(word,WordList.get(word)+1);					
					}				
				}
				fw.write(topic+"\t"+WordList.size()+"\n");
				Iterator witer = WordList.entrySet().iterator();
				while (witer.hasNext()) {
					HashMap.Entry wentry = (HashMap.Entry) witer.next();
					String word = (String) wentry.getKey();
					int value = (int) wentry.getValue();
					fw.write(word+"\t"+value+"\n");
				}				
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
