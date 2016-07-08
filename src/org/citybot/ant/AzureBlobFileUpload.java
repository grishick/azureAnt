package org.citybot.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Iterator;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.selectors.FileSelector;

import com.microsoft.windowsazure.services.blob.client.BlobContainerPermissions;
import com.microsoft.windowsazure.services.blob.client.BlobContainerPublicAccessType;
import com.microsoft.windowsazure.services.blob.client.CloudBlobClient;
import com.microsoft.windowsazure.services.blob.client.CloudBlobContainer;
import com.microsoft.windowsazure.services.blob.client.CloudBlockBlob;
import com.microsoft.windowsazure.services.blob.client.ListBlobItem;
import com.microsoft.windowsazure.services.core.storage.CloudStorageAccount;
import com.microsoft.windowsazure.services.core.storage.StorageException;

public class AzureBlobFileUpload extends Task {
	String container;
	String account;
	String key;
	String blobpath;
	String protocol = "http";
	private Vector filesets = new Vector();
	Boolean create = true;
	Boolean list = false;
	public void setContainer(String container) {
		this.container = container;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public void setCreate(Boolean create) {
		this.create = create;
	}

	public void setList(Boolean list) {
		this.list = list;
	}
	public void addFileset(FileSet fileset) {
        filesets.add(fileset);
    	}
	
	public String getBlobpath() {
		return blobpath;
	}
	public void setBlobpath(String blobpath) {
		this.blobpath = blobpath;
	}
	public void execute() {
        if (container==null) {
            throw new BuildException("Property 'container' is required");
        }
        if (account==null) {
            throw new BuildException("Property 'account' is required");
        }
        if (key==null) {
            throw new BuildException("Property 'key' is required");
        }
        if(filesets.isEmpty()) {
        	throw new BuildException("A nested 'fileset' is required");
        }
        try {
        	String storageConnectionString = String.format("DefaultEndpointsProtocol=%s;AccountName=%s;AccountKey=%s", protocol, account, key);
			CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			CloudBlobContainer blobContainer = blobClient.getContainerReference(container);
			if(create) {
				blobContainer.createIfNotExist();
				// Create a permissions object
				BlobContainerPermissions containerPermissions = new BlobContainerPermissions();
				// Include public access in the permissions object
				//containerPermissions.setPublicAccess(BlobContainerPublicAccessType.CONTAINER);
				// Set the permissions on the container
				blobContainer.uploadPermissions(containerPermissions);
			}

			// Create or overwrite the "myimage.jpg" blob with contents from a local file
			
			for(Iterator itFSets = filesets.iterator(); itFSets.hasNext(); ) {
				FileSet fs = (FileSet)itFSets.next();
				DirectoryScanner ds = fs.getDirectoryScanner(getProject());        
	            String[] includedFiles = ds.getIncludedFiles();
		        for(int i=0; i<includedFiles.length; i++) {
		        	String filename = includedFiles[i].replace('\\','/');
		        	File source = new File(ds.getBasedir() + "/" + filename);
		        	String blobname = filename.substring(filename.lastIndexOf("/")+1);
		        	if(getBlobpath()!=null){
		        		blobname = getBlobpath() + "/" + blobname;
		        	}
	                CloudBlockBlob blobHandle = blobContainer.getBlockBlobReference(blobname);
	                blobHandle.upload(new FileInputStream(source), source.length());
		        }
			}
			
			if(list) {
				// Loop over blobs within the container and output the URI to each of them
				for (ListBlobItem blobItem : blobContainer.listBlobs()) {
					getProject().log(blobItem.getUri().toString());
				}
			}
		} catch (InvalidKeyException e) {
			throw new BuildException(e);
		} catch (URISyntaxException e) {
			throw new BuildException(e);
		} catch (StorageException e) {
			throw new BuildException(e);
		} catch (FileNotFoundException e) {
			throw new BuildException(e);
		} catch (IOException e) {
			throw new BuildException(e);
		}
    }
	
}
