package DataProcess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

public class SVMSubsetSelection {
	static String path = "../o_ans_SVM_train_ID.txt";
	public static int readAnsByLines(String fileName) {
		Random random = new Random();
		Vector<String> posi = new Vector<String>();
		Vector<String> nega = new Vector<String>();
		File file = new File(fileName);
		BufferedReader reader = null;
		int line = 0;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			tempString = null;
			line = 0;
			while ((tempString = reader.readLine()) != null) {
				if (tempString.split("(\t)|( )")[0].equals("-1"))
					nega.addElement(tempString);
				else posi.addElement(tempString);
			}
			reader.close();
			FileWriter fw = new FileWriter(fileName.replaceAll("o_", ""));
			System.out.println(posi.size());
			System.out.println(nega.size());
			if (posi.size()<nega.size())
			{
				Vector<String> tmp = posi;
				posi = nega;
				nega = tmp;
			}
			for (int i = 0;i<posi.size();i++)
				fw.write(posi.elementAt(i)+'\n');
			for (int i = 0;i<nega.size();i++)
			{
				//int ran = random.nextInt(nega.size());
				//fw.write(nega.elementAt(ran)+"\n");
				//nega.removeElementAt(ran);
				fw.write(nega.elementAt(i)+"\n");
			}
			for (int i = 0;i<posi.size()-nega.size();i++)
			{
				int ran = random.nextInt(nega.size());
				fw.write(nega.elementAt(ran)+"\n");
				//nega.removeElementAt(ran);
				//fw.write(nega.elementAt(i)+"\n");
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
	public static void main(String[] args)
	{
	//	for (int i = 1;i<6;i++)
		//readAnsByLines(path.replaceFirst("ID", String.valueOf(i)));
	}
}
