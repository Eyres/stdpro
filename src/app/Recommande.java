package app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JTextPane;

import listeners.LogListener;

import org.apache.commons.lang3.StringUtils;

import transformation.ExcelToCSV;
import app.recommande.ResultEnum;

import com.itextpdf.text.pdf.PdfReader;

import enums.Civilite;
import enums.Extension;
import enums.TypeDeVoie;
import fichier.PathResultat;
import fichier.WriteFile;

public class Recommande {
	
    private static final String pathUsed = "\\\\stdpro1\\Recommande";
    private static final String repertoire = PathResultat.makePathResult(pathUsed);
    /**
     * Parses a PDF to a plain text file.
     * @param screen 
     * @param pdf the original PDF
     * @param txt the resulting text
     * @throws IOException
     */
    public static void traitement(JTextPane screen) {
        try{
            String pathA = chooseFilePdf();
            String[] array = pathA.split(File.separator+File.separator);
            String[] newFile = array[array.length-1].split("\\.");
            String extension = newFile[1].toUpperCase().replace(Extension.PDF.name(), Extension.CSV.name()).toLowerCase();
            String pathB = repertoire + newFile[0] + "." + extension;
            LogListener.ecrireLogArea(screen, "DEBUT DE LA LECTURE DU PDF ET DE SA CONVERTION : "+pathA);
            parsePdf(screen, pathA, pathB);
            LogListener.ecrireLogArea(screen, "FIN DE LA LECTURE DU PDF ET DE SA CONVERTION : "+ pathB);
        }catch(Exception e){
            e.printStackTrace();
            LogListener.ecrireLogArea(screen, e.toString());
        }
    }

    private static String chooseFilePdf() throws Exception{
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(pathUsed));
        chooser.setApproveButtonText("Choix du fichier PDF...");
        
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
            String path = chooser.getSelectedFile().getAbsolutePath().toString();
            if(!path.toUpperCase().endsWith(Extension.PDF.name())){
                throw new Exception("Le fichier choisit n'est pas un fichier PDF");
            }
            return path;
        }else{
            throw new Exception("Probleme technique lors de la sélection du fichier PDF");
        }
    }

    public static void parsePdf(JTextPane screen, String pdf, String csv) {
        PdfReader reader = null;
        PrintWriter out = null;
        int i = 1;

        try{
            reader = new PdfReader(pdf);
            out = new PrintWriter(new FileOutputStream(csv));
            EcrireEnTete(screen, csv);

            for (;i <= reader.getNumberOfPages(); i++) {
                Map<String, String> result = new HashMap<String, String>();
                String text = AppUtils.getTextPdfArea(reader, i, 0, 602, 595, 842);

                String[] adresse = text.trim().split("\n");

                if(adresse.length != 3 && adresse.length != 4) continue;

                if(adresse.length == 3){
                    if(!troisLignesDetectees(result, adresse, screen, text)) continue;
                }else if(adresse.length == 4){
                    if(!quatreLignesDetectees(result, adresse, screen, text)) continue;
                }

                for(ResultEnum value : ResultEnum.values()){
                    WriteFile.ecrire(csv, result.get(value.name()) == null ? "" : result.get(value.name()));
                    WriteFile.ecrire(csv, ";");
                }
                WriteFile.ecrire(csv, "\n");
            }
        }catch(Exception e){
            LogListener.ecrireLogArea(screen, "Echec de lecture de la page : " + i +" pour cause : "+ e.toString());
        }finally{
            if(out != null){
                out.flush();
                out.close();
            }
            if(reader != null){
                reader.close();
            }
        }
    }

    private static boolean troisLignesDetectees(Map<String, String> result, String[] adresse, JTextPane screen, String text) throws URISyntaxException{
        if(!searchCivilite(result, adresse[0].trim())){
            LogListener.ecrireLogArea(screen, "erreur sur la civilite : " + text.replace("\n", ";"));
            return false;
        }
        if(!searchAdresse(result, adresse[1].trim())){
            LogListener.ecrireLogArea(screen, "erreur sur l'adresse : " + text.replace("\n", ";"));
            return false;
        }
        if(!searchCity(result, adresse[2].trim())){
            LogListener.ecrireLogArea(screen, "erreur sur la ville : " + text.replace("\n", ";"));
            return false;
        }
        return true;
    }

    private static boolean quatreLignesDetectees(Map<String, String> result, String[] adresse, JTextPane screen, String text) throws URISyntaxException{
        if(!searchCivilite(result, adresse[0].trim())){
            LogListener.ecrireLogArea(screen, "erreur sur la civilite : " + adresse.toString());
            return false;
        }
        if(searchAdresse(result, adresse[1].trim())){
            result.put(ResultEnum.BATIMENT_IMMEUBLE.name(), adresse[2].trim());
        }else{
            if(!searchAdresse(result, adresse[2].trim())){
                LogListener.ecrireLogArea(screen, "erreur sur l'adresse : " + text.replace("\n", ";"));
                return false;
            }
            result.put(ResultEnum.BATIMENT_IMMEUBLE.name(), adresse[1].trim());
        }
        if(!searchCity(result, adresse[3].trim())){
            LogListener.ecrireLogArea(screen, "erreur sur la ville : " + text.replace("\n", ";"));
            return false;
        } 
        return true;
    }

    private static void EcrireEnTete(JTextPane screen, String path) throws IOException {
        LogListener.ecrireLogArea(screen, "ECRITURE DE L'ENTETE...");
        for(ResultEnum value : ResultEnum.values()){
            WriteFile.ecrire(path, value.toString());
            WriteFile.ecrire(path,";");
        }
        WriteFile.ecrire(path, "\n");
        LogListener.ecrireLogArea(screen, "FIN ECRITURE DE L'ENTETE...");
    }

    public static boolean searchAdresse(Map<String, String> result, String adresse) throws URISyntaxException {
        for(String typeVoie : TypeDeVoie.getTypeVoie()){
            String r = "((([0-9]*)|([0-9]+-[0-9]+)|([0-9]+ et [0-9]+)|([0-9]+,?)) (B |BIS |T |TIER )?)?\\b" + typeVoie + "\\b(\\.)? (.*)";
            Pattern pattern =   Pattern.compile(r, Pattern.CASE_INSENSITIVE);
            Matcher matcher =   pattern.matcher(ExcelToCSV.removeAccents(adresse.trim()));

            if(matcher.find()){		
                String voie = matcher.group();
                if(StringUtils.isNotBlank(voie)){
                    String rb = "^[ ]*[0-9]*[ ]*";
                    Pattern patternb =   Pattern.compile(rb, Pattern.CASE_INSENSITIVE);
                    Matcher matcherb =   patternb.matcher(voie);

                    if(matcherb.find()){	
                        String chaine = matcherb.group();
                        result.put(ResultEnum.NUMERO_VOIE.name(), chaine.trim());
                        result.put(ResultEnum.NOM_VOIE.name(), voie.substring(chaine.length()).trim());
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static boolean searchCity(Map<String, String> result, String adresse) {
        String ville = adresse;
        if(StringUtils.isNotBlank(ville)){
            String r = "^[ ]*[0-9]*[ ]*";
            Pattern pattern =   Pattern.compile(r, Pattern.CASE_INSENSITIVE);
            Matcher matcher =   pattern.matcher(ville);

            if(matcher.find()){	
                String chaine = matcher.group().trim();
                result.put(ResultEnum.CODE_POSTAL.name(), chaine.trim());
                result.put(ResultEnum.VILLE.name(), ville.substring(chaine.length()+1).trim());
                return true;
            }
        }
        return false;
    }

    public static boolean searchCivilite(Map<String, String> result, String adresse) {
        for(Civilite civilite : Civilite.values()){
            String c = civilite.toString();
            String r = "^" + c + " ";

            Pattern pattern =   Pattern.compile(r, Pattern.CASE_INSENSITIVE);
            Matcher matcher =   pattern.matcher(adresse.trim());

            if(matcher.find()){		
                result.put(ResultEnum.IDENTITAIRE_DESTINATAIRE.name(), adresse.trim());
                return true;
            }
        }
        return false;
    }
}
