package ClassificationAsSimilarity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class DealFinalTrainData {
	static String path = "ans_SVM_train_num.txt";
	public static int readByLines(String fileName) {
		File file = new File(fileName);
		FileWriter fw  = null;
		try {
			fw = new FileWriter("select_"+fileName);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		BufferedReader reader = null;
		int line = 0;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			tempString = null;
			line = 0;
			Vector<String> Favor = new Vector<String> ();
			Vector<String> Against = new Vector<String> ();
			Vector<String> None = new Vector<String> ();			
			while ((tempString = reader.readLine()) != null) {
				int label = Integer.parseInt(tempString.split("\t")[0]);
				if (label==1) Favor.addElement(tempString);
				else
				if (label==-1) Against.addElement(tempString);
				else None.addElement(tempString);
			}
			reader.close();
			int min = 1000;
			if (Favor.size()<min&&Favor.size()!=0) min = Favor.size();
			if (Against.size()<min&&Against.size()!=0) min = Against.size();
			if (None.size()<min&&None.size()!=0) min = None.size();
			for (int i = 0;i<min*4;i++)
			{
				
				int id;
				if (Favor.size()!=0)
				{
					id = (int)(Math.random()*Favor.size());
					fw.write(Favor.elementAt(id)+'\n');
					Favor.remove(id);
				}
				if (Against.size()!=0)
				{
					id = (int)(Math.random()*Against.size());
					fw.write(Against.elementAt(id)+'\n');
					Against.remove(id);
				}
				if (None.size()!=0)
				{
					id = (int)(Math.random()*None.size());
					fw.write(None.elementAt(id)+'\n');
					None.remove(id);
				}
			}
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
	public static void main(String[] args){
		for (int i = 1;i<=5;i++)
		{
			readByLines(path.replace("num", String.valueOf(i)));	
		}
	}
}
