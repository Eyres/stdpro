package listeners;

import java.awt.Color;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import fichier.WriteFile;

public class LogListener {
    private final static String newline = System.getProperty("line.separator");

    public static void ecrireLogFile(String path, String message) throws IOException{
        WriteFile.ecrire(formatDate() +" : "+ message + newline, path);
    }

    public synchronized static void ecrireLogArea(JTextPane screen, String message){
        System.out.println(message);
        appendToPanel(screen, message + newline, Color.BLACK);
        screen.update(screen.getGraphics());
        screen.updateUI();
    }

    private static String formatDate(){
        Date d = new Date();
        DateFormat mediumDateFormat = DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM,
                DateFormat.MEDIUM);
        return mediumDateFormat.format(d);
    }

    private static void appendToPanel(JTextPane screen, String msg, Color c){
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = screen.getDocument().getLength();
        screen.setCaretPosition(len);
        screen.setCharacterAttributes(aset, false);
        screen.replaceSelection(msg);
    }
}
