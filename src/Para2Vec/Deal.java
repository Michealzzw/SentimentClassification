package Para2Vec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import TextProcessTool.TPT;

public class Deal {
	public static Pattern wordPa = Pattern.compile("[a-zA-Z0-9]+");
	public static Pattern UnwordPa = Pattern.compile("[^a-zA-Z0-9]");
	public static String pathwiki = "wiki-abstract.txt";
	public static String pathtrain = "semeval2016-task6-trainingdata.txt";
	public static String pathtest = "SemEval2016-Task6-subtaskA-testdata-gold.txt";
	public static FileWriter fw;
	public static int readwikiFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        int line = 0;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            //tempString = reader.readLine();
            line = 0;
            while ((tempString = reader.readLine()) != null) {
            	tempString = tempString.replaceAll("(<abstract>|</abstract>)", "");
            	int len = tempString.length();
            	char[] str = new char[len*3];
            	int mark = 0;
            	for (int i = 0;i<len;i++)
            	{
            		String tmp = tempString.substring(i,i+1);
            		if (tmp.matches("[^a-zA-Z0-9]"))
	            	{
            			str[mark++] = ' ';
                		str[mark++] = tempString.charAt(i);
                		str[mark++] = ' ';
	            	}
            		else
            			str[mark++] = tmp.toLowerCase().charAt(0);
            	}
            	tempString = new String(str,0,mark).replaceAll("[ ]+", " ");
            	fw.write("wiki_"+line+" ");
            	String[] wordlist = tempString.split(" ");
            	for (int i = 0;i<wordlist.length;i++)
            	{
            		fw.write(TPT.Stemming(wordlist[i])+" ");
            	}
            	fw.write("\n");
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
	public static int readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        int line = 0;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            //tempString = reader.readLine();
            line = 0;
            tempString = reader.readLine();
            while ((tempString = reader.readLine()) != null) {
            	int No = Integer.parseInt(tempString.split("\t")[0]);
            	//
            	tempString = tempString.split("\t")[1]+" "+tempString.split("\t")[2];
            	int len = tempString.length();
            	char[] str = new char[len*3];
            	int mark = 0;
            	for (int i = 0;i<len;i++)
            	{
            		String tmp = tempString.substring(i,i+1);
            		if (tmp.matches("[^a-zA-Z0-9]"))
	            	{
            			str[mark++] = ' ';
                		str[mark++] = tempString.charAt(i);
                		str[mark++] = ' ';
	            	}
            		else
            			str[mark++] = tmp.toLowerCase().charAt(0);
            	}
            	tempString = new String(str,0,mark).replaceAll("[ ]+", " ");
            	String[] wordlist = tempString.split(" ");
//            	System.out.println(No);
            	fw.write("semeval_"+No+" ");
            	for (int i = 0;i<wordlist.length;i++)
            	{
            		fw.write(TPT.Stemming(wordlist[i])+" ");
            	}
            	fw.write("\n");            	
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
	public static void main(String[] args) throws IOException
	{
		fw = new FileWriter("Para2Vec.txt");
		readFileByLines(pathtrain);
		readFileByLines(pathtest);
		readwikiFileByLines(pathwiki);
		fw.close();
	}
}
