package app;

import java.util.Date;

import javax.swing.JTextPane;

import app.parcourspdf.ParcoursPDF;
import listeners.LogListener;

public class ComptagePDF {
    public static void traitement(JTextPane screen){
        LogListener.ecrireLogArea(screen, "DEBUT DU COMPTAGE DES PDFS...");
        String path;
        Integer[] result = {0, 0};
        try {
            path = AppUtils.chooseDirectoryPdf();
            ParcoursPDF.listDirectory(screen, path, "", result);
        } catch (Exception e) {
            LogListener.ecrireLogArea(screen, e.getMessage());
        }

        LogListener.ecrireLogArea(screen, "------------------------------------------------------------");
        LogListener.ecrireLogArea(screen, new Date() + " : nombres pdf totals : [" + result[0] + "]");
        LogListener.ecrireLogArea(screen, new Date() + " : nombres pages totals : [" + result[1] + "]");
        LogListener.ecrireLogArea(screen, "------------------------------------------------------------");

        LogListener.ecrireLogArea(screen, "FIN DU COMPTAGE DES PDFS...");
    }
}
