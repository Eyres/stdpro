package app;

import javax.swing.JTextPane;

import listeners.LogListener;
import app.parcourspdf.ParcoursPDF;

public class RenamePDF {
    public static void traitement(JTextPane screen){
        LogListener.ecrireLogArea(screen, "DEBUT DU RENOMAGE DES PDFS...");
        try {
            String regex = AppUtils.entreeUtilisateur("Application", "Veuillez saisir une valeur de rechercher (\\d{QUANTITE} pour les chiffres)");
            String path = AppUtils.chooseDirectoryPdf();
			String prefixe = AppUtils.entreeUtilisateur("Application", "Veuillez saisir un préfixe de renommage (exemple : BA_)");
			String suffixe = AppUtils.entreeUtilisateur("Application", "Veuillez saisir un suffixe de renommage");
			
            //ParcoursPDF.renameDirectory(screen, path, regex + "\\b", prefixe, suffixe);
			ParcoursPDF.renameDirectory(screen, path, "\\b"+regex+"\\b", prefixe, suffixe);
        } catch (Exception e) {
            LogListener.ecrireLogArea(screen, e.getMessage());
            e.printStackTrace();
        }
        LogListener.ecrireLogArea(screen, "FIN DU RENOMAGE DES PDFS...");
    }
}
