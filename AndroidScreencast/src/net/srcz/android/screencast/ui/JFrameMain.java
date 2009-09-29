package net.srcz.android.screencast.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.srcz.android.screencast.injector.ConstEvtKey;
import net.srcz.android.screencast.injector.ConstEvtMotion;
import net.srcz.android.screencast.injector.Injector;
import net.srcz.android.screencast.injector.KeyCodeConverter;
import net.srcz.android.screencast.injector.ScreenCaptureThread;

import com.android.ddmlib.Device;

public class JFrameMain extends JFrame {

	private JPanelScreen jp = new JPanelScreen();
	private JToolBar jtb = new JToolBar();
	private JToggleButton jtbRecord = new JToggleButton("Record");
	private JButton jbExplorer = new JButton("Explore");

	private Device device;
	private Injector injector;
	private ScreenCaptureThread captureThread;
	
	public void setInjector(Injector injector) {
		this.injector = injector;
	}

	public JFrameMain(Device device) throws IOException {
		this.device = device;
		initialize();
		this.captureThread = new ScreenCaptureThread(device, jp);
	}
	
	public void initialize() throws IOException {
		jtb.setFocusable(false);
		jbExplorer.setFocusable(false);
		jtbRecord.setFocusable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon.png")));
		setDefaultCloseOperation(3);
		setLayout(new BorderLayout());
		add(jtb,BorderLayout.NORTH);
		JScrollPane jsp = new JScrollPane(jp);
		add(jsp,BorderLayout.CENTER);
		jsp.setPreferredSize(new Dimension(336*1, 512*1));
		pack();
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
						captureThread.toogleOrientation();
						arg0.consume();
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
		
		jtbRecord.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if(jtbRecord.isSelected()) {
					startRecording();
				} else {
					stopRecording();
				}
			}
			
		});
		jtb.add(jtbRecord);
		
		jbExplorer.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				JFrameExplorer jf = new JFrameExplorer(device);
				jf.setVisible(true);
			}
		});
		jtb.add(jbExplorer);
		
	}
	
	private void startRecording() {
		JFileChooser jFileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Vidéo file","mov");
		jFileChooser.setFileFilter(filter);
		int returnVal = jFileChooser.showSaveDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			captureThread.startRecording(jFileChooser.getSelectedFile());
		}
	}
	
	private void stopRecording() {
		captureThread.stopRecording();
	}
	
	public void startCapture() {
		captureThread.start();
	}
	
	public void stopCapture() {
		captureThread.interrupt();
	}
}
