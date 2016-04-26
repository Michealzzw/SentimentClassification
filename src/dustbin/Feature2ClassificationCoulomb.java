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

public class Feature2ClassificationCoulomb {
	// 读特征、对每一个，求topk，输出k*k矩阵
	static int ParaK = 14;
	static String sentiment = "AGAINST";
	static String Feature_path = "Feature.txt";
	static String Matrix_path = "Matrix_"+sentiment+".txt";
	static String Matrix_Matlab_path = "Matrix_Matlab_"+sentiment+".txt";
	static String Matrix_ID_path = "Matrix_ID_"+sentiment+".txt";
	static HashMap<String, Vector<HashMap<String, Double>>> Topic_Twitter_Other_Feature = new HashMap<String, Vector<HashMap<String, Double>>>();
	static HashMap<String, Vector<HashMap<String, Double>>> Topic_Twitter_All_Feature = new HashMap<String, Vector<HashMap<String, Double>>>();

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
				if (!Topic_Twitter_Other_Feature.containsKey(topic))
					Topic_Twitter_Other_Feature.put(topic, new Vector<HashMap<String, Double>>());
				line = Integer.parseInt(list[1]);
				for (int i = 0; i < line; i++) {
					tempString = reader.readLine();
					list = tempString.split("\t");
					HashMap<String, Double> now = TPT.StringProcess(list, (double) line, 2);
					TPT.FeatureNormalization(now);
					if (!list[0].equals(sentiment))
						Topic_Twitter_Other_Feature.get(topic).add(now);
					else
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
				Vector<HashMap<String, Double>> OtherFeature = Topic_Twitter_Other_Feature.get(topic);
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

					// 排序后
					fw.write(sentiment + "\t");
					double[] other_distance = new double[OtherFeature.size()];
					for (int i1 = 0;i1<OtherFeature.size();i1++)
						other_distance[i1] = TPT.CosineDistance(allFeature.elementAt(i),
								OtherFeature.elementAt(i1));
						
					double gate_value = Matrix[i][infoIds.get(ParaK-1).getKey()];
					Vector<HashMap<String,Double>> ElectronList = new Vector<HashMap<String,Double>>();
					ElectronList.addElement(allFeature.elementAt(i));
					for (int i1 = 0;i1<OtherFeature.size();i1++) if (other_distance[i1]>gate_value)
					{
						ElectronList.addElement(OtherFeature.elementAt(i1));
					}
					HashMap<String,Double> Electron = null;
					if (ElectronList.size()>1)
					{
						Electron = TPT.Coulomb(ElectronList);
						TPT.FeatureNormalization(Electron);
					}
					else
						Electron = allFeature.elementAt(i);
					double[] Distance = new double[ParaK];
					for (int i1 = 0; i1 < ParaK; i1++) { Distance[i1] = TPT.CosineDistance(Electron,
							allFeature.elementAt(infoIds.get(i1).getKey()));}
					for (int i1 = 0; i1 < ParaK; i1++) {
						int id = infoIds.get(i1).getKey();
						fw.write(id + "\t" + sentiment + "\t" + Distance[i1]+"\t");
						fw_ID.write(id+"\t");
					}
					fw.write("\n");
					fw_ID.write("\n");
					for (int i1 = 0; i1 < ParaK; i1++) {
						for (int j1 = 0; j1 < ParaK; j1++) {
							int id1 = infoIds.get(i1).getKey();
							int id2 = infoIds.get(j1).getKey();
							fw.write(Matrix[id1][id2] + "\t");							
							fw_M.write((1-Distance[i1]-Distance[j1]+Matrix[id1][id2])+"\t");
						}
						fw.write("\n");
						fw_M.write("\n");
					}

				}
				for (int i = 0;i<allFeature.size();i++)
				{
					int val = 0;
					if (sentiment.equals("FAVOR")) val = 1;
					else
						if (sentiment.equals("NONE")) val = 0;
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
