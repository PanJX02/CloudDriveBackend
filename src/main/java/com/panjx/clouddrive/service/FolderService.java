package com.panjx.clouddrive.service;

import com.panjx.clouddrive.pojo.FileList;
import org.springframework.stereotype.Service;

@Service
public interface FolderService {
    FileList getFiles(Long folderId);
}
