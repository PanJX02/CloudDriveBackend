package com.panjx.clouddrive.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownloadResponse {
    List<DownloadFile> DownloadFiles;

    public static DownloadResponse withUrl(List<DownloadFile> downloadFile) {
        return new DownloadResponse(downloadFile);
    }
}
