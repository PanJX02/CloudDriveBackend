package com.panjx.clouddrive.controller;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.UploadRequest;
import com.panjx.clouddrive.pojo.UploadResponse;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public Result upload(@RequestBody UploadRequest uploadRequest) {
        log.info("上传文件");
        log.info("文件名：{}  扩展名：{}  SHA256：{}  pid：{}", uploadRequest.getFileName(),uploadRequest.getFileExtension(),uploadRequest.getFileSHA256(),uploadRequest.getFile_pid());
        return fileService.upload(uploadRequest.getFileName(),uploadRequest.getFileExtension(),uploadRequest.getFileSHA256(),uploadRequest.getFile_pid());
    }
    
    @PostMapping("/upload/complete")
    public Result uploadComplete(@RequestBody UserFile userFile) {
        log.info("接收上传完成通知");
        log.info("文件名：{} 大小：{} SHA256：{}", userFile.getFileName(), userFile.getFileSize(), userFile.getFileSHA256());
        return fileService.uploadComplete(userFile);
    }
}
