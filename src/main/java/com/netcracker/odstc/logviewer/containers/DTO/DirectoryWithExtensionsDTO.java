package com.netcracker.odstc.logviewer.containers.DTO;

import com.netcracker.odstc.logviewer.models.Directory;

public class DirectoryWithExtensionsDTO {
    private Directory directory;
    private String[] extensions;

    public DirectoryWithExtensionsDTO() {
    }

    public DirectoryWithExtensionsDTO(Directory directory, String[] extensions) {
        this.directory = directory;
        this.extensions = extensions;
    }

    public Directory getDirectory() {
        return directory;
    }

    public void setDirectory(Directory directory) {
        this.directory = directory;
    }

    public String[] getExtensions() {
        return extensions;
    }

    public void setExtensions(String[] extensions) {
        this.extensions = extensions;
    }
}
