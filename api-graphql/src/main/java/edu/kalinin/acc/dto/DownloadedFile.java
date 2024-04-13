package edu.kalinin.acc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DownloadedFile {

    private String contentType;
    private byte[] content;
    private String fileName;
    private boolean isEmpty;
    private long size;

    public DownloadedFile(String contentType, byte[] content, String fileName) {
        this.contentType = contentType;
        this.content = content;
        this.fileName = fileName;
    }

}