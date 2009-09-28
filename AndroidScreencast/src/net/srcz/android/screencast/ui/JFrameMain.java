package net.srcz.android.screencast.ui;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import net.srcz.android.screencast.injector.ConstEvtKey;
import net.srcz.android.screencast.injector.ConstEvtMotion;
import net.srcz.android.screencast.injector.Injector;
import net.srcz.android.screencast.injector.KeyCodeConverter;

public class JFrameMain extends JFrame {

	public JPanelScreen jp = new JPanelScreen();
	public Injector injector = null;
	
	public void setInjector(Injector injector) {
		this.injector = injector;
	}

	public JFrameMain() throws IOException {
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon.png")));
		setDefaultCloseOperation(3);
		setLayout(new BorderLayout());
		add(new JScrollPane(jp),BorderLayout.CENTER);
		setSize(336*1, 512*1);
		setLocationRelativeTo(null);
		addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if(injector == null)
					return;
				try {
					int code = KeyCodeConverter.getKeyCode(e);
					injector.injectKeycode(ConstEvtKey.ACTION_DOWN,code);
				} catch (IOException e1) {
					throw new RuntimeException(e1);
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if(injector == null)
					return;
				try {
					int code = KeyCodeConverter.getKeyCode(e);
					injector.injectKeycode(ConstEvtKey.ACTION_UP,code);
				} catch (IOException e1) {
					throw new RuntimeException(e1);
				}
			}
			
			
		});
		
		MouseAdapter ma = new MouseAdapter() {

			@Override
			public void mouseDragged(MouseEvent arg0) {
				if(injector == null)
					return;
				try {
					Point p2 = jp.getRawPoint(arg0.getPoint());
					injector.injectMouse(ConstEvtMotion.ACTION_MOVE, p2.x, p2.y);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if(injector == null)
					return;
				try {
					Point p2 = jp.getRawPoint(arg0.getPoint());
					injector.injectMouse(ConstEvtMotion.ACTION_DOWN, p2.x, p2.y);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				if(injector == null)
					return;
				try {
					if(arg0.getButton() == MouseEvent.BUTTON3) {
						jp.setLandscape(!jp.isLandscape());
						return;
					}
					Point p2 = jp.getRawPoint(arg0.getPoint());
					injector.injectMouse(ConstEvtMotion.ACTION_UP, p2.x, p2.y);
					
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				if(injector == null)
					return;
				try {
					//injector.injectKeycode(ConstEvtKey.ACTION_DOWN,code);
					//injector.injectKeycode(ConstEvtKey.ACTION_UP,code);
					injector.injectTrackball(arg0.getWheelRotation() < 0 ? -1f : 1f);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
		
		jp.addMouseMotionListener(ma);
		jp.addMouseListener(ma);
		jp.addMouseWheelListener(ma);
		

		
	}
}
