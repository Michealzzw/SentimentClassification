package TextProcessTool;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import PosterStemmer.Stemmer;

public class TPT {
	static Stemmer stem = new Stemmer();
	public static String Stemming(String word)
	{
		stem.add(word.toCharArray(),word.length());
		stem.stem();
		word = stem.toString();
		return word;
	}
	public static String RemoveAtUsername(String Sent)
	{
		 return Sent.replaceAll("@[^ ]+", " ");
	}
	static Pattern wordPt = Pattern.compile("[a-zA-Z]+");
	public static Vector<String> Sentense2Words(String Sent)
	{
		Vector<String> words = new Vector<String>();
		Sent = Sent.replaceAll("[^a-zA-Z]+", " ");
		
		Matcher wordMc = wordPt.matcher(Sent);
		while (wordMc.find())
		{
			String word = Stemming(wordMc.group().toLowerCase());
			words.addElement(word);
		}
		return words;
	}
	public static HashMap<String,Integer> Sentense2WordList(String Sent, boolean stem)
	{
		HashMap<String,Integer> words = new HashMap<String,Integer>();
		Sent = Sent.replaceAll("[^a-zA-Z]+", " ");		
		Matcher wordMc = wordPt.matcher(Sent);
		String preword = null;
		while (wordMc.find())
		{
			String word;
			if (stem) word = Stemming(wordMc.group().toLowerCase());
			else word = wordMc.group().toLowerCase();
			if (!words.containsKey(word)) words.put(word, 0);
			words.put(word, words.get(word)+1);
		/*	if (preword!=null)
			{
				if (!words.containsKey(preword+word)) words.put(preword+word, 0);
				words.put(preword+word, words.get(preword+word)+1);
			}
			*/
			preword = word;
		}
		return words;
	}
	public static double TFIDFcalculate(double TF,double IDF,double Dnum)
	{
		return (TF*Math.log(Dnum/IDF+1));
		//return (TF);
	}
	public static HashMap<String,Double> StringProcess(String[] list,Double Dnum,int StartIndex)
	{
		HashMap<String,Double> HM = new HashMap<String,Double>();
		for (int i = StartIndex;i<list.length;i+=3)
		{
			HM.put(list[i], TFIDFcalculate(Double.parseDouble(list[i+1]),Double.parseDouble(list[i+2]),Dnum));
		}
		return HM;
	}
	public static void FeatureNormalization(HashMap<String,Double> now)
	{
		double sum = 0.0;
		Iterator iter = now.entrySet().iterator();
		Vector<String> wordentry = new Vector<String>();
		while (iter.hasNext()) {
			HashMap.Entry entry = (HashMap.Entry) iter.next();
			wordentry.addElement((String)entry.getKey());
			sum += (double) entry.getValue()*(double) entry.getValue();
		}
		sum = Math.sqrt(sum);
		for (int i = 0;i<wordentry.size();i++)
		{
			now.put(wordentry.elementAt(i), now.get(wordentry.elementAt(i))/sum);
		}
	}
	public static double CosineDistance(HashMap<String,Double> a,HashMap<String,Double> b)
	{
		double sum = 0.0;
		Iterator iter = a.entrySet().iterator();
		while (iter.hasNext()) {
			HashMap.Entry entry = (HashMap.Entry) iter.next();
			String word = (String) entry.getKey();
			if (b.containsKey(word))
				sum += (double) entry.getValue()*b.get(word);
		}
		return sum;
	}
	
	
	
	
	
	
	public static double calculateBalance (Vector<Double> position,double power)
	{
		if (position.size()==1) return position.elementAt(0);
		double posi = position.elementAt(0);
		double ans = posi,ansforce = 1e10,ansdist = 1;
		double l,r;
		Object[] array = position.toArray();
		Arrays.sort(array);
		for (int i = 0;i<=array.length;i++)
		{
			if (i!=0) l = (double)array[i-1];
			else l = posi-1;
			if (i!=array.length)
				r = (double)array[i];
			else r = posi+1;
			if (l>r||l<posi-1||r>posi+1) continue;
			while (Math.abs(l-r)>1e-6)
			{
				double ltmp = (r-l)/3.0+l;
				double rtmp = r-(r-l)/3.0;
				double lforce = calculateForce(position,power,ltmp);
				double rforce = calculateForce(position,power,rtmp);
				if (Math.abs(lforce)>Math.abs(rforce))
					l = ltmp;
				else r = rtmp;				
				if (Math.abs(lforce)<1e-3)
				{
					if (Math.abs(l-posi)<ansdist)
							
					{
						ansforce = Math.abs(lforce);
						ans = l;
						ansdist = Math.abs(l-posi);
					}
				}
				else
				if (Math.abs(lforce)<ansforce) 
					{
						ansforce = Math.abs(lforce);
						ans = l;
						ansdist = Math.abs(l-posi);
					}
			}
		}
		return ans;
	}
	public static double calculateForce (Vector<Double> position,double power,double now)
	{
		double posi = position.elementAt(0);
		double sign = 1.0;
		if (now<posi) sign = -1.0;
		double force = sign*power/((1-Math.abs(now-posi))*(1-Math.abs(now-posi))+1e-10);
		for (int i = 1;i<position.size();i++)
		{
			double nega = position.elementAt(i);
			sign = 1.0;
			if (now>nega) sign = -1.0;
			force += sign*1/((now-nega)*(now-nega)+1e-10);	
		}
		return force;
			
	}
	public static HashMap<String,Double> Coulomb(Vector<HashMap<String,Double>> ElectronList)
	{
		HashMap<String,Double> Electron = new HashMap<String,Double>();
		Iterator iter = ElectronList.elementAt(0).entrySet().iterator();
		while (iter.hasNext()) {
			HashMap.Entry entry = (HashMap.Entry) iter.next();
			String word = (String) entry.getKey();
			Vector<Double> position = new Vector<>();
			position.addElement((double)entry.getValue());
			for (int i = 1;i<ElectronList.size();i++)
			if (ElectronList.elementAt(i).containsKey(word))
			{
				position.addElement(ElectronList.elementAt(i).get(word));
			}
			Electron.put(word, calculateBalance(position,ElectronList.size()));
		}
		for (int now = 1;now<ElectronList.size();now++)
		{
			iter = ElectronList.elementAt(now).entrySet().iterator();
			while (iter.hasNext()) {
				HashMap.Entry entry = (HashMap.Entry) iter.next();
				String word = (String) entry.getKey();
				if (Electron.containsKey(word)) continue;
				Vector<Double> position = new Vector<>();
				position.addElement(0.0);
				for (int i = now;i<ElectronList.size();i++)
				if (ElectronList.elementAt(i).containsKey(word))
				{
					position.addElement(ElectronList.elementAt(i).get(word));
				}
				Electron.put(word, calculateBalance(position,ElectronList.size()));
			}
		}
		return Electron;
	}
}
