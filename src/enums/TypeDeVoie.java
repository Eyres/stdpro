package enums;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;

import myApp.Main;
import fichier.ReadFileData;

public class TypeDeVoie {
    private static ArrayList<String> typeVoie = new ArrayList<String>();

    public static final ArrayList<String> getTypeVoie() throws URISyntaxException{
        if(typeVoie.size() != 0){
            return typeVoie;
        }

        InputStream url = Main.class.getResourceAsStream("TypeDeVoie.csv");
        ArrayList<String> typeVoie = ReadFileData.read(url);
        return typeVoie;
    }

    private final String value;

    private TypeDeVoie(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
