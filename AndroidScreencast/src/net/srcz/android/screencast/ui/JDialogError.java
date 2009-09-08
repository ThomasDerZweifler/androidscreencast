package net.srcz.android.screencast.ui;

import java.awt.BorderLayout;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JDialog;
import javax.swing.JLabel;

public class JDialogError extends JDialog {

	public JDialogError(Throwable ex) {
		getRootPane().setLayout(new BorderLayout());
		JLabel l = new JLabel();
		StringWriter w = new StringWriter();
		ex.printStackTrace(new PrintWriter(w));
		l.setUI(new MultiLineLabelUI());
		l.setText(w.toString());
		getRootPane().add(l,BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
	}
	
}
