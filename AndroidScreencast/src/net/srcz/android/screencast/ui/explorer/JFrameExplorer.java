package net.srcz.android.screencast.ui.explorer;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import net.srcz.android.screencast.api.injector.OutputStreamShellOutputReceiver;

import com.android.ddmlib.FileListingService;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.FileListingService.FileEntry;
import com.android.ddmlib.FileListingService.IListingReceiver;
import com.android.ddmlib.SyncService.ISyncProgressMonitor;

public class JFrameExplorer extends JFrame {

	JTree jt;
	IDevice device;
	FileListingService service;
	
	private class FileTreeNode extends DefaultMutableTreeNode {
		String name;
		String path;
		
		public FileTreeNode(String name, String path) {
			super(name);
			this.name = name;
			this.path = path;
		}
		
		
	}
	
	private class FolderTreeNode extends LazyMutableTreeNode {
		
		String name;
		String path;
		
		public FolderTreeNode(String name, String path) {
			this.name = name;
			this.path = path;
		}
		
		@Override
		public void initChildren() {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				String cmd = "ls -l "+path;
				device.executeShellCommand(cmd, new OutputStreamShellOutputReceiver(bos));
				String s = new String(bos.toByteArray(),"UTF-8");
				String[] entries = s.split("\r\n");
				for(int i=0; i<entries.length; i++) {
					String[] data = entries[i].split(" ");
					if(data.length < 4)
						continue;
					/*
					for(int j=0; j<data.length; j++) {
						System.out.println(j+" = "+data[j]);
					}
					*/
					String attribs = data[0];
					boolean directory = attribs.startsWith("d");
					String name = data[data.length-1];
					if(directory)
						add(new FolderTreeNode(name, path + name + "/"));
					else
						add(new FileTreeNode(name, path + name));
				}
			} catch(Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		
		public String toString() {
			return name;
		}
		
	}
	
	public JFrameExplorer(IDevice device) {
		this.device = device;
		this.service = 	device.getFileListingService();
		
		setTitle("Explorer");
		setLayout(new BorderLayout());
		
		jt = new JTree(new DefaultMutableTreeNode("Test"));
		jt.setModel(new DefaultTreeModel(new FolderTreeNode("Device","/")));
		jt.setRootVisible(true);
		
		JScrollPane jsp = new JScrollPane(jt);
		
		add(jsp, BorderLayout.CENTER);
		setSize(640,480);
		setLocationRelativeTo(null);
		
		//FileEntry[] entries = 
		device.getFileListingService().getChildren(service.getRoot(), false, new IListingReceiver() {
			
			public void setChildren(FileEntry arg0, FileEntry[] arg1) {
				// TODO Auto-generated method stub
				System.out.println("setChildren "+arg1.length);
				
			}
			
			public void refreshEntry(FileEntry arg0) {
				
				System.out.println("refresh"+arg0.getName());
			}
		});
		
		jt.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					TreePath tp = jt.getPathForLocation(e.getX(), e.getY());
					if(tp == null)
						return;
					if(!(tp.getLastPathComponent() instanceof FileTreeNode))
						return;
					FileTreeNode node = (FileTreeNode)tp.getLastPathComponent();
					launchFile(node);
				}
			}
			
		});
	}

	private void launchFile(FileTreeNode node) {
		try {
			File tempFile = File.createTempFile("android", node.name);
			tempFile.deleteOnExit();
			// ugly hack to call the method without FileEntry
			Method m = device.getSyncService().getClass().getDeclaredMethod("doPullFile", String.class, String.class, ISyncProgressMonitor.class);
			m.setAccessible(true);
			m.invoke(device.getSyncService(), node.path, tempFile.getAbsolutePath(), device.getSyncService().getNullProgressMonitor());
			Desktop.getDesktop().open(tempFile);
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
}
