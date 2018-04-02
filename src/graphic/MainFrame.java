package graphic;

import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import action.ActionApp;
import action.ActionHelp;
import enums.TypeApp;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private final ImageIcon imgAide =  new ImageIcon("aide.png");

	public MainFrame(){
		super();
		build();
	}

	private void build(){
		JPanel area = new JPanel();
		this.setLocationRelativeTo(null);

		GridLayout gl = new GridLayout(6, 2);
		gl.setHgap(10);
		gl.setVgap(10);
		area.setLayout(gl);

		area.add(makeButton(this, area, TypeApp.RECOMMANDES));
		area.add(makeHelp(area, TypeApp.RECOMMANDES));

		area.add(makeButton(this, area, TypeApp.CODEBARRE));
		area.add(makeHelp(area, TypeApp.CODEBARRE));

		area.add(makeButton(this, area, TypeApp.COMPTAGEPDF));
		area.add(makeHelp(area, TypeApp.COMPTAGEPDF));

		area.add(makeButton(this, area, TypeApp.RENAME));
		area.add(makeHelp(area, TypeApp.RENAME));

		area.add(makeButton(this, area, TypeApp.ACHEMYTOTIFF));
		area.add(makeHelp(area, TypeApp.ACHEMYTOTIFF));

		area.add(makeButton(this, area, TypeApp.TIFFTOPDF));
		area.add(makeHelp(area, TypeApp.TIFFTOPDF));

		this.add(area);
		this.setTitle("Automatisation des taches");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

		this.pack();
	}

	private JButton makeHelp(JPanel area, TypeApp type) {
		JButton item = new JButton(imgAide);
		item.addActionListener(new ActionHelp(area, type));
		return item;
	}

	private JButton makeButton(MainFrame mainFrame, JPanel area, TypeApp type) {
		JButton item = new JButton(type.getValue());
		item.addActionListener(new ActionApp(this, area, type));
		return item;
	}
}
