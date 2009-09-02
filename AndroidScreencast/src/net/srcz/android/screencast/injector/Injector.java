package net.srcz.android.screencast.injector;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import com.android.ddmlib.Device;
import com.android.ddmlib.IShellOutputReceiver;

public class Injector {
	private static final int PORT = 1324;
	Device device;
	Socket s;
	OutputStream os;
	
	public Injector(Device d) throws IOException {
		this.device = d;
		Thread t = new Thread() {
			public void run() {
				try {
					launchProg(""+PORT);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		device.createForward(PORT, PORT);

		try {
			s = new Socket("127.0.0.1",PORT);
			os = s.getOutputStream();
			os.write("quit\n".getBytes());
			os.flush();
			os.close();
			s.close();
			System.out.println("Old client closed");
		} catch(Exception ex) {
			// ignoré
		}

		try {
			Thread.sleep(1000);
			t.start();
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		s = new Socket("127.0.0.1",PORT);
		os = s.getOutputStream();
		System.out.println("succes !");
	}
	
	public void close() {
		try {
			os.write("quit\n".getBytes());
			os.flush();
			os.close();
			s.close();
		} catch(Exception ex) {
			// ignoré 
		}
	}
	
	public void injectMouse(int action, float x, float y) throws IOException {
		long downTime = 10;
    	long eventTime = 10;
    	
    	int metaState = -1;
    	
        String cmdList1 = "pointer/"+downTime+"/"+eventTime+"/"+action+"/"+x+"/"+y+"/"+metaState;
		injectData(cmdList1);
	}
	
	public void injectTrackball(float amount) throws IOException {
		long downTime = 0;
    	long eventTime = 0;
    	float x = 0;
    	float y = amount;
    	int metaState = -1;
    	
        String cmdList1 = "trackball/"+downTime+"/"+eventTime+"/"+ConstEvtMotion.ACTION_MOVE+"/"+x+"/"+y+"/"+metaState;
		injectData(cmdList1);
        String cmdList2 = "trackball/"+downTime+"/"+eventTime+"/"+ConstEvtMotion.ACTION_CANCEL+"/"+x+"/"+y+"/"+metaState;
		injectData(cmdList2);
	}
	
	public void injectKeycode(int type, int keyCode) throws IOException {
		String cmdList = "key/"+type+"/"+keyCode;
		injectData(cmdList);
	}
	
	private void injectData(String data) throws IOException {
		os.write( (data+"\n").getBytes());
		os.flush();
	}
	
	private void launchProg(String cmdList) throws IOException {
		String fullCmd = "export CLASSPATH=/data/MyInjectEventApp.jar; exec app_process /system/bin net.srcz.android.screencast.client.Main "+cmdList;
		System.out.println(fullCmd);
		device.executeShellCommand(fullCmd, new IShellOutputReceiver() {
			
			@Override
			public boolean isCancelled() {
				return false;
			}
			
			@Override
			public void flush() {
			}
			
			@Override
			public void addOutput(byte[] buf, int off, int len) {
				System.out.write(buf,off,len);
				System.out.println("recu "+len);
			}
		});		
		System.out.println("Prog ended");
	}
}
