package com.panjx.clouddrive.controller;

import com.panjx.clouddrive.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/folders")
public class FolderController {

    @Autowired
    private FolderService folderService;

    @GetMapping("/{folderId}/files")
    public String getFiles(@PathVariable String folderId) {
        return "fileList";
    }
}
