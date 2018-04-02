package app.parcourspdf;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.LongAdder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JTextPane;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.im4java.core.IM4JavaException;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import app.AppUtils;
import enums.Extension;
import listeners.LogListener;
import myApp.Main;
import net.sourceforge.tess4j.util.PdfUtilities;

public class ParcoursPDF {
	static String path = null;
	static String endLine ="\n";
	static double percentW = 21;//cm
	static double percentH = 29.7;//cm (old:14)
	static double largeur = (percentW/2.54);//A4 : 21
	static double hauteur = (percentH/2.54);//A4 : 29.7

	public static void listDirectory(JTextPane screen, String parentDir, String currentDir, Integer[] result) throws IOException, SQLException {
		String dirToList = parentDir;
		if (!currentDir.equals("")) {
			dirToList += File.separator + currentDir;
		}

		File f =  new File(dirToList); 
		File[] subFiles = f.listFiles();
		if (subFiles != null && subFiles.length > 0) {
			for (File aFile : subFiles) {
				String currentFileName = aFile.getName();
				if (currentFileName.equals(".") || currentFileName.equals("..")) {
					continue;
				}
				if (aFile.isDirectory()) {
					LogListener.ecrireLogArea(screen, new Date() + " : Dossier : [" + dirToList + File.separator + currentFileName + "]");
					ParcoursPDF.listDirectory(screen, dirToList, currentFileName, result);
				} else if(currentFileName.toUpperCase().endsWith(Extension.PDF.name())){
					LogListener.ecrireLogArea(screen, new Date() + " : Fichier : [" + currentFileName + "]");
					result[0]+= 1;
					Integer nbTemp = AppUtils.nbPagesPdf(dirToList + File.separator + currentFileName);
					LogListener.ecrireLogArea(screen, new Date() + " : Nombres de pages : [" + nbTemp + "]");
					result[1]+=nbTemp;
				}
			}
		}
	}

	public static void renameDirectory(JTextPane screen, String path, String regex, String prefixe, String suffixe) throws Exception {
		if(StringUtils.isBlank(path)){
			LogListener.ecrireLogArea(screen, new Date() + " : Le chemin d'acces d'initialisation est vide");
			return;
		}

		String currentPath = path.endsWith("\\") ? path : path + File.separator;
		HashMap<String, String> rename = new HashMap<String, String>();
		remplirMapRename(path, rename, currentPath, screen, regex, prefixe, suffixe);
		System.gc();

		for(Entry<String, String>temp : rename.entrySet()){
			if(!new File(temp.getValue()).exists()){
				new File(temp.getKey()).renameTo(new File(temp.getValue()));
				LogListener.ecrireLogArea(screen, new Date() + " : Copie réussie ("+temp.getKey()+" -> "+temp.getValue()+")");
			}else{
				LogListener.ecrireLogArea(screen, new Date() + " : Echec de la Copie (Doublon) ("+temp.getKey()+" -> "+temp.getValue()+")");
			}
		}
	}

	private static void remplirMapRename(String racine, HashMap<String, String> rename, String currentPath, JTextPane screen, String regex, String prefixe, String suffixe) throws Exception{
		File[] subFiles = new File(racine).listFiles();

		final LongAdder totalErrors = new LongAdder();
		final LongAdder traite = new LongAdder();

		LogListener.ecrireLogArea(screen, String.format("Available processors: %d", Runtime.getRuntime().availableProcessors()));
		LogListener.ecrireLogArea(screen, String.format("Il y a %d TIF/TIFF/PDF fichiers à OCR en parallèle.", subFiles.length));

		path = makeLibPath(System.getProperty("user.dir"), screen);
		long time = System.currentTimeMillis();
		if (subFiles != null && subFiles.length > 0) {
			runRun(subFiles, screen, regex, currentPath, prefixe, suffixe, rename, totalErrors, traite);
		}
		LogListener.ecrireLogArea(screen, new Date() + " : Durée Totale du traitement : " + AppUtils.millisToDate(time, System.currentTimeMillis()));
		LogListener.ecrireLogArea(screen, new Date() + " : "+traite+" Fichier traités");
		LogListener.ecrireLogArea(screen, new Date() + " : "+totalErrors+" Fichier en erreurs");
	}

	private static void runRun(File[] subFiles, JTextPane screen, String regex, String currentPath, String prefixe, String suffixe, HashMap<String, String> rename, LongAdder totalErrors, LongAdder traite){
		Arrays.stream(subFiles).filter((aFile) -> filterFile(aFile)).parallel().forEach((aFile) -> LogListener.ecrireLogArea(screen, runFile(screen, aFile, regex, currentPath, prefixe, suffixe, rename, totalErrors, traite)));
	}

	private static boolean filterFile(File aFile){
		if(aFile.isDirectory()){
			return false;
		}
		String name = FilenameUtils.getName(aFile.getAbsolutePath());
		if(name.toUpperCase().endsWith(Extension.PDF.name())){
			return true;
		}else if(name.toUpperCase().endsWith(Extension.TIF.name())){
			return true;
		}else if(name.toUpperCase().endsWith(Extension.TIFF.name())){
			return true;
		}else{
			return false;
		}
	}

	private static String runFile (JTextPane screen, File aFile, String regex, String currentPath, String prefixe, String suffixe, HashMap<String, String> rename, LongAdder totalErrors, LongAdder traite){
		String currentName = FilenameUtils.getName(aFile.getAbsolutePath());
		String full = currentPath + currentName;
		//final LongAdder errorsInCurrentRun = new LongAdder();
		MyTesseract localTesseract = initOCRTesseract1(screen);
		String outScreen = "";
		String[] arrayOCR;
		try {
			arrayOCR = makeOCR(screen, currentName, full, localTesseract, totalErrors, outScreen);
			if(StringUtils.isBlank(arrayOCR[0])){
				return arrayOCR[1];
			}
			String extension = currentName.toUpperCase().endsWith(Extension.PDF.name()) ? Extension.PDF.toString() : Extension.TIFF.toString();
			arrayOCR[1] +=matchOCR(regex, arrayOCR[0], prefixe, suffixe, totalErrors, currentName, full, extension, rename, aFile);
			traite.increment();
			return arrayOCR[1];
		} catch (Exception e) {
			e.printStackTrace();
			totalErrors.increment();
		}
		return outScreen;
	}

	private static String matchOCR(String regex, String text, String prefixe, String suffixe, LongAdder errorsInCurrentRun, String currentName, String full, String extension, HashMap<String, String> rename, File aFile) {
		Pattern pattern =   Pattern.compile(regex, Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CHARACTER_CLASS);
		Matcher matcher =   pattern.matcher(text);
		String chaine = "";

		if(matcher.find()){ 
			chaine = (StringUtils.isNotBlank(prefixe) ? prefixe : "") + matcher.group().trim() + (StringUtils.isNotBlank(suffixe) ? suffixe : "");
		}

		if(StringUtils.isBlank(chaine)){
			errorsInCurrentRun.increment();
			return (errorsInCurrentRun.intValue()) + " Fichier en erreurs : Impossible de renommer le fichier : [" + currentName + "] : value regex not found"+endLine;
		}

		File newFile = new File(full.replace(currentName, chaine) + "." + extension);
		rename.put(aFile.getPath().toString(), newFile.getPath().toString());

		newFile = null;
		aFile = null;
		return "";
	}

	private static MyTesseract initOCRTesseract1(JTextPane screen){
		MyTesseract localTesseract = new MyTesseract();
		if(path != null && !path.contains("tessdata")){
			path += "\\tessdata";
		}
		localTesseract.setDatapath(path);
		//localTesseract.setConfigs(Arrays.asList("digits"));
		localTesseract.setLanguage("fra");
		localTesseract.setTessVariable("load_system_dawg", "F");
		localTesseract.setTessVariable("load_freq_dawg", "F");
		localTesseract.setTessVariable("tessedit_char_whitelist", "ASTFR0123456789-.");
		//localTesseract.setTessBaseAPISetPageSegMode(TessPageSegMode.PSM_AUTO);

		//localTesseract.setTessVariable("classify_bln_numeric_mode", "1");
		localTesseract.setTessVariable("tessedit_create_hocr", "1");
		localTesseract.setTessVariable("tessedit_zero_rejection", "T");
		//localTesseract.setTessVariable("tessedit_pageseg_mode", "1");

		localTesseract.init();
		localTesseract.setTessVariables();
		return localTesseract;
	}

	private static String[] makeOCR(JTextPane screen, String currentName, String full, MyTesseract localTesseract, LongAdder errorsInCurrentRun, String outScreen) throws Exception{
		long time = System.currentTimeMillis();

		File f = makeFile(currentName, full);
		String tempName = f.getAbsolutePath();

		try{
			outScreen+= new Date() + " : Traitement du fichier " + currentName + endLine;
			//BufferedImage bimg = ImageIO.read(f);
			int dpi = getDpi(f);
			int tempL = (int)(largeur*dpi);
			int tempH = (int)(hauteur*dpi);

			outScreen+= new Date() + " : DPI : [" + dpi +"]"+endLine;
			//outScreen+= new Date() + " : size File : [" + bimg.getWidth() + "||"+bimg.getHeight()+"]"+endLine;
			outScreen+= new Date() + " : size Utile : [" + tempL + "||"+tempH+"]"+endLine;
			String text = localTesseract.doOCR(OptimisationMagic(f.getAbsolutePath()));

			if(currentName.toUpperCase().endsWith(Extension.PDF.name())){
				if(!f.delete()){
					outScreen+= new Date() + " : Impossible de supprimer le fichier : [" + tempName + "]"+endLine;
				}else{
					outScreen+= new Date() + " : Suppression du fichier : [" + tempName + "]"+endLine;
				}
			}
			f = null;
			outScreen+= new Date() + " : Durée du traitement : " + AppUtils.millisToDate(time, System.currentTimeMillis())+endLine;
			return new String[] {AppUtils.clean(text), outScreen};
		}catch(UnsatisfiedLinkError e){
			outScreen+= new Date() + " : Probleme de librairie : veuillez élever les droits d'administration"+endLine;
			outScreen+= new Date() + " : " + e.getMessage()+endLine;
			LogListener.ecrireLogArea(screen, new Date() + outScreen);
			System.exit(0);
		}catch(NullPointerException e){
			errorsInCurrentRun.increment();
			outScreen+= (errorsInCurrentRun.intValue()) + " Fichier en erreurs : erreur de lecture du fichier : " + e.getMessage()+endLine;
			return new String[] {"", outScreen};
		}
		return new String[] {"", outScreen};
	}

	private static BufferedImage OptimisationMagic(String filename) throws IOException, InterruptedException, IM4JavaException {
		Mat image = Imgcodecs.imread(filename);
		MatOfByte matOfByte = new MatOfByte();

		Mat out = new Mat(image.size(), CvType.CV_8UC1);
		Imgproc.equalizeHist(out, out);
		Imgproc.cvtColor(image, out, Imgproc.COLOR_BGR2GRAY);
		Imgcodecs.imencode(".png", out, matOfByte);

		image.release();
		byte[] b = matOfByte.toArray();

		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		BufferedImage img = ImageIO.read(bais);
		bais.close();
		return img;
	}

	private static synchronized File makeFile(String currentName, String full) throws IOException{
		if(currentName.toUpperCase().endsWith(Extension.PDF.name())){
			return PdfUtilities.convertPdf2Tiff(new File(full));
		}else{
			return new File(full);
		}
	}

	private static synchronized int getDpi(File f) throws IOException, ImageReadException{
		return Imaging.getImageInfo(f).getPhysicalWidthDpi();
	}

	private static String makeLibPath(String dir, JTextPane screen) {
		try{
			File directory = new File(dir + File.separator + "tessdata");
			boolean bits32 = "32".equals(System.getProperty("sun.arch.data.model"));
			String libPath = null;
			String sysPath = null;

			if(!directory.exists()){
				LogListener.ecrireLogArea(screen, new Date() + " Création des librairies dynamiques dans le dossier " + dir);
				directory.mkdir();
			}

			InputStream lgSrc = Main.class.getResourceAsStream("tessdata/fra.traineddata");
			InputStream convertSrc = Main.class.getResourceAsStream("tessdata/pdf.ttf");

			String trained = dir + "/tessdata/fra.traineddata";
			Path lgDest = Paths.get(trained);

			if(!new File(trained).exists()){
				Files.copy(lgSrc, lgDest);
				LogListener.ecrireLogArea(screen, new Date() + " Création du fichier " + trained);
			}

			trained = dir + "/tessdata/pdf.ttf";
			Path convertDest = Paths.get(dir + "/tessdata/pdf.ttf");
			if(!new File(trained).exists()){
				Files.copy(convertSrc, convertDest);
				LogListener.ecrireLogArea(screen, new Date() + " Création du fichier " + trained);
			}

			LogListener.ecrireLogArea(screen, new Date() + " Architecture " + (bits32 ? "32":"64" ) +" bits détectée\n");
			if(bits32){
				sysPath = "win32-x86";
				libPath = "C:"+File.separator+"Windows"+File.separator+"SysWOW64";

				if(!new File(libPath).exists()){
					libPath = "C:"+File.separator+"Windows"+File.separator+"System32";
				}

				String name = "opencv_java320.dll";
				InputStream in = Main.class.getResourceAsStream("opencv/"+sysPath+"/"+name);
				File f = new File(libPath+File.separator+name);

				if(!f.exists()){
					LogListener.ecrireLogArea(screen, new Date() + " Création du fichier " + name + " dans le dossier " + libPath);
					Files.copy(in, Paths.get(f.getAbsolutePath()));
				}

				name = "gsdll32.dll";
				in = Main.class.getResourceAsStream("tessdata/"+sysPath+"/"+name);
				f = new File(libPath+File.separator+name);

				if(!f.exists()){
					LogListener.ecrireLogArea(screen, new Date() + " Création du fichier " + name + " dans le dossier " + libPath);
					Files.copy(in, Paths.get(f.getAbsolutePath()));
				}

				name = "liblept173.dll";
				in = Main.class.getResourceAsStream("tessdata/"+sysPath+"/"+name);
				f = new File(libPath+File.separator+name);

				if(!f.exists()){
					LogListener.ecrireLogArea(screen, new Date() + " Création du fichier " + name + " dans le dossier " + libPath);
					Files.copy(in, Paths.get(f.getAbsolutePath()));
				}

				name = "libtesseract304.dll";
				in = Main.class.getResourceAsStream("tessdata/"+sysPath+"/"+name);
				f = new File(libPath+File.separator+name);

				if(!f.exists()){
					LogListener.ecrireLogArea(screen, new Date() + " Création du fichier " + name + " dans le dossier " + libPath);
					Files.copy(in, Paths.get(f.getAbsolutePath()));
				}
			}else{
				sysPath = "win32-x86-64";
				libPath = "C:"+File.separator+"Windows"+File.separator+"System32";

				String name = "opencv_java320.dll";
				InputStream in = Main.class.getResourceAsStream("opencv/"+sysPath+"/"+name);
				File f = new File(libPath+File.separator+name);

				if(!f.exists()){
					LogListener.ecrireLogArea(screen, new Date() + " Création du fichier " + name + " dans le dossier " + libPath);
					Files.copy(in, Paths.get(f.getAbsolutePath()));
				}

				name = "gsdll64.dll";
				in = Main.class.getResourceAsStream("tessdata/"+sysPath+"/"+name);
				f = new File(libPath+File.separator+name);

				if(!f.exists()){
					LogListener.ecrireLogArea(screen, new Date() + " Création du fichier " + name + " dans le dossier " + libPath);
					Files.copy(in, Paths.get(f.getAbsolutePath()));
				}

				name = "liblept173.dll";
				in = Main.class.getResourceAsStream("tessdata/"+sysPath+"/"+name);
				f = new File(libPath+File.separator+name);

				if(!f.exists()){
					LogListener.ecrireLogArea(screen, new Date() + " Création du fichier " + name + " dans le dossier " + libPath);
					Files.copy(in, Paths.get(f.getAbsolutePath()));
				}

				name = "libtesseract304.dll";
				in = Main.class.getResourceAsStream("tessdata/"+sysPath+"/"+name);
				f = new File(libPath+File.separator+name);

				if(!f.exists()){
					LogListener.ecrireLogArea(screen, new Date() + " Création du fichier " + name + " dans le dossier " + libPath);
					Files.copy(in, Paths.get(f.getAbsolutePath()));
				}
			}
			System.setProperty("jna.library.path", libPath);
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		}catch(Exception e){
			LogListener.ecrireLogArea(screen, new Date() + " : " + e.getMessage() +" en erreur");
		}
		return dir;
	}
}

