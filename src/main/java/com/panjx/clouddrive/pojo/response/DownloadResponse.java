package com.panjx.clouddrive.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownloadResponse {
    // 是否是文件夹
    boolean folderType;

    //总大小
    Long totalSize;

    // 下载链接
    List<DownloadFile> DownloadFiles;

    public static DownloadResponse withUrl(boolean folderType,Long totalSize,List<DownloadFile> downloadFile) {
        return new DownloadResponse(folderType,totalSize,downloadFile);
    }
}
