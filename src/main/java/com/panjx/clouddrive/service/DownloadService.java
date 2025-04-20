package com.panjx.clouddrive.service;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.UserFile;

public interface DownloadService {

    /**
     * 处理文件下载的操作
     */
    Result download(UserFile userFile);
}
