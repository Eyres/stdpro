package app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.swing.JTextPane;

import org.apache.commons.lang3.StringUtils;

import enums.Extension;
import listeners.LogListener;

public class AlchemyToTiff {
	private static void remplirHashMap(String data, HashMap<String, String> nameToTitle, JTextPane screen) throws IOException{
		InputStream ips=new FileInputStream(data);
		InputStreamReader ipsr=new InputStreamReader(ips);
		BufferedReader br=new BufferedReader(ipsr);
		try{
			String l1, l2, l3;

			while(true){
				l1=br.readLine();
				if(l1 == null){break;}
				if(l1.trim() == ""){continue;}
				
				if(!l1.contains("Nom de fichier")){
					LogListener.ecrireLogArea(screen, "Le format de fichier est de la forme : (ligne 1 => Nom de fichier, ligne 2 => Titre du document, ligne 3 => vide)");
					continue;
				}

				l2=br.readLine();
				if(!l2.contains("Titre du document")){
					LogListener.ecrireLogArea(screen, "Le format de fichier est de la forme : (ligne 1 => Nom de fichier, ligne 2 => Titre du document, ligne 3 => vide)");
					continue;
				}

				l3=br.readLine();
				if(StringUtils.isNotBlank(l3)){
					LogListener.ecrireLogArea(screen, "Le format de fichier est de la forme : (ligne 1 => Nom de fichier, ligne 2 => Titre du document, ligne 3 => vide)");
					continue;
				}

				String[] s1 = l1.toUpperCase().split(":");
				String[] s2 = l2.toUpperCase().split(":");

				nameToTitle.put(s1[1].trim(), s2[1].trim());
			}
		} catch (Exception e){
			e.printStackTrace();
		}finally{
			br.close();
			ipsr.close();
			ips.close();
		}
	}

	private static String makeName(String fileName, boolean fileExist, int version, String prefixe, String suffixe, String extension, String path){
		String file      = removeExtension(fileName);
		if(fileExist){
			return path + "\\" + prefixe + file.trim().replaceAll(" ", "")+ suffixe +"_v" + version + "." + extension;
		}else{
			return path + "\\" + prefixe + file.trim().replaceAll(" ", "") +suffixe + "." + extension;
		}
	}

	private static String removeExtension(String fileName) {
		return fileName.split("\\.")[0];
	}

	private static String recupNameFile(File f){
		String path = f.getPath().trim();
		String [] newPath = path.split("\\\\");
		Integer lastInt = newPath.length-1;
		String lastStr = newPath[lastInt].split("-")[0].trim();
		return lastStr.toUpperCase();
	}

	private static Integer searchVersion(String fileName, String prefixe, String suffixe, String extension, String output){
		int version = 1;
		String nameFile = makeName(fileName, false, version, prefixe, suffixe, extension, output);
		while(new File(nameFile).exists() && !new File(nameFile).isDirectory()){
			version++;
			nameFile = makeName(fileName, true, version, prefixe, suffixe, extension, output);
		}
		return version;
	}

	private static void rename(String inputPath, String output, HashMap<String, String> nameToTitle, String prefixe, String suffixe, String extension, JTextPane screen){
		File input = new File(inputPath);
		File[] tousLesFichiers = input.listFiles();
		int j = 0;
		for(File f:tousLesFichiers){
			String i = nameToTitle.get(recupNameFile(f));
			if(i != null){
				if(new File(makeName(i, false, 0, prefixe, suffixe, extension, output)).exists() && !new File(makeName(i, false, 0, prefixe, suffixe, extension, output)).isDirectory()) {
					LogListener.ecrireLogArea(screen,"fichier existe deja : "+i);
					int version = searchVersion(i, prefixe, suffixe, extension, output);
					LogListener.ecrireLogArea(screen,"renomé : " + makeName(i, true, version, prefixe, suffixe, extension, output));
					f.renameTo(new File(makeName(i, true, version, prefixe, suffixe, extension, output)));
				}else{
					LogListener.ecrireLogArea(screen,"renomé : "+makeName(i, false, 0, prefixe, suffixe, extension, output));
					f.renameTo(new File(makeName(i, false, 0, prefixe, suffixe, extension, output)));
				}
				j++;
			}else{
				LogListener.ecrireLogArea(screen, "probleme renommage : "+recupNameFile(f) + " en " + i);
			}
		}
		LogListener.ecrireLogArea(screen, j+" fichiers modifiés");
	}

	public static void traitement(JTextPane screen){
		try {
			String input = AppUtils.chooseDirectoryPdf();
			String output = AppUtils.chooseDirectoryPdf();
			String data = AppUtils.chooseFilePdf(Extension.DAT.toString());

			String prefixe = AppUtils.entreeUtilisateur("Application", "Veuillez saisir un préfixe de renommage (exemple : BA_)");
			String suffixe = AppUtils.entreeUtilisateur("Application", "Veuillez saisir un suffixe de renommage");
			String extension = Extension.TIFF.toString();

			HashMap<String, String> nameToTitle = new HashMap<String, String>();

			remplirHashMap(data, nameToTitle, screen);
			rename(input, output, nameToTitle, prefixe, suffixe, extension, screen);
		}catch(Exception e){
			LogListener.ecrireLogArea(screen, e.toString());
		}
	}
}