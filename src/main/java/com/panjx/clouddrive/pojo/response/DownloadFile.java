package com.panjx.clouddrive.pojo.response;

import lombok.Data;

@Data
public class DownloadFile {
    private String SHA256;
    private String url;
    private boolean folderType;
    private Long size;
    private String filePath;
    private String fileName;
    private String fileExtension;
}
