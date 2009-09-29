package net.srcz.android.screencast;

import java.io.IOException;

import net.srcz.android.screencast.app.SwingApplication;
import net.srcz.android.screencast.injector.Injector;
import net.srcz.android.screencast.ui.JDialogDeviceList;
import net.srcz.android.screencast.ui.JFrameMain;
import net.srcz.android.screencast.ui.JSplashScreen;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.Device;

public class Main extends SwingApplication {

	JFrameMain jf;
	Injector injector;
	Device device;
	
	public Main() throws IOException {
	
		JSplashScreen jw = new JSplashScreen("");
		
		try {
			initialize(jw);
		} finally {
			jw.setVisible(false);
			jw = null;
		}
	}
	
	private void initialize(JSplashScreen jw) throws IOException {
		jw.setText("Getting devices list...");
		jw.setVisible(true);
		
		AndroidDebugBridge bridge = AndroidDebugBridge.createBridge();
		waitDeviceList(bridge);

		Device devices[] = bridge.getDevices();
		
		jw.setVisible(false);

		// Let the user choose the device
		JDialogDeviceList jd = new JDialogDeviceList(devices);
		jd.setVisible(true);
		
		device = jd.getDevice();
		if(device == null) {
			System.exit(0);
			return;
		}
		
		// Start showing the device screen
		jf = new JFrameMain(device);
		jf.setTitle(""+device);
		
		
		// Show window
		jf.setVisible(true);
		
		// Starting injector
		jw.setText("Starting input injector...");
		jw.setVisible(true);

		injector = new Injector(device);
		injector.start();
		jf.setInjector(injector);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Start polling
		jf.startCapture();
		
	}

	
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
	
	protected void close() {
		System.out.println("cleaning up...");
		if(injector != null)
			injector.close();
		if(jf != null)
			jf.stopCapture();
		if(device != null) {
			synchronized (device) {
				AndroidDebugBridge.terminate();
			}
		}
		System.out.println("cleanup done, exiting...");
		super.close();
	}

	public static void main(String args[]) throws IOException {
		new Main();
	}

}
