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

public class CrossCalculateAll {
	static String[] topic={"FeministMovement","Atheism","HillaryClinton","LegalizationofAbortion","ClimateChangeisaRealConcern"};
	static String Result_path = "Feature_SVM/All_SVM";
	static String Feature_path = "Feature_SVM/Feature_SVM";
	static Vector<Vector<Integer>> allID;
	static Vector<Vector<Double>> allValue;
	static Vector<Integer> alltag;
	
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
            	String[] list = tempString.split("\t| ");
            	Vector<Integer> ID = new Vector<Integer>();
            	Vector<Double> Value = new Vector<Double>();
            	alltag.addElement(Integer.parseInt(list[0]));
            	allID.addElement(ID);
            	allValue.addElement(Value);
            	for (int i = 1;i<list.length;i++)
            	{
            		ID.addElement(Integer.parseInt(list[i].split(":")[0]));
            		Value.addElement(Double.parseDouble(list[i].split(":")[1]));
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
	public static void main(String[] args)
	{
		for (int topicid = 0;topicid<topic.length;topicid++)
		{
			allID = new  Vector<Vector<Integer>>();
			allValue = new  Vector<Vector<Double>>();
			alltag = new Vector<Integer>();
			int posi = 0;
			int nega = 0;
			int none = 0;
			readFileByLines(Feature_path+"_"+topic[topicid]+".txt");
			try {
				FileWriter fw = new FileWriter(Result_path+"_"+topic[topicid]+".txt");
				for (int i = 0;i<allID.size();i++)
				{
					for (int j = 0;j<allID.size();j++)						
					{
						//int id = infoIds.get(j).getKey();
						int id = j;
						//if (alltag.elementAt(i)!=0||alltag.elementAt(id)!=0) continue;
						if (alltag.elementAt(i)!=alltag.elementAt(id))
						{
							fw.write("1");
							nega++;
						}
						else 
							{
								fw.write("0");
								posi++;
							}
						Vector<Integer> IDa = allID.elementAt(i);
						Vector<Integer> IDb = allID.elementAt(id);
						Vector<Double> Valuea = allValue.elementAt(i);
						Vector<Double> Valueb = allValue.elementAt(id);
						int marka = 0;int markb = 0;
						while (marka<IDa.size()||markb<IDb.size())
						{
							if (marka<IDa.size()&&markb<IDb.size())
							{
								int ida = IDa.elementAt(marka);
								int idb = IDb.elementAt(markb);
								double valuea = Valuea.elementAt(marka);
								double valueb = Valueb.elementAt(markb);
								if (ida==idb)
								{
									//fw.write("\t"+ida+":"+Math.abs(valuea-valueb));
									marka++;
									markb++;
								}
								else
								if (ida<idb)
								{
									fw.write("\t"+ida+":1");
									marka++;
								}
								else
								{
									fw.write("\t"+idb+":1");
									markb++;
								}
							}
							else
								if (marka<IDa.size())
								{
									int ida = IDa.elementAt(marka);
									double valuea = Valuea.elementAt(marka);
									fw.write("\t"+ida+":1");
									marka++;
								}
								else
								{
									int idb = IDb.elementAt(markb);
									double valueb = Valueb.elementAt(markb);
									fw.write("\t"+idb+":1");
									markb++;
								}
						}
						fw.write("\n");
					}
				}
				fw.close();
				System.out.println("nega "+nega+" posi "+posi);
				System.out.println(topic[topicid]+" "+allID.size());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
