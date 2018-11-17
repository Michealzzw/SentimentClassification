package DataProcess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

public class SVMSubsetSelectionLDAFeatrue {
	static String path = "PCA_Feature.txt";
	public static int readAnsByLines(String fileName,int num) {
		Random random = new Random();
		File file = new File(fileName);
		BufferedReader reader = null;
		int line = 0;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			tempString = null;
			FileWriter fw = new FileWriter("LDA_Feature"+String.valueOf(num)+".txt");
			line = 0;
			while ((tempString = reader.readLine()) != null) {
				//fw.write(tempString);
				Vector<String> posi = new Vector<String>();
				Vector<String> nega = new Vector<String>();
				int dnum = Integer.parseInt(tempString.split("(\t)|( )")[0]);
				int modelNum = Integer.parseInt(tempString.split("(\t)|( )")[1]);
				//System.out.println(dnum);
				//System.out.println(modelNum);
				for (int i = 0;i<modelNum;i++)
				{
					tempString = reader.readLine();
					if (tempString.split("(\t)|( )")[0].equals("-1"))
						nega.addElement(tempString);
					else posi.addElement(tempString);
				}
				System.out.println(posi.size());
				System.out.println(nega.size());
				if (posi.size()>nega.size())
				{
					Vector<String> tmp = posi;
					posi = nega;
					nega = tmp;
				}
				fw.write((posi.size()*2+dnum-modelNum)+" "+(posi.size()*2)+"\n");
				for (int i = 0;i<posi.size();i++)
					fw.write(posi.elementAt(i)+'\n');
				for (int i = 0;i<posi.size();i++)
				{
					int ran = random.nextInt(nega.size());
					fw.write(nega.elementAt(ran)+"\n");
					nega.removeElementAt(ran);
				}
				
				for (int i = 0;i<dnum-modelNum;i++)
					fw.write(reader.readLine()+"\n");
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
	public static void main(String[] args)
	{
		for (int i =0 ;i<10;i++)
		readAnsByLines(path,i);
	}
}
