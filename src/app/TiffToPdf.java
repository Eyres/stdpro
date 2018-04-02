package app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JTextPane;

import org.apache.commons.lang3.StringUtils;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;

import enums.Extension;
import listeners.LogListener;

public class TiffToPdf {
	@SuppressWarnings("deprecation")
	private static void Tiff2Pdf(String tifPath, String pdfPath, JTextPane screen) throws IOException {
		String imgeFilename = tifPath;
		Document document = new Document();
		PdfWriter writer = null;
		RandomAccessFileOrArray ra = null;
		Image image;

		if(!imgeFilename.toUpperCase().endsWith(Extension.TIFF.name()) && !imgeFilename.toUpperCase().endsWith(Extension.TIF.name())){
			LogListener.ecrireLogArea(screen, "Le fichier " + imgeFilename + " a été ignoré (raison : extension incorrect)");
			return;
		}

		try {
			writer = PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
			writer.setStrictImageSequence(true);
			document.open();
			ra = new RandomAccessFileOrArray(imgeFilename);
			int pagesTif = TiffImage.getNumberOfPages(ra);
			for (int i = 1; i <= pagesTif; i++) {
				image = TiffImage.getTiffImage(ra, i);
				image.scaleAbsolute(PageSize.A4);
				document.setMargins(0, 0, 0, 0);
				document.setPageSize(PageSize.A4);
				document.newPage();
				document.add(image);
			}
			new File(pdfPath);
			LogListener.ecrireLogArea(screen, "Création du fichier " + pdfPath + " réussit avec succès");
		} catch (Exception e) {
			LogListener.ecrireLogArea(screen, "Le fichier " + pdfPath + " n'a pas pu être crée");
			e.printStackTrace();
		} finally {
			document.close();
			ra.close();
			writer.close();
		}
	}

	private static String removeExtension(String fileName) {
		return fileName.split("\\.")[0];
	}

	public static void traitement(JTextPane screen){
		try{
			String inputPath = AppUtils.chooseDirectoryPdf();
			String outputPath = AppUtils.chooseDirectoryPdf();
			String prefixe = AppUtils.entreeUtilisateur("Application", "Veuillez saisir un préfixe de renommage (exemple : BA_)");
			String suffixe = AppUtils.entreeUtilisateur("Application", "Veuillez saisir un suffixe de renommage");

			File input = new File(inputPath);
			File[] tousLesFichiers = input.listFiles();
			for(File f:tousLesFichiers){
				String nomTiff = inputPath + "\\" + f.getName();
				String nomPdf  = outputPath + "\\" + (StringUtils.isNotBlank(prefixe) ? prefixe.trim() : "") + removeExtension(f.getName()) + (StringUtils.isNotBlank(suffixe) ? suffixe.trim() : "") + "." + Extension.PDF.toString();
				Tiff2Pdf(nomTiff, nomPdf, screen);
			}
		}catch(Exception e){
			LogListener.ecrireLogArea(screen, e.toString());
		}
	}
}
