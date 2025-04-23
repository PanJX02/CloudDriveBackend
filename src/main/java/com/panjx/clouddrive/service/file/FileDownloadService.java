package com.panjx.clouddrive.service.file;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.UserFile;

public interface FileDownloadService {

    /**
     * 处理文件下载的操作
     */
    Result download(UserFile userFile);
}
