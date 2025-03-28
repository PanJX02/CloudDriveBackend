package com.panjx.clouddrive.pojo;

import lombok.Data;

@Data
public class UploadRequest {
    private String fileName;
    private String fileExtension;
    private String fileSHA256;
    private Long file_pid;
}
