package net.srcz.android.screencast.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.view.IWindowManager;
import android.view.KeyEvent;
import android.view.MotionEvent;


public class Main {

	IBinder wmbinder = ServiceManager.getService( "window" );
    final IWindowManager wm = IWindowManager.Stub.asInterface( wmbinder );
    int port;
    boolean debug = false;
    
	public Main(int port, boolean debug) {
		this.port = port;
		this.debug = debug;
	}
	
	public void execute() throws IOException {
	    ServerSocket ss = new ServerSocket(port);
	    while(true) {
            final Socket s = ss.accept();
            if(s == null || ss.isClosed())
            	break;
            if(debug)
            	System.out.println("New client ! ");
            Thread t = new Thread() {
            	public void run() {
            		try {
            			handleClient(s);
            		} catch(Exception ex) {
            			ex.printStackTrace();
            		}
            		try {
            			s.close();
            		} catch(Exception ex) {
            			// ignor�
            		}
            	}
            };
            t.start();
            /*
            Thread t2 = new Thread() {
            	public void run() {
            		try {
            			File f = new File("/dev/graphics/fb0");
            			FileInputStream fis = new FileInputStream(f);
            			byte[] data = new byte[16];
            			OutputStream os = s.getOutputStream();
            			while(true) {
            				int val = fis.read(data);
            				if(val <= 0) {
            					sleep(0);
            					continue;
            				}
            				os.write(data,0,val);
            				os.flush();
            			}
            		} catch(Exception ex) {
            			ex.printStackTrace();
            		}
            	}
            };
            t2.start();
            */
        }
		
	    
	}
	
	private void handleClient(Socket s) throws IOException, RemoteException {
		InputStream is = s.getInputStream();
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		while(true) {
    		String line = r.readLine();
    		if(line == null) {
    			r.close();
    			s.close();
    			break;
    		}
    		if(debug)
    			System.out.println("Received : "+line);
    		try {
    			handleCommand(line);
    		} catch(Exception ex) {
    			ex.printStackTrace();
    		}
		}
	}
	
	private void handleCommand(String line) throws RemoteException {
		String[] paramList = line.split("/");
		String type = paramList[0];
		if(type.equals("quit")) {
			System.exit(0);
			return;
		}
		if(type.equals("pointer")) {
			wm.injectPointerEvent(getMotionEvent(paramList), false);
			return;
		}
		if(type.equals("key")) {
			wm.injectKeyEvent(getKeyEvent(paramList), false);
			return;
		}
		if(type.equals("trackball")) {
			wm.injectTrackballEvent(getMotionEvent(paramList), false);
			return;
		}
		
		throw new RuntimeException("Invalid type : "+type);

	}
	
    public static void main(String[] args) {
    	try {
    		if(args.length == 0)
    			throw new RuntimeException("Need >= 1 param");
    	    int port = Integer.parseInt(args[0]);
    	    boolean debug = args.length >= 2 && args[1].equals("debug");
    	    new Main(port, debug).execute();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

    /*
    public static void keystroke2(int keycode) throws RemoteException {
    
		import android.app.Instrumentation;
		Instrumentation ins = new Instrumentation();
		ins.sendKeyDownUpSync(keycode);
    }
    */
    
    private static MotionEvent getMotionEvent(String[] args) {
    	int i = 1;
    	long downTime = Long.parseLong(args[i++]);
    	long eventTime = Long.parseLong(args[i++]);
    	int action = Integer.parseInt(args[i++]);
    	float x = Float.parseFloat(args[i++]);
    	float y = Float.parseFloat(args[i++]);
    	int metaState = Integer.parseInt(args[i++]);
        return MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
    }

    private static KeyEvent getKeyEvent(String[] args) {
    	int action = Integer.parseInt(args[1]);
    	int code = Integer.parseInt(args[2]);
    	return new KeyEvent(action, code);
    }
}
