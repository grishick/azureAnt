package org.citybot.ant;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.microsoft.windowsazure.services.blob.client.CloudBlobClient;
import com.microsoft.windowsazure.services.blob.client.CloudBlobContainer;
import com.microsoft.windowsazure.services.blob.client.CloudBlockBlob;
import com.microsoft.windowsazure.services.core.storage.CloudStorageAccount;
import com.microsoft.windowsazure.services.core.storage.StorageException;

public class AzureBlobFileDownload extends Task {
	String blob;
	String container;
	String account;
	String key;
	String protocol = "http";
	String file;
	public void setBlob(String blob) {
		this.blob = blob;
	}
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
	public void setFile(String file) {
		this.file = file;
	}
	public void execute() {
        if (blob==null) {
            throw new BuildException("Property 'blob' is required");
        }
        if (container==null) {
            throw new BuildException("Property 'container' is required");
        }
        if (account==null) {
            throw new BuildException("Property 'account' is required");
        }
        if (key==null) {
            throw new BuildException("Property 'key' is required");
        }
        if(file == null) {
        	throw new BuildException("Property 'file' is required");
        }
        try {
        	String storageConnectionString = String.format("DefaultEndpointsProtocol=%s;AccountName=%s;AccountKey=%s", protocol, account, key);
			CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			CloudBlobContainer blobContainer = blobClient.getContainerReference(container);

			// Create or overwrite the "myimage.jpg" blob with contents from a local file
			CloudBlockBlob blobHandle = blobContainer.getBlockBlobReference(blob);
			if(blobHandle == null || !blobHandle.exists()) {
				project.log("Cannot find blob " + blob);
				throw new BuildException("Cannot find blob " + blob);
			}
			blobHandle.download(new FileOutputStream(file));
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
