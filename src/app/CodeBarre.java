package app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JTextPane;

import listeners.LogListener;
import myApp.Main;
import transformation.ExcelToCSV;
import app.codebarre.Data;
import app.codebarre.ReadFile;
import app.codebarre.ResultEnum;
import enums.Extension;
import fichier.PathResultat;
import fichier.WriteFile;

public class CodeBarre {
	
    private static final String pathUsed = "\\\\stdpro1\\BaseAdhesion-Portefeuille";
    private static final String repertoire = PathResultat.makePathResult(pathUsed);
    private static final String path = repertoire + "test.csv";
    
    public static void traitement(JTextPane screen){
        try{
            Data d = new Data();
            choixFileOne(screen, d);
            RemplirData(screen, d);
            EcrireEnTete(screen);
            EcrireResultat(screen, d);
        }catch(Exception e){
            LogListener.ecrireLogArea(screen, e.toString());
        }
    }

    private static void EcrireEnTete(JTextPane screen) throws IOException {
        LogListener.ecrireLogArea(screen, "ECRITURE DE L'ENTETE...");
        File f = new File(path);
        if(!f.exists()) { 
            for(ResultEnum value : ResultEnum.values()){
                WriteFile.ecrire(path, value.toString());
                WriteFile.ecrire(path,";");
            }
            WriteFile.ecrire(path, System.getProperty("line.separator" ));
        }
        LogListener.ecrireLogArea(screen, "FIN ECRITURE DE L'ENTETE...");
    }

    private static void EcrireResultat(JTextPane screen, Data d) throws IOException {
        LogListener.ecrireLogArea(screen, "ECRITURE DES RESULTATS EN COURS ....");
        for(Map<String, String> entryFinal : d.getData()){
            String mapValue = "";
            for(ResultEnum value : ResultEnum.values()){
            	String temp = entryFinal.get(value.toString());
                if(temp == null) temp = "";
                mapValue+=temp+";";
            }
            WriteFile.ecrire(path, mapValue+System.getProperty("line.separator" ));
        }
        LogListener.ecrireLogArea(screen, "FIN DE L ECRITURE DES RESULTATS...");
    }

    private static void RemplirData(JTextPane screen, Data d) throws Exception {
        LogListener.ecrireLogArea(screen, "REMPLISSAGE DES DONNEES EN COURS ...");
        ReadFile.readFile(screen, d, Main.class.getResourceAsStream("pdv.csv"), "ONE");
        ReadFile.readFile(screen, d, new FileInputStream(d.getPathFileOne()), "TWO");
        LogListener.ecrireLogArea(screen, "FIN REMPLISSAGE DES DONNEES...");
    }

    private static void choixFileOne(JTextPane screen, Data d) throws Exception{
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(pathUsed));
        chooser.setApproveButtonText("Fichier d'adresse");
        
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
            String pathA = chooser.getSelectedFile().getAbsolutePath().toString();
            if(pathA.toUpperCase().endsWith(Extension.CSV.name())){
                d.setPathFileOne(pathA);
            }else if(pathA.toUpperCase().endsWith(Extension.XLS.name())){
                LogListener.ecrireLogArea(screen, "FICHIER XLS DETECTE : DEBUT DE LA CONVERSION...");
                String pathB = pathA.toUpperCase().replace(Extension.XLS.name(), Extension.CSV.name()).toLowerCase();
                ExcelToCSV.convertToXls(new File(pathA), new File(pathB));
                LogListener.ecrireLogArea(screen, "FICHIER XLS DETECTE : FIN DE LA CONVERSION...");
                pathA = pathB;
            }else if(pathA.toUpperCase().endsWith(Extension.XLSX.name())){
                String pathB = pathA.toUpperCase().replace(Extension.XLSX.name(), Extension.CSV.name()).toLowerCase();
                LogListener.ecrireLogArea(screen, "FICHIER XLSX DETECTE : DEBUT DE LA CONVERSION...");
                ExcelToCSV.convertToXlsx(new File(pathA), new File(pathB));
                LogListener.ecrireLogArea(screen, "FICHIER XLSX DETECTE : FIN DE LA CONVERSION...");
                pathA = pathB;
            }else{
                throw new Exception("Probleme d'extension du fichier d'adresse");
            }
            d.setPathFileOne(pathA);
        }else{
            throw new Exception("Probleme technique lors de la sélection du fichier d'adresse");
        }
    }
}
