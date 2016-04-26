package dustbin;

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

public class Feature2TraditionalClassificationMatrixNoseparate2LocallyEnlarge {
	// 读特征、对每一个，求topk，输出k*k矩阵
	static int ParaK = 14;
	static String Feature_path = "Feature.txt";
	static String Matrix_path = "Matrix.txt";
	static String Matrix_Matlab_path = "Matrix_Matlab.txt";
	static String Matrix_ID_path = "Matrix_ID.txt";
	static HashMap<String, Vector<HashMap<String, Double>>> Topic_Twitter_Favor_Feature = new HashMap<String, Vector<HashMap<String, Double>>>();
	static HashMap<String, Vector<HashMap<String, Double>>> Topic_Twitter_Against_Feature = new HashMap<String, Vector<HashMap<String, Double>>>();
	static HashMap<String, Vector<HashMap<String, Double>>> Topic_Twitter_None_Feature = new HashMap<String, Vector<HashMap<String, Double>>>();
	static HashMap<String, Vector<HashMap<String, Double>>> Topic_Twitter_All_Feature = new HashMap<String, Vector<HashMap<String, Double>>>();
	static HashMap<String, Vector<String>> Topic_Twitter_All_Predisposition = new HashMap<String, Vector<String>>();

	// remove all @...
	// replace all unalphabet by " "
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
				line = Integer.parseInt(list[1]);
				HashMap<String, Vector<HashMap<String, Double>>> T_T_F = null;
				Vector<HashMap<String, Double>> T_F = null;
				for (int i = 0; i < line; i++) {
					tempString = reader.readLine();
					list = tempString.split("\t");
					Topic_Twitter_All_Predisposition.get(topic).add(list[0]);
					if (list[0].equals("FAVOR"))
						T_T_F = Topic_Twitter_Favor_Feature;
					else if (list[0].equals("AGAINST"))
						T_T_F = Topic_Twitter_Against_Feature;
					else
						T_T_F = Topic_Twitter_None_Feature;
					if (!T_T_F.containsKey(topic))
						T_T_F.put(topic, new Vector<HashMap<String, Double>>());
					T_F = T_T_F.get(topic);
					HashMap<String, Double> now = TPT.StringProcess(list, (double) line, 2);
					TPT.FeatureNormalization(now);
					T_F.add(now);
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
		try {
			FileWriter fw = new FileWriter(Matrix_path);
			FileWriter fw_M = new FileWriter(Matrix_Matlab_path);
			FileWriter fw_ID = new FileWriter(Matrix_ID_path);
			Iterator iter = Topic_Twitter_All_Feature.entrySet().iterator();
			while (iter.hasNext()) {
				HashMap.Entry entry = (HashMap.Entry) iter.next();
				String topic = (String) entry.getKey();
				System.out.println(topic);
				Vector<HashMap<String, Double>> allFeature = (Vector<HashMap<String, Double>>) entry.getValue();
				Vector<String> T_P = Topic_Twitter_All_Predisposition.get(topic);
				fw.write(topic + "\t" + allFeature.size() + "\n");
				fw_ID.write(allFeature.size()+"\t"+ParaK+"\n");
				fw_M.write(allFeature.size()+"\t"+ParaK+"\n");
				Double[][] Matrix = new Double[allFeature.size()][allFeature.size()];
				for (int i = 1; i < allFeature.size(); i++)
					for (int j = 0; j < i; j++) {
						Matrix[i][j] = Matrix[j][i] = TPT.CosineDistance(allFeature.elementAt(i),
								allFeature.elementAt(j));
					}
				for (int i = 0;i<allFeature.size();i++) Matrix[i][i] = 1.0;
				for (int i = 0; i < allFeature.size(); i++) {
					HashMap<Integer, Double> ID_Dis = new HashMap<Integer, Double>();
					for (int j = 0; j < allFeature.size(); j++)
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
					double bias = (1-Matrix[i][infoIds.get(ParaK-1).getKey()]);
					// 排序后
					fw.write(T_P.elementAt(i) + "\t");
					for (int i1 = 0; i1 < ParaK; i1++) {
						int id = infoIds.get(i1).getKey();
						fw.write(id + "\t" + T_P.elementAt(id) + "\t" + Matrix[i][id]+"\t");
						fw_ID.write(id+"\t");
					}
					fw.write("\n");
					fw_ID.write("\n");
					for (int i1 = 0; i1 < ParaK; i1++) {
						for (int j1 = 0; j1 < ParaK; j1++) {
							int id1 = infoIds.get(i1).getKey();
							int id2 = infoIds.get(j1).getKey();
							double bias1 = 0.0 , bias2 = 0.0;
							if (!T_P.elementAt(i).equals(T_P.elementAt(id1))) bias1 = bias;
							if (!T_P.elementAt(i).equals(T_P.elementAt(id2))) bias2 = bias;
							fw.write(Matrix[id1][id2] + "\t");							
							fw_M.write((1-Matrix[i][id1]-Matrix[i][id2]+bias1+bias2+Matrix[id1][id2])+"\t");
						}
						fw.write("\n");
						fw_M.write("\n");
					}

				}
				for (int i = 0;i<allFeature.size();i++)
				{
					int val = 0;
					if (T_P.elementAt(i).equals("FAVOR")) val = 1;
					else
						if (T_P.elementAt(i).equals("NONE")) val = 0;
						else val = -1;
					fw_M.write(val+"\t");
				}
				fw_M.write("\n");
			}
			fw.close();
			fw_M.close();
			fw_ID.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
