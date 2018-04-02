package action;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import app.AlchemyToTiff;
import app.CodeBarre;
import app.ComptagePDF;
import app.Recommande;
import app.RenamePDF;
import app.TiffToPdf;
import enums.TypeApp;
import graphic.MainFrame;

public class ActionApp implements ActionListener {
	private TypeApp type;
	private JPanel area;
	private JTextPane screen;
	private JFrame mainFrame;

	public ActionApp(MainFrame mainFrame, JPanel area, TypeApp type) {
		this.area = area;
		this.type = type;
		this.mainFrame = mainFrame;
	}

	public void actionPerformed(ActionEvent arg0) {
		//on affiche une fenetre d'aide
		area.removeAll();
		area.repaint();

		screen = new JTextPane();
		JScrollPane jScrollPane = new JScrollPane(screen, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		area.setLayout(new BorderLayout());
		area.add(jScrollPane);

		if(enums.TypeApp.RECOMMANDES.equals(type)){
			Recommande.traitement(screen);
		}else if(enums.TypeApp.CODEBARRE.equals(type)){
			CodeBarre.traitement(screen);
		}else if(enums.TypeApp.COMPTAGEPDF.equals(type)){
			ComptagePDF.traitement(screen);
		}else if(enums.TypeApp.RENAME.equals(type)){
			RenamePDF.traitement(screen);
		}else if(enums.TypeApp.ACHEMYTOTIFF.equals(type)){
			AlchemyToTiff.traitement(screen);
		}else if(enums.TypeApp.TIFFTOPDF.equals(type)){
			TiffToPdf.traitement(screen);
		}

		mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		//mainFrame.setUndecorated(true);
		mainFrame.setVisible(true);
	}
}
