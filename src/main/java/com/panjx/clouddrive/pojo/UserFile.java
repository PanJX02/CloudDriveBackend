package com.panjx.clouddrive.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFile {
    private long id;
    private long userId;
    private long fileId;
    private String fileSHA256;
    private String fileName;
    private String fileExtension;
    private String fileCategory;
    private String fileSize;
    private long filePid;
    private int folderType;
    private int deleteFlag;
    private long recoveryTime;
    private long createTime;
    private long lastUpdateTime;
}
