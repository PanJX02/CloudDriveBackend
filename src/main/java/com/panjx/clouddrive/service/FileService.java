package com.panjx.clouddrive.service;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.UserFile;
import org.springframework.stereotype.Service;

@Service
public interface FileService {

    Result upload(String fileName,String fileExtension, String fileSHA256, Long file_pid);

}
