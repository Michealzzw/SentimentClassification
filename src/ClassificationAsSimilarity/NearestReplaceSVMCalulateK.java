package ClassificationAsSimilarity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class NearestReplaceSVMCalulateK {
	static String root = "../";
	static String pathtrain = root+"ans_SVM_train_num.txt";
	static String pathtest = root+"ans_SVM_test_num.txt";
	static String pathans = root+"libsvm-3.21/num.ans";
	static int ParaK = 41;
	static Vector<Vector<Double>> Features;
	static Vector<Vector<Double>> FeaturesTest;
	static Vector<Integer> Label;
	static Vector<Integer> LabelTest;
	public static int readFeatureByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		int line = 0;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			tempString = null;
			line = 0;
			while ((tempString=reader.readLine())!=null)
			{
				String[] list = tempString.split("\t");
				Label.addElement(Integer.parseInt(list[0]));
				Vector<Double> now = new Vector<Double>();
				for (int i = 1;i<list.length;i++)
				{
					String[] pair = list[i].split(":");
					now.addElement(Double.parseDouble(pair[1]));
				}
				Features.addElement(now);
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
	public static int readTestByLines(String fileName,String outName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		int line = 0;
		try {
			FileWriter fw = new FileWriter(outName);
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			tempString = null;
			line = 0;
			while ((tempString=reader.readLine())!=null)
			{
				String[] list = tempString.split("\t");
				Vector<Double> now = new Vector<Double>();
				FeaturesTest.addElement(now);
				LabelTest.addElement(Integer.parseInt(list[0]));
				for (int i = 1;i<list.length;i++)
				{
					String[] pair = list[i].split(":");
					now.addElement(Double.parseDouble(pair[1]));
				}
				HashMap<Integer, Double> ID_Dis = new HashMap<Integer, Double>();
				for (int i = 0; i < Features.size(); i++)
				{
					double sum = 0.0;
					Vector<Double> tmp = Features.elementAt(i);
					for (int j = 0;j<tmp.size();j++)
					{
						sum+= (tmp.elementAt(j)-now.elementAt(j))*(tmp.elementAt(j)-now.elementAt(j));
					}
					ID_Dis.put(i, sum);
				}					
				List<HashMap.Entry<Integer, Double>> infoIds = new ArrayList<HashMap.Entry<Integer, Double>>(
						ID_Dis.entrySet());
				Collections.sort(infoIds, new Comparator<HashMap.Entry<Integer, Double>>() {
					public int compare(HashMap.Entry<Integer, Double> o1, HashMap.Entry<Integer, Double> o2) {
						// return (o2.getValue() - o1.getValue());
						if ((o1.getValue() - o2.getValue()) < 0)
							return -1;
						else
						if ((o1.getValue() - o2.getValue()) > 0)	
							return 1;
						else return 0;
					}
				});
				int[] label = new int[3];
				label[0] = 0;label[1] = 0;label[2] = 0;
				for (int i = 0; i < ParaK; i++) {						
					int id = infoIds.get(i).getKey();
					label[Label.elementAt(id)+1]++;
				}
				if (label[0]==label[2])
				{
					int id = infoIds.get(ParaK).getKey();
					label[Label.elementAt(id)+1]++;
				}
				int max = 0;
				for (int i=1;i<3;i++) if (label[i]>label[max]) max = i;
				fw.write(max-1+"\n");
			}
			fw.close();
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
	public static void main(String[] args) {
		if (args.length>0)
			ParaK = Integer.parseInt(args[0]);
		for (int i = 1;i<=5;i++)
		{
			Features = new Vector<Vector<Double>>();
			Label = new Vector<Integer>();
			FeaturesTest = new Vector<Vector<Double>>();
			LabelTest = new Vector<Integer>();
			readFeatureByLines(pathtrain.replace("num", String.valueOf(i)));
			boolean[][] mark = new boolean[Features.size()][Features.size()];
			for (int j1 = 0;j1<Features.size();j1++)
			{
				HashMap<Integer, Double> ID_Dis = new HashMap<Integer, Double>();
				Vector<Double> now = Features.elementAt(j1);
				for (int i1 = 0; i1 < Features.size(); i1++)
					if (i1!=j1)
					{
						double sum = 0.0;
						Vector<Double> tmp = Features.elementAt(i1);
						for (int j = 0;j<tmp.size();j++)
						{
							sum+= (tmp.elementAt(j)-now.elementAt(j))*(tmp.elementAt(j)-now.elementAt(j));
						}
						ID_Dis.put(i1, sum);
					}					
				List<HashMap.Entry<Integer, Double>> infoIds = new ArrayList<HashMap.Entry<Integer, Double>>(
						ID_Dis.entrySet());
				Collections.sort(infoIds, new Comparator<HashMap.Entry<Integer, Double>>() {
					public int compare(HashMap.Entry<Integer, Double> o1, HashMap.Entry<Integer, Double> o2) {
						// return (o2.getValue() - o1.getValue());
						if ((o1.getValue() - o2.getValue()) < 0)
							return -1;
						else
						if ((o1.getValue() - o2.getValue()) > 0)	
							return 1;
						else return 0;
					}
				});
				int other = 0,me = 0;
				int myid = Label.elementAt(j1);
				for (int j = 0;j<Features.size()-1;j++)
				{
					int id = infoIds.get(j).getKey();
					if (Label.elementAt(id)==myid) me++; else other++;
					if (me>other) mark[j1][j] = true;
					else mark[j1][j] = false;
				}
		//		System.out.println(me+" "+other);
			}
			int max = 0;
			for (int j = 0;j<Features.size()-1;j++)
			{
				int sum = 0;
				for (int k = 0;k<Features.size();k++) if (mark[k][j]) sum++;
				if (sum>=max) { max = sum;ParaK = j+1;}
			}
			for (int j = 0;j<Features.size()-1;j++)
			{
				int sum = 0;
				for (int k = 0;k<Features.size();k++) if (mark[k][j]) sum++;
				if (sum>=max*0.99) { ParaK = j+1;}
			}
			System.out.println(max+" "+ParaK);
			readTestByLines(pathtest.replace("num", String.valueOf(i)),pathans.replace("num", String.valueOf(i)));
//			for (int j1 = 0;j1<FeaturesTest.size();j1++)
//			{
//				HashMap<Integer, Double> ID_Dis = new HashMap<Integer, Double>();
//				Vector<Double> now = FeaturesTest.elementAt(j1);
//				for (int i1 = 0; i1 < Features.size(); i1++)
//					{
//						double sum = 0.0;
//						Vector<Double> tmp = Features.elementAt(i1);
//						for (int j = 0;j<tmp.size();j++)
//						{
//							sum+= (tmp.elementAt(j)-now.elementAt(j))*(tmp.elementAt(j)-now.elementAt(j));
//						}
//						ID_Dis.put(i1, sum);
//					}					
//				List<HashMap.Entry<Integer, Double>> infoIds = new ArrayList<HashMap.Entry<Integer, Double>>(
//						ID_Dis.entrySet());
//				Collections.sort(infoIds, new Comparator<HashMap.Entry<Integer, Double>>() {
//					public int compare(HashMap.Entry<Integer, Double> o1, HashMap.Entry<Integer, Double> o2) {
//						// return (o2.getValue() - o1.getValue());
//						if ((o1.getValue() - o2.getValue()) < 0)
//							return -1;
//						else
//						if ((o1.getValue() - o2.getValue()) > 0)	
//							return 1;
//						else return 0;
//					}
//				});
//				int other = 0,me = 0;
//				int myid = LabelTest.elementAt(j1);
//				for (int j = 0;j<Features.size();j++)
//				{
//					int id = infoIds.get(j).getKey();
//					if (Label.elementAt(id)==myid) me++; else other++;
//					if (me>other) mark[j1][j] = true;
//					else mark[j1][j] = false;
//				}
//		//		System.out.println(me+" "+other);
//			}
//			max = 0;
//			for (int j = 0;j<Features.size();j++)
//			{
//				int sum = 0;
//				for (int k = 0;k<FeaturesTest.size();k++) if (mark[k][j]) sum++;
//				if (sum>=max) { max = sum;ParaK = j+1;}
//			}
//			System.out.println(max+" "+ParaK);
//			readTestByLines(pathtest.replace("num", String.valueOf(i)),pathans.replace("num", String.valueOf(i)));
		}
	}
}
