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
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import TextProcessTool.TPT;

public class Feature4FLE {
	// 读特征、对每一个，求topk，输出k*k矩阵
	static int ParaK = 10;
	static int GateOne = 80;
	static String isStan = "st_";
	static String Feature_path = "Feature.txt";
	static String Matrix_path = isStan+"Matrix.txt";
	static String ClassAns_path = "Feature_SVM/Cross_SVM_topic.txt.ans";
	static String TestClassAns_path = "Feature_SVM/Test_SVM_topic.txt.ans";
	static String Matrix_Value_path = isStan+"Matrix_Value.txt";
	static String Matrix_ID_path = isStan+"Matrix_ID.txt";
	static String Feature_test_path = "Feature_test.txt";
	static HashMap<String, Integer> Topic_Number = new HashMap<String, Integer>();
	static HashMap<String, Vector<HashMap<String, Double>>> Topic_Twitter_All_Feature = new HashMap<String, Vector<HashMap<String, Double>>>();
	static HashMap<String, Vector<String>> Topic_Twitter_All_Predisposition = new HashMap<String, Vector<String>>();
	static HashMap<String, Vector<Integer>> Topic_Twitter_All_ID = new HashMap<String, Vector<Integer>>();

	// remove all @...
	// replace all unalphabet by " "
	public static int readMatrixByLines(String fileName,Double Matrix[][],int docu) {
		File file = new File(fileName);
		BufferedReader reader = null;
		int line = 0;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			tempString = null;
			line = 0;
			//System.out.println(docu);
			for (int i = 0;i<docu;i++)
				for (int j = 0;j<docu;j++)
				{
					tempString = reader.readLine();					
					Matrix[i][j] = Matrix[j][i] = 1-Double.parseDouble(tempString);
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
	public static int readTestMatrixByLines(String fileName,Double Matrix[][],int modelNum,int docu) {
		File file = new File(fileName);
		BufferedReader reader = null;
		int line = 0;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			tempString = null;
			line = 0;
			//System.out.println(docu);
			for (int i = 0;i<docu;i++)
				for (int j = 0;j<modelNum;j++)
				{
					tempString = reader.readLine();					
					Matrix[i+modelNum][j] = Matrix[j][i+modelNum] = 1-Double.parseDouble(tempString);
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
	public static int readFeatureByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		int line = 0;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			tempString = null;
			line = 0;
			while ((tempString = reader.readLine()) != null) {
				String[] list = tempString.split("\t");
				String topic = list[0];
				if (!Topic_Twitter_All_Feature.containsKey(topic))
					Topic_Twitter_All_Feature.put(topic, new Vector<HashMap<String, Double>>());
				if (!Topic_Twitter_All_Predisposition.containsKey(topic))
					Topic_Twitter_All_Predisposition.put(topic, new Vector<String>());
				if (!Topic_Twitter_All_ID.containsKey(topic))
					Topic_Twitter_All_ID.put(topic, new Vector<Integer>());
				line = Integer.parseInt(list[1]);
				for (int i = 0; i < line; i++) {
					tempString = reader.readLine();
					list = tempString.split("\t");
					int id = Integer.parseInt(list[0]);
					Topic_Twitter_All_Predisposition.get(topic).add(list[1]);
					Topic_Twitter_All_ID.get(topic).add(id);
					HashMap<String, Double> now = TPT.StringProcess(list, (double) line, 3);
					TPT.FeatureNormalization(now);
					Topic_Twitter_All_Feature.get(topic).add(now);
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

	public static void main(String[] args) {
		readFeatureByLines(Feature_path);
		{
			Iterator iter = Topic_Twitter_All_Feature.entrySet().iterator();
			while (iter.hasNext()) {
				HashMap.Entry entry = (HashMap.Entry) iter.next();
				String topic = (String) entry.getKey();
				Topic_Number.put(topic, Topic_Twitter_All_Feature.get(topic).size());
			}
		}
		readFeatureByLines(Feature_test_path);
		try {
			FileWriter fw = new FileWriter(Matrix_path);
			FileWriter fw_Va = new FileWriter(Matrix_Value_path);
			FileWriter fw_ID = new FileWriter(Matrix_ID_path);
			FileWriter ans_ID = new FileWriter("ans_ID.txt");
			Iterator iter = Topic_Twitter_All_Feature.entrySet().iterator();
			while (iter.hasNext()) {
				int posi = 0;int nega = 0;
				int count = 0;
				HashMap.Entry entry = (HashMap.Entry) iter.next();
				String topic = (String) entry.getKey();
				System.out.println(topic);
				int modelNum = Topic_Number.get(topic);
				Vector<HashMap<String, Double>> allFeature = (Vector<HashMap<String, Double>>) entry.getValue();
				Vector<String> T_P = Topic_Twitter_All_Predisposition.get(topic);
				fw.write(topic + "\t" + allFeature.size() +"\t"+modelNum+ "\n");
				fw_ID.write(allFeature.size()+"\t"+ParaK+"\t"+modelNum+"\n");
				fw_Va.write(allFeature.size()+"\t"+ParaK+"\t"+modelNum+"\n");
				Double[][] Matrix = new Double[allFeature.size()][allFeature.size()];
				Double[][] MatrixDis = new Double[allFeature.size()][allFeature.size()];
				for (int i = 1; i < allFeature.size(); i++)
					for (int j = 0; j < i; j++) {
						MatrixDis[i][j] = MatrixDis[j][i] = TPT.CosineDistance(allFeature.elementAt(i),
								allFeature.elementAt(j));
					}
				readMatrixByLines(ClassAns_path.replaceAll("topic", topic.replaceAll(" ", "")),Matrix,modelNum);
				readTestMatrixByLines(TestClassAns_path.replaceAll("topic", topic.replaceAll(" ", "")),Matrix,modelNum,allFeature.size()-modelNum);
				for (int i = 0;i<allFeature.size();i++) Matrix[i][i] = 1.0;
				for (int i = 0;i<allFeature.size();i++) MatrixDis[i][i] = 1.0;
				for (int i = 0;i<T_P.size();i++)
					if (T_P.elementAt(i).equals("FAVOR")) posi++; else nega++;
				
				
				int gatenum= 0;
				
				for (int i = 0; i < modelNum; i++) {
					HashMap<Integer, Double> ID_Dis = new HashMap<Integer, Double>();
					for (int j = 0; j < modelNum; j++)
						if (j!=i)
							ID_Dis.put(j, Matrix[i][j]);
					List<HashMap.Entry<Integer, Double>> infoIds = new ArrayList<HashMap.Entry<Integer, Double>>(
							ID_Dis.entrySet());
					Collections.sort(infoIds, new Comparator<HashMap.Entry<Integer, Double>>() {
						public int compare(HashMap.Entry<Integer, Double> o1, HashMap.Entry<Integer, Double> o2) {
							// return (o2.getValue() - o1.getValue());
							if ((o1.getValue() - o2.getValue()) < 0)
								return 1;
							else
							if ((o1.getValue() - o2.getValue()) > 0)	
								return -1;
							else return 0;
						}
					});
					int sum = 0;
					for (int j = 0;j<ParaK*2;j++)
					{
						 int id = infoIds.get(j).getKey();
						 if (T_P.elementAt(id).equals(T_P.elementAt(i)))
							 sum++;
					}
					if (sum*2>ParaK*2) gatenum++; 
					//System.out.println(mark);
				}
				//GateOne = modelNum-gatenum;
				
				
				
				
				System.out.println("Gate "+GateOne);
				for (int i = 0; i < allFeature.size(); i++) {
					if (i>=modelNum) ans_ID.write(Topic_Twitter_All_ID.get(topic).elementAt(i)+"\n");
					HashMap<Integer, Double> ID_Dis = new HashMap<Integer, Double>();
					for (int j = 0; j < modelNum; j++)
						if (j!=i)
							ID_Dis.put(j, Matrix[i][j]);
					List<HashMap.Entry<Integer, Double>> infoIds = new ArrayList<HashMap.Entry<Integer, Double>>(
							ID_Dis.entrySet());
					Collections.sort(infoIds, new Comparator<HashMap.Entry<Integer, Double>>() {
						public int compare(HashMap.Entry<Integer, Double> o1, HashMap.Entry<Integer, Double> o2) {
							// return (o2.getValue() - o1.getValue());
							if ((o1.getValue() - o2.getValue()) < 0)
								return 1;
							else
							if ((o1.getValue() - o2.getValue()) > 0)	
								return -1;
							else return 0;
						}
					});
					double gate = infoIds.get(GateOne).getValue();
					ID_Dis = new HashMap<Integer, Double>();
					for (int j = 0; j < modelNum; j++)
						if (j!=i&&Matrix[i][j]>gate)
							ID_Dis.put(j, MatrixDis[i][j]);
					infoIds = new ArrayList<HashMap.Entry<Integer, Double>>(
							ID_Dis.entrySet());
					Collections.sort(infoIds, new Comparator<HashMap.Entry<Integer, Double>>() {
						public int compare(HashMap.Entry<Integer, Double> o1, HashMap.Entry<Integer, Double> o2) {
							// return (o2.getValue() - o1.getValue());
							if ((o1.getValue() - o2.getValue()) < 0)
								return 1;
							else
							if ((o1.getValue() - o2.getValue()) > 0)	
								return -1;
							else return 0;
						}
					});

					// 排序后
					fw.write(T_P.elementAt(i) + "\t");
					int sum = 0;
					for (int i1 = 0; i1 < ParaK; i1++) {						
						int id = infoIds.get(i1).getKey();
						if (T_P.elementAt(id).equals(T_P.elementAt(i))) sum++;
						fw.write(id + "\t" + T_P.elementAt(id) + "\t" + MatrixDis[i][id]+"\t");
						fw_ID.write(id+"\t");
					}
					if ((double)sum/ParaK<0.5) count ++;
					fw.write("\n");
					fw_ID.write("\n");
					for (int i1 = 0; i1 < ParaK; i1++) {
						for (int j1 = 0; j1 < ParaK; j1++) {
							int id1 = infoIds.get(i1).getKey();
							int id2 = infoIds.get(j1).getKey();
							fw.write(MatrixDis[id1][id2] + "\t");							
							fw_Va.write((1-MatrixDis[i][id1]-MatrixDis[i][id2]+MatrixDis[id1][id2])+"\t");
						}
						fw.write("\n");
						fw_Va.write("\n");
					}

				}
				for (int i = 0;i<allFeature.size();i++)
				{
					int val = 0;
					if (T_P.elementAt(i).equals("FAVOR")) val = 1;
					else
						if (T_P.elementAt(i).equals("NONE")) val = 0;
						else val = -1;
					fw_Va.write(val+"\t");
			}
			fw_Va.write("\n");
			System.out.println(count);
			System.out.println(posi+"\t"+nega);
			}
			fw.close();
			fw_Va.close();
			fw_ID.close();
			ans_ID.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
