package fichier;

import java.io.File;

import enums.Parametrage;

public class PathResultat {
	public static String makePathResult(String pathused){
		if(new File(pathused).exists()){
			return pathused + "\\";
		}
		
		String current = "";
		File f = new File(Parametrage.DISQUE_X.getValue());
		if(f.exists() && f.isDirectory()){
			current += Parametrage.DISQUE_X.getValue();
		}else{
			current += Parametrage.DISQUE_C.getValue();
		}
		
		current += Parametrage.RESULTAT.getValue();
		f = new File(current);
		if(!f.exists() || !f.isDirectory()){
			f.mkdir();
		}
		return current;
	}
}
