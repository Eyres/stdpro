package app.codebarre;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.JTextPane;

import listeners.LogListener;

public class ReadFile {
	public static void readFile(JTextPane screen, Data d, InputStream ips, String step) throws Exception{
		InputStreamReader ipsr = new InputStreamReader(ips);
		BufferedReader br = new BufferedReader(ipsr);
		int nbLigne = 0;
		String ligne;

		initStruct(d);

		String separatorCurrent = ";";
		String separatorNext = ",";
		LogListener.ecrireLogArea(screen, "Séparateur : " + separatorCurrent);

		while ((ligne=br.readLine())!=null){
			if(ligne == null || "".equals(ligne.trim()) || ligne.length() == 0){
				continue;
			}

			if(nbLigne == 0){
				++nbLigne;
				continue;
			}else{
				++nbLigne;
			}

			Map<String, String> list = new HashMap<String, String>();
			String[] chaine = ligne.split(separatorCurrent);

			if(chaine.length <= 1){
				String temp = separatorCurrent;
				separatorCurrent = separatorNext;
				separatorNext = temp;
				LogListener.ecrireLogArea(screen, "Changement de séparateur : " + separatorCurrent);
				chaine = ligne.split(separatorCurrent);
			}

			if("ONE".equals(step)){
				//compareValues(chaine.length, FileOneEnum.values().length, step, ligne);
				int i = 0;
				for(FileTwoEnum value : FileTwoEnum.values()){
					if(chaine.length <= i){
						list.put(value.name(), "");
					}else{
						list.put(value.name(), chaine[i++]);
					}
				}
				String codeAgence = list.get(FileTwoEnum.PDV.toString());
				if(codeAgence != null && !"".equals(codeAgence.trim())){
					d.getSource().put(codeAgence, list);
				}
			}else if("TWO".equals(step)){
				//compareValues(chaine.length, FileTwoEnum.values().length, step, ligne);
				int i = 0;
				for(FileOneEnum value : FileOneEnum.values()){
					if(chaine.length <= i){
						list.put(value.name(), "");
					}else{
						list.put(value.name(), chaine[i++]);
					}
				}
				String codeAgence = list.get(FileOneEnum.CODE_AGENCE.toString());
				Map<String, String> temp = d.getSource().get(codeAgence);
				if(temp == null || temp.isEmpty()){
					d.getRejet().add(codeAgence);
					continue;
				}

				Map<String, String> resultat = new HashMap<String, String>();
				resultat.putAll(list);
				resultat.putAll(temp);
				d.getData().add(resultat);
			}
		}
		if("ONE".equals(step)){
			LogListener.ecrireLogArea(screen, "Nombre de codes agences detectés : "+d.getSource().size());
		}
		if("TWO".equals(step)){
			LogListener.ecrireLogArea(screen, "Nombre de mails detectés 'valides' : "+d.getData().size());
			LogListener.ecrireLogArea(screen, "Nombre de mails detectés 'rejets' : "+(nbLigne - d.getData().size()));
		}

		if(d.getRejet().size() != 0){
			LogListener.ecrireLogArea(screen, "Ligne(s) manquante(s) dans le fichier principale des codes agences : "+d.getRejet().toString());
		}
		br.close(); 	
	}

	private static void initStruct(Data d) {
		if(d.getData() == null){
			List<Map<String, String>> map = new ArrayList<Map<String, String>>();
			d.setData(map);
		}

		if(d.getSource() == null){
			Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
			d.setSource(map);
		}


		if(d.getData() == null){
			List<Map<String, String>> map = new ArrayList<Map<String, String>>();
			d.setData(map);
		}

		if(d.getRejet() == null){
			d.setRejet(new TreeSet<String>());
		}
	}
}