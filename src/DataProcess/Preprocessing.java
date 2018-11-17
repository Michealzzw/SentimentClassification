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

public class Preprocessing {
	static String filename = "semeval2016-task6-trainingdata.txt";
	static String filename2 = "SemEval2016-Task6-subtaskA-testdata-gold.txt";
	static FileWriter fw;
	// remove all @...
	// replace all unalphabet by " "
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
            	
            	if (1+1==2)
            	{
	            	fw.write(tempString+"\n" );
	            	continue;
            	}
            	tempString = tempString.replaceAll("@[a-zA-Z]+", " USER ");
            	tempString = tempString.replaceAll("(l|L)(et's)|(ET'S) ", "let us ");
            	tempString = tempString.replaceAll("'(s|S) ", " is has ");
            	tempString = tempString.replaceAll("'(ll|LL) ", " will ");
            	tempString = tempString.replaceAll(" (won't)|(WON'T) ", " will not ");
            	tempString = tempString.replaceAll("'(t|T) ", " not ");
            	tempString = tempString.replaceAll(" (I|i)'(m|M) ", " I am ");
            	tempString = tempString.replaceAll("'(re|RE) ", " are ");
            	tempString = tempString.replaceAll("\\$", " MONEY ");
            	tempString = tempString.replaceAll("&", " and ");
            	tempString = tempString.replaceAll("([0-9]|2[0-4]|[0-1][0-9]):[0-5][0-9]", " TIME ");
            	String[] list = tempString.split("\t");            	
            	for (int i = 0;i<3;i++)
            	{
            		if (i!=2)
            		fw.write(list[i]+"\t");
            		else
            		{
            			String[] words = list[2].split("[\\^#*~\\-/ \\[\\]().?!,'\":;<]");
            			for (int j = 0;j<words.length;j++)
            			{
            				boolean flag = true;
            				if (words[j].matches("[ ]*")) { fw.write(" ");continue;}
            				if (words[j].matches("([19]|[20])[0-9]{2}")) { fw.write(" DATE ");continue;}
            				if (words[j].matches("[0-9]+%")) { fw.write(" RATE ");continue;}
            				if (words[j].matches("[0-9]+(st|th|nd|rd)")) { fw.write(" DATE RANK ");continue;}
            				if (words[j].matches("[0-9]+(s|hr|yr|am|pm)(s)?")) { fw.write(" TIME ");continue;}
            				if (words[j].matches("[0-9]+")) { fw.write(" NUMBER ");continue;}
            				
            				if (words[j].matches("[a-zA-Z][a-z]+"))
            				{
            					String word = words[j].toLowerCase();
            					//if (word.charAt(0)=='#') word = word.replaceAll("#", "");
            					//char[] tmp = new char[word.length()+1];
            					//tmp[0] = word.charAt(0);
            					//int mark = 1;
            					for (int k = 1;k<word.length()-1;k++)
            					{
            						while (k<word.length()-1&&word.charAt(k)==word.charAt(k-1)&&word.charAt(k)==word.charAt(k+1))
            						{
            							flag = false;
            							//System.out.println(word);
            							k++;
            						}
            					//	tmp[mark++] = word.charAt(k);
            					}
            					//tmp[mark++] = word.charAt(word.length()-1);
            					//tmp[mark++] = '\0';
            					if (flag==false)
            					{
            					 //System.out.println(word);
            					
            					 if (word.contains("all")) word = "all";
            					 if (word.contains("fuu")&&word.contains("uuck")) word = "fuck";
            					 if (word.contains("soo")) word = "so";
            					 if (word.contains("suu")&&word.contains("uuper")) word = "super";
            					 if (word.contains("yaa")&&word.contains("aas")) word = "yas";
            					 if (word.contains("ayyy")) word = "ay";
            					 if (word.contains("lmao")) word = "lmao";
            					 //if (word.contains("hlllary")) word = "hillary";
            					 //System.out.println(word);
            					 fw.write(word+" ");
            					}
            					else fw.write(word+" ");
            				}
            				else
            				{
            					if (words[j].matches("[A-Z]+")) {fw.write(words[j]+" ");continue;}
            					if (words[j].equals("I")||words[j].equals("i")) {fw.write(words[j]+" ");continue;}
            					if (words[j].equals("a")||words[j].equals("A")) {fw.write(words[j]+" ");continue;}
            					if (words[j].equals("&")) {fw.write(" and ");continue;}
            					if (words[j].equals("u")||words[j].equals("U")) {fw.write("you ");continue;}
            					if (words[j].equals("r")||words[j].equals("R")) {fw.write("are ");continue;}
            					if (words[j].equals("SemST")) continue;
            					if (words[j].length()==1) continue;
            					Vector<String> allSplit = new Vector<String>();
            					Vector<String> outSplit = new Vector<String>();
            					int pre = 0;
            					int now = 0;
            					while (now<words[j].length()-1)
            					{
            						char a = words[j].charAt(now);
            						char b = words[j].charAt(now+1);
            						if (!(a<='Z'&&a>='A'&&b<='Z'&&b>='A'))
            						if (!(a<='9'&&a>='0'&&b<='9'&&b>='0'))
            						if (!(a<='z'&&a>='a'&&b<='z'&&b>='a'))
            						{
            							allSplit.addElement(words[j].substring(pre ,now+1));
            							pre = now+1;
            						}
            						now++;
            					}
            					allSplit.addElement(words[j].substring(pre, now+1));
            					for (int k = 0;k<allSplit.size();k++)
            					{
            						String tmp = allSplit.elementAt(k);
            						if (tmp.matches("(nd|th|rd|st)")) continue;
            						if (tmp.matches("[a-z]+")) outSplit.addElement(tmp+" ");
            						else
            						if (tmp.matches("[A-Z]+"))
            							if (tmp.length()>2||k+1==allSplit.size()||!allSplit.elementAt(k+1).matches("[a-z]+")) outSplit.addElement(tmp+" ");
            							else{
            								if (tmp.length()==2)
            									outSplit.addElement(tmp.charAt(0)+" "+tmp.charAt(1)+allSplit.elementAt(k+1)+" ");
            								else
            								outSplit.addElement(tmp+allSplit.elementAt(k+1)+" ");
            								k++;
            							}
            						else
            						if (tmp.matches("[0-9]+"))
            						{
            							if (k+1!=allSplit.size()&&allSplit.elementAt(k+1).matches("(nd|th|rd|st)"))
            							{
            								outSplit.addElement(" DATA ");
            							}
            							else
            							if (tmp.equals("2")) outSplit.addElement(" to ");
            							else
            							if (tmp.equals("4")) outSplit.addElement(" for ");
            							else
            								outSplit.addElement(" NUMBER ");
            						}
            					}
            					//System.out.println(words[j]);
            					for (int k = 0;k<outSplit.size();k++)
            						fw.write(outSplit.elementAt(k)+" ");
            				}
            			}
            			fw.write("\t");
            		}
            	}
            	fw.write(list[3]+"\n");
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
		fw = new FileWriter(filename);
		readFileByLines(filename.replace(".", "-origin."));
		fw.close();
		fw = new FileWriter(filename2);
		readFileByLines(filename2.replace(".", "-origin."));
		fw.close();
	}
}
