package Para2Vec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import TextProcessTool.TPT;

public class DealVector2SVM {
	public static String[] topic = {"Feminist Movement","Atheism","Hillary Clinton","Legalization of Abortion","Climate Change is a Real Concern"};
	public static HashMap<String,FileWriter> topic_test_fw = new HashMap<String,FileWriter>();
	public static HashMap<String,FileWriter> topic_train_fw = new HashMap<String,FileWriter>();
	public static HashMap<String,Integer> id_pre = new HashMap<String,Integer>();
	public static HashMap<String,String> id_topic = new HashMap<String,String>();
	public static FileWriter fw;
	public static int readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        int line = 0;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            //tempString = reader.readLine();
            line = 0;
            while ((tempString = reader.readLine()) != null) {
            	String[] list = tempString.split("\t");
            	if (list[3].equals("FAVOR")) id_pre.put(list[0], 1);
            	if (list[3].equals("AGAINST")) id_pre.put(list[0], -1);
            	if (list[3].equals("NONE")) id_pre.put(list[0], 0);
            	if (list[3].equals("UNKNOWN")) id_pre.put(list[0], -2);
            	id_topic.put(list[0], list[1]);
            	line++;
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
	public static int readVecFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        int line = 0;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            //tempString = reader.readLine();
            line = 0;
            while ((tempString = reader.readLine()) != null) {
            	String[] list = tempString.split(" ");
            	String tmp = list[0].replaceAll("semeval_", "");
            	if (id_pre.get(tmp)==-2)
            		fw = topic_test_fw.get(id_topic.get(tmp));
            	else fw = topic_train_fw.get(id_topic.get(tmp));
            	if (id_pre.containsKey(tmp)&&id_pre.get(tmp)!=0)
            	{
            		//fw.write(tempString.replaceAll(list[0], id_pre.get(tmp).toString())+"\n");
            		fw.write(id_pre.get(tmp).toString()+" ");
            		for (int i = 1;i<list.length;i++)
            			fw.write(i+":"+list[i]+" ");
            		fw.write("\n");
            	}	
            	line++;
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
	public static String path = "Para2Vec.txt";
	public static String pathtrain = "semeval2016-task6-trainingdata-origin.txt";
	public static String pathtest = "SemEval2016-Task6-subtaskA-testdata.txt";
	public static void main(String[] args) throws IOException
	{
		for (int i = 0;i<topic.length;i++) 
		{
			topic_test_fw.put(topic[i], new FileWriter("ans_SVM_test_"+(i+1)+".txt"));
			topic_train_fw.put(topic[i], new FileWriter("ans_SVM_train_"+(i+1)+".txt"));
		}
		readFileByLines(pathtrain);
		readFileByLines(pathtest);
		readVecFileByLines(path);
		for (int i = 0;i<topic.length;i++) 
		{
			topic_test_fw.get(topic[i]).close();;
			topic_train_fw.get(topic[i]).close();;
		}
	}
}
