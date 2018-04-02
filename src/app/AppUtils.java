package app;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.FilteredTextRenderListener;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.RegionTextRenderFilter;

public class AppUtils {
	public static String clean(String text){
	    text = text.replaceAll("\\.+",".");
	    text = text.replaceAll(" +"," ");
	    text = text.replaceAll(",+",",");
	    return text;
	}
	
	public static String millisToDate(long begin, long end){
		java.util.Date uDate = new java.util.Date (begin); //Relever l'heure avant le debut du progamme (en milliseconde) 
		Date dateFin = new Date (end); //Relever l'heure a la fin du progamme (en milliseconde) 
		Date duree = new Date (System.currentTimeMillis()); //Pour calculer la différence
		duree.setTime (dateFin.getTime () - uDate.getTime ());  //Calcul de la différence
		long secondes = duree.getTime () / 1000;
		long min = secondes / 60;
		long heures = min / 60;
		//long mili = duree.getTime () % 1000;
		secondes %= 60;
		return heures + " heure(s) " + min + " min " + secondes + " sec";
	}

	public static String chooseFilePdf(String extension) throws Exception{
		JFileChooser chooser = new JFileChooser();
		chooser.setApproveButtonText("Choix du fichier "+extension+"...");
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
			String path = chooser.getSelectedFile().getAbsolutePath().toString();
			if(StringUtils.isNotBlank(extension)){
				if(!path.toUpperCase().endsWith(extension)){
					throw new Exception("Le fichier choisit n'est pas un fichier "+extension);
				}
			}
			return path;
		}else{
			throw new Exception("Probleme technique lors de la sélection du fichier PDF");
		}
	}

	public static String chooseDirectoryPdf() throws Exception{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		chooser.setApproveButtonText("Choix du dossier...");
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
			File path = chooser.getSelectedFile();
			if(!path.isDirectory()){
				throw new Exception("Le dossier choisit est invalide");
			}
			return path.getAbsolutePath();
		}else{
			throw new Exception("Probleme technique lors de la sélection du fichier PDF");
		}
	}

	public static Integer nbPagesPdf(String pdf) throws IOException{
		PdfReader p = new PdfReader(pdf);
		Integer retur = p.getNumberOfPages();
		p.close();
		return retur;
	}

	//on part d'en bas a gauche (full : 0 0 595 842)
	public static String getTextPdfArea(PdfReader reader, int numeroPage, int x, int y, int largeur, int hauteur) throws IOException{
		Rectangle r = new Rectangle(x, y, largeur, hauteur);
		String text = PdfTextExtractor.getTextFromPage(reader, numeroPage, 
				new FilteredTextRenderListener(
						new LocationTextExtractionStrategy(), 
						new RegionTextRenderFilter(r)
						)
				);
		return text;
	}

	public static String getTextPdf(PdfReader reader, int numeroPage) throws IOException{
		return PdfTextExtractor.getTextFromPage(reader, numeroPage, new FilteredTextRenderListener(new LocationTextExtractionStrategy()));
	}

	public static String entreeUtilisateur(String titre, String message) {
		return JOptionPane.showInputDialog(null, message, titre, JOptionPane.QUESTION_MESSAGE);
	}

	//	public static void pdfToImage(String pathPdf, String dest, String format) throws IOException{
	//		File pdfFile = new File(pathPdf);
	//		RandomAccessFile raf = new RandomAccessFile(pdfFile, "r");
	//		FileChannel channel = raf.getChannel();
	//		ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
	//		PDFFile pdf = new PDFFile(buf);
	//		PDFPage page = pdf.getPage(0);
	//
	//		// create the image
	//		java.awt.Rectangle rect = new java.awt.Rectangle(0, 0, (int) page.getBBox().getWidth(),
	//				(int) page.getBBox().getHeight());
	//		BufferedImage bufferedImage = new BufferedImage(rect.width, rect.height,
	//				BufferedImage.TYPE_INT_RGB);
	//
	//		Image image = page.getImage(rect.width, rect.height,    // width & height
	//				rect,                       // clip rect
	//				null,                       // null for the ImageObserver
	//				true,                       // fill background with white
	//				true                        // block until drawing is done
	//				);
	//		Graphics2D bufImageGraphics = bufferedImage.createGraphics();
	//		bufImageGraphics.drawImage(image, 0, 0, null);
	//		ImageIO.write(bufferedImage, format, new File( dest ));
	//	}

	public static void pdfToImage(String pathPdf, String dest, Integer DPI, Integer page) throws IOException{
		PDDocument document = PDDocument.load(new File(pathPdf));
		PDFRenderer pdfRenderer = new PDFRenderer(document);
		BufferedImage bim = pdfRenderer.renderImageWithDPI(page-1, DPI, ImageType.RGB);
		ImageIOUtil.writeImage(bim, dest, DPI);
		document.close();
	}
}
