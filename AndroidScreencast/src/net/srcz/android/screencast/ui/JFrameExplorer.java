package net.srcz.android.screencast.ui;

import java.awt.BorderLayout;
import java.io.ByteArrayOutputStream;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import net.srcz.android.screencast.injector.OutputStreamShellOutputReceiver;

import com.android.ddmlib.Device;
import com.android.ddmlib.FileListingService;
import com.android.ddmlib.FileListingService.FileEntry;
import com.android.ddmlib.FileListingService.IListingReceiver;

public class JFrameExplorer extends JFrame {

	JTree jt;
	Device device;
	FileListingService service;
	
	private class FileTreeNode extends LazyLoadingTreeNode {
		
		String entry;
		
		public FileTreeNode(String entry) {
			super(entry,jt,false);
			this.entry = entry;
		}
		
		@Override
		public MutableTreeNode[] loadChildren(JTree tree) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				device.executeShellCommand("ls -l "+entry, new OutputStreamShellOutputReceiver(bos));
				String s = new String(bos.toByteArray(),"UTF-8");
				String[] entries = s.split("\n");
				MutableTreeNode[] nodes = new MutableTreeNode[entries.length];
				for(int i=0; i<entries.length; i++) {
					String[] data = entries[i].split(" ");
					if(data.length < 4)
						continue;
					for(int j=0; j<data.length; j++) {
						System.out.println(j+" = "+data[j]);
					}
					String attribs = data[0];
					boolean directory = attribs.startsWith("d");
					String path = entry + data[data.length-1];
					System.out.println("path="+path);
					if(directory)
						nodes[i] = new FileTreeNode(path);
					else
						nodes[i] = new DefaultMutableTreeNode(path);
				}
				return nodes;
			} catch(Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		
		public String toString() {
			return entry;
		}
		
	}
	
	public JFrameExplorer(Device device) {
		this.device = device;
		this.service = 	device.getFileListingService();
		setLayout(new BorderLayout());
		
		jt = new JTree(new DefaultMutableTreeNode("Test"));
		jt.setModel(new DefaultTreeModel(new FileTreeNode("/")));
		jt.setRootVisible(true);
		add(jt, BorderLayout.CENTER);
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
	}

}
