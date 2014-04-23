azureAnt
========

This is a simple ANT plugin that adds two ANT tasks:

azurefileup - a task for uploading files to Windows Azure blob store 

and 
azurefiledown - a task for downloading files from Windows Azure blob store. 

to compile the ANT task jar file run "ant jar" in the root folder of the project.

Usage:
========
To use the new ANT tasks in your ANT script:

1. add azureant.jar file to your ant classpath. E.g.: 

    <path id="java.myproject.classpath">
        <pathelement location="${build.classes}"/>
        <fileset dir="somefolder">
        	<include name="azureant.jar" />
        </fileset>    	
    </path>
    
2. Define "azurefiledown" and "azurefileup" tasks in your ANT script:

<taskdef name="azurefileup" classname="org.citybot.ant.AzureBlobFileUpload" >
     	<classpath>
			<path refid="java.myproject.classpath" />
		</classpath>
</taskdef>

<taskdef name="azurefiledown" classname="org.citybot.ant.AzureBlobFileDownload" >
       	<classpath>
			<path refid="java.myproject.classpath" />
		</classpath>
</taskdef>   

3. Declare ANT properties to hold your Windows Azure credentials, e.g.:

	<property name="azure.key" value="averylongsequenceofcharactersthatisyourazurekey" />
	<property name="azure.account" value="azureant" />
	<property name="azure.container" value="testfiles" />
	

4. Use the tasks in your ANT targets, e.g.: 
to download a file from your Windows Azure blob store:

<azurefiledown file="testfiles/azureanttest_download.txt" blob="azureanttest.txt" 
container="${azure.container}" account="${azure.account}" key="${azure.key}"/>

to upload a file to your Windows Azure blob store:

<azurefileup container="${azure.container}" list="true" create="true" account="${azure.account}" key="${azure.key}">
    <fileset dir="${env.HOME}/somefilesIwantToUpload" includes="*" />
</azurefileup>


5. Task parameters:

    common parameters (both azurefileup and azurefiledown tasks)
        
        account - string, required. Your Windows Azure Storage Account name. You can manage these in the properties of your Windows Azure container by clicking on "Manage Access Keys" at the bottom of Azure Manager screen.
        
        key - string, required. Your Windows Azure Storage Account access key. You can manage these in the properties of your Windows Azure container by clicking on "Manage Access Keys" at the bottom of Azure Manager screen.
    
    azurefileup:
    
        container - string, required. Name of the blob container where you want the files to be uploaded.
        
        list -  boolean, optional. If set to "true" will print out a list of keys in the blob container where the new file is being uploaded after 
        finishing the upload. Default value is "false".
        
        create - boolean, optional. When a blob container specified in "container" parameter does not exist, the task can create
        the container. If "create" is set to "true", the task will attempt to create the blob container. If "create" is set to "false" and the container
        does not exist, the task will fail. Default value is "true".
        
        <fileset> - FileSet, required. Ant <fileset> element that defines the list of files to be uploaded. See more here: http://ant.apache.org/manual/Types/fileset.html
        
    azurefiledown:
    
        container - string, required. Name of the blob container where the blob that you want to download is located.
        
        blob - string, required. Name of the blob that you want to download.
        
        file - string, required. Local path, including file name where to save downloaded blob.

        
        
        
        
