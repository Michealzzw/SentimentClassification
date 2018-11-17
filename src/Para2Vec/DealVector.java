package Para2Vec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import TextProcessTool.TPT;

public class DealVector {
	
	public static String path = "vectors.txt";

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
            	String[] list = tempString.split(" ");
            	if (list[0].matches("semeval_[0-9]+"))
            	{
            		fw.write(tempString+"\n");
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
	public static void main(String[] args) throws IOException
	{
		fw = new FileWriter("Para2Vec.txt");
		readFileByLines(path);		
		fw.close();
	}
}
