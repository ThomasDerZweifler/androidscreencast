package net.srcz.android.screencast;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.srcz.android.screencast.injector.Injector;
import net.srcz.android.screencast.ui.JDialogError;
import net.srcz.android.screencast.ui.JFrameMain;
import net.srcz.android.screencast.ui.JSplashScreen;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.Device;

public class Main extends JPanel {

	JFrameMain jf;
	Thread tPolling;
	
	private void waitDeviceList(AndroidDebugBridge bridge) {
		int count = 0;
		while (bridge.hasInitialDeviceList() == false) {
			try {
				Thread.sleep(100);
				count++;
			} catch (InterruptedException e) {
				// pass
			}
			// let's not wait > 10 sec.
			if (count > 100) {
				throw new RuntimeException("Timeout getting device list!");
			}
		}
	}
	
	public Main() throws IOException {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				close();
			}
		});
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			JDialogError jd = null;
			
			@Override
			public void uncaughtException(Thread arg0, Throwable ex) {
				try {
					if(jd != null && jd.isVisible())
						return;
					jd = new JDialogError(ex);
					SwingUtilities.invokeAndWait(new Runnable() {
						
						@Override
						public void run() {
							jd.setVisible(true);
							
						}
					});
				} catch(Exception ex2) {
					// ignored
					ex2.printStackTrace();
				}
			}
		});


		
		
		JSplashScreen jw = new JSplashScreen("Getting devices list...");
		jw.setVisible(true);

		try {
			initialize(jw);
		} finally {
			jw.setVisible(false);
			jw = null;
		}
		jf.setVisible(true);
		


	}
	
	private void initialize(JSplashScreen jw) throws IOException {
		AndroidDebugBridge bridge = AndroidDebugBridge.createBridge();
		waitDeviceList(bridge);
		
		Device devices[] = bridge.getDevices();
		final Device device = devices[0];

		jw.setText("Starting input injector...");
		Injector injector = new Injector(device);

		jf = new JFrameMain(device, injector);

		tPolling = new Thread(new Runnable() {

			public void run() {
				jf.jp.pollForever();
			}
		});
		tPolling.start();
	}
	
	private void close() {
		System.out.println("exiting...");
		tPolling.interrupt();
		jf.injector.close();
		AndroidDebugBridge.terminate();
		System.out.println("exiting... cleanup done");
	}

	public static void main(String args[]) throws IOException {
		new Main();
	}

}
