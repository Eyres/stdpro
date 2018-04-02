package action;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import enums.TypeApp;
import listeners.LogListener;

public class ActionHelp implements ActionListener {
	private TypeApp type;
	private JPanel area;
	private JTextPane screen;

	public ActionHelp(JPanel area, TypeApp type) {
		this.area = area;
		this.type = type;
	}

	public void actionPerformed(ActionEvent arg0) {
		//on affiche une fenetre d'aide
		area.removeAll();
		area.repaint();

		screen = new JTextPane();
		area.add(new JScrollPane(screen, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
		area.setLayout(new BorderLayout());
		area.add(screen);

		if(enums.TypeApp.RECOMMANDES.equals(type)){
			LogListener.ecrireLogArea(screen, "Permet de récupérer les adresses destinataires sur un PDF pour les importer dans un fichier adresses CSV (ex : MED/LBPAI) normalisé et conforme aux normes postales.");
		}else if(enums.TypeApp.CODEBARRE.equals(type)){
			LogListener.ecrireLogArea(screen, "Permet de récupérer les fichiers « contrats SFR » pour les associer aux fichiers « adresses agences » et permettre de faire les relances et statistiques.");
		}else if(enums.TypeApp.COMPTAGEPDF.equals(type)){
			LogListener.ecrireLogArea(screen, "Permet de comptabiliser dans un dossier le nombre de PDF ainsi que le nombre de pages dans chaque PDF (ex : Bouygues). "
					+ "Le résultat peut servir à fournir un référentiel de conservation en csv.");
		}else if(enums.TypeApp.RENAME.equals(type)){
			LogListener.ecrireLogArea(screen, "Permet de renommer les documents PDF ou TIFF (ex : numéro de contrat) grâce à une ocerisation full texte."
			+" Permet également de préciser le nommage en ajoutant un préfixe et/ou un suffixe."
			+" Ex Norme de renommage  \\d{10} = numéro de contrat à 10 chiffres"
			+" Ex Norme de renommage 2 \\d{6} = numéro de contrat commençant par un 2 à 7 chiffres");
		}else if(enums.TypeApp.ACHEMYTOTIFF.equals(type)){
			LogListener.ecrireLogArea(screen, "Permet de récupérer des fichiers TIFF pour les renommer en TIFF  grâce à un fichier .dat");
		}else if(enums.TypeApp.TIFFTOPDF.equals(type)){
			LogListener.ecrireLogArea(screen, "Permet de changer les TIFF en PDF (avec un préfixe et/ou suffixe)");
		}
	}
}
