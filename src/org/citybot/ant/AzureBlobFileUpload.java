package org.citybot.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.microsoft.windowsazure.services.blob.client.BlobContainerPermissions;
import com.microsoft.windowsazure.services.blob.client.BlobContainerPublicAccessType;
import com.microsoft.windowsazure.services.blob.client.CloudBlobClient;
import com.microsoft.windowsazure.services.blob.client.CloudBlobContainer;
import com.microsoft.windowsazure.services.blob.client.CloudBlockBlob;
import com.microsoft.windowsazure.services.blob.client.ListBlobItem;
import com.microsoft.windowsazure.services.core.storage.CloudStorageAccount;
import com.microsoft.windowsazure.services.core.storage.StorageException;

public class AzureBlobFileUpload extends Task {
	String blob;
	String container;
	String account;
	String key;
	String protocol = "http";
	String file;
	Boolean create = true;
	Boolean list = false;
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
	public void setCreate(Boolean create) {
		this.create = create;
	}
	public void setFile(String file) {
		this.file = file;
	}

	public void setList(Boolean list) {
		this.list = list;
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
			CloudBlockBlob blobHandle = blobContainer.getBlockBlobReference(blob);
			File source = new File(file);
			blobHandle.upload(new FileInputStream(source), source.length());
			if(list) {
				// Loop over blobs within the container and output the URI to each of them
				for (ListBlobItem blobItem : blobContainer.listBlobs()) {
					project.log(blobItem.getUri().toString());
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
