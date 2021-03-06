package jrdesktop.viewer.main.FileMng;

import jrdesktop.viewer.main.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * FileManager.java
 * @author benbac
 */

public class FileManager {

    private File[] files;
    private String serverDolwnloadingFolder;   
    private String dolwnloadingFolder;
    private String uploadingFolder;
    
    private Recorder recorder;
    private FileTransGui fileTransGui;
    
    public FileManager(Recorder recorder) {
        this.recorder = recorder;
    }
            
    public File[] getFiles() {
        return files;
    }
    
    public void setFiles(File[] _files) {
        files = _files;
    }
    
    public void ReceiveFiles(FileTransGui fileTransGui, File[] files) {
        long size = 0;
        String filename, viewerFilename;
        for (int i=0; i<files.length; i++) {
            if (fileTransGui.isCanceled()) return;
            fileTransGui.setCurrentFile(files[i].getName());
            filename = files[i].toString();
            viewerFilename = filename.substring(serverDolwnloadingFolder.length());
            viewerFilename = dolwnloadingFolder + viewerFilename;
            size += ReceiveFile(filename, viewerFilename);
            fileTransGui.updateData(size);
        }
    } 
    
    public void SendFiles(FileTransGui fileTransGui, File[] files) {
        long size = 0;
        for (int i=0; i<files.length; i++) {
            if (fileTransGui.isCanceled()) return;
            fileTransGui.setCurrentFile(files[i].toString());                      
            size += SendFile(files[i]);     
            fileTransGui.updateData(size);
        }        
    }
    
    public long ReceiveFile(String filename, String viewerFilename) {
        try { 
            byte[] filedata = recorder.viewer.ReceiveFile(filename);
            new File(new File(viewerFilename).getParent()).mkdirs();
            File file = new File(viewerFilename);
            
            BufferedOutputStream output = new
                BufferedOutputStream(new FileOutputStream(file));
            output.write(filedata, 0, filedata.length);
            output.flush();
            output.close(); 
            return filedata.length;
        } catch (Exception e) {
            e.getStackTrace();
            return 0;
        }
    }
    
    public long SendFile(File file) {      
        try {
            byte buffer[] = new byte[(int)file.length()];
            BufferedInputStream input = new
                BufferedInputStream(new FileInputStream(file));
            input.read(buffer, 0, buffer.length);
            input.close();            
            String filename = file.toString().substring(uploadingFolder.length());
            recorder.viewer.SendFile(buffer, filename);
            return file.length();
        } catch(Exception e){
            e.printStackTrace();
            return 0;
        }    
    }        
        
    public void setUploadingFolder(String folder) {
        uploadingFolder = folder;
    }
    
    public void setDownloadingFolder(String folder) {
        dolwnloadingFolder = folder;
    }
    
    public void setServerDownloadingFolder(String folder) {
        serverDolwnloadingFolder = folder;
    }      
}
