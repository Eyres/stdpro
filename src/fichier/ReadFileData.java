package fichier;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ReadFileData {
    public static ArrayList<String> read(InputStream ips){
        ArrayList<String> chaine = new ArrayList<String>();

        //lecture du fichier texte	
        try{
            InputStreamReader ipsr=new InputStreamReader(ips);
            BufferedReader br=new BufferedReader(ipsr);
            String ligne;
            while ((ligne=br.readLine())!=null){
                if(ligne == null || ligne.length() == 0) continue;
                chaine.add(ligne.trim());
            }
            br.close(); 
        }catch (Exception e){
            System.out.println(e.toString());
        }	

        return chaine;
    }
}