package net.srcz.android.screencast;

import java.io.IOException;

import javax.swing.JPanel;

import net.srcz.android.screencast.ui.JFrameMain;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.Device;

public class Main extends JPanel {

	public Main() throws IOException {

		AndroidDebugBridge bridge;
		bridge = AndroidDebugBridge.createBridge();
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
				System.err.println("Timeout getting device list!");
				return;
			}
		}
		Device devices[] = bridge.getDevices();
		final Device device = devices[0];
		
		final JFrameMain jf = new JFrameMain(device);
		
		final Thread tPolling = new Thread(new Runnable() {

			public void run() {
				jf.jp.pollForever();
			}
		});
		tPolling.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("exiting...");
				tPolling.interrupt();
				jf.injector.close();
				AndroidDebugBridge.terminate();
				System.out.println("exiting... cleanup done");
			}
		});
	}

	public static void main(String args[]) throws IOException {
		new Main();
	}

}
