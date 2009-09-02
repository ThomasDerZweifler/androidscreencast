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

import com.android.ddmlib.Device;

public class JFrameMain extends JFrame {

	public JPanelScreen jp;
	public Injector injector;
	
	public JFrameMain(final Device device) throws IOException {
		jp = new JPanelScreen(device);
		injector = new Injector(device);
		setTitle(""+device);
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon.png")));
		setDefaultCloseOperation(3);
		setLayout(new BorderLayout());
		add(new JScrollPane(jp),BorderLayout.CENTER);
		setSize(336*1, 512*1);
		setLocationRelativeTo(null);
		addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				try {
					int code = KeyCodeConverter.getKeyCode(e);
					injector.injectKeycode(ConstEvtKey.ACTION_DOWN,code);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					int code = KeyCodeConverter.getKeyCode(e);
					injector.injectKeycode(ConstEvtKey.ACTION_UP,code);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
			
		});
		
		MouseAdapter ma = new MouseAdapter() {

			@Override
			public void mouseDragged(MouseEvent arg0) {
				try {
					Point p2 = jp.getRawPoint(arg0.getPoint());
					injector.injectMouse(ConstEvtMotion.ACTION_MOVE, p2.x, p2.y);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				try {
					Point p2 = jp.getRawPoint(arg0.getPoint());
					injector.injectMouse(ConstEvtMotion.ACTION_DOWN, p2.x, p2.y);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				try {
					Point p2 = jp.getRawPoint(arg0.getPoint());
					injector.injectMouse(ConstEvtMotion.ACTION_UP, p2.x, p2.y);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				try {
					//injector.injectKeycode(ConstEvtKey.ACTION_DOWN,code);
					//injector.injectKeycode(ConstEvtKey.ACTION_UP,code);
					injector.injectTrackball(arg0.getWheelRotation() < 0 ? -1f : 1f);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		jp.addMouseMotionListener(ma);
		jp.addMouseListener(ma);
		jp.addMouseWheelListener(ma);
		

		setVisible(true);
	}
}
