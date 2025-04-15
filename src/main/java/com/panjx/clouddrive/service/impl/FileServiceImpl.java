package com.panjx.clouddrive.service.impl;

import com.panjx.clouddrive.mapper.FileMapper;
import com.panjx.clouddrive.mapper.StorageMapper;
import com.panjx.clouddrive.mapper.UserMapper;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.Storage;
import com.panjx.clouddrive.pojo.response.DownloadFile;
import com.panjx.clouddrive.pojo.response.DownloadResponse;
import com.panjx.clouddrive.pojo.response.UploadResponse;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.service.FileService;
import com.panjx.clouddrive.utils.KodoUtil;
import com.panjx.clouddrive.utils.SecurityUtils;
import com.qiniu.common.QiniuException;
import lombok.extern.slf4j.Slf4j;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private StorageMapper storageMapper;

    @Autowired
    private UserMapper userMapper;

    private final Tika tika = new Tika();


    @Override
    public Result upload(String fileName,String fileExtension, String fileSHA256, Long file_pid) {
        log.info("根据文件SHA256查询文件");
        UserFile existingFile = fileMapper.findByFileSHA256(fileSHA256);
        log.info("查询结果：{}", existingFile);
        // 当前用户ID
        Long userId = SecurityUtils.getCurrentUserId();

        if (existingFile != null) {
            log.info("查询到文件ID：{}", existingFile.getId());

            // 创建新的用户文件记录
            UserFile userFile = new UserFile();
            // 设置用户ID
            userFile.setUserId(userId);
            // 设置文件ID
            userFile.setFileId(existingFile.getFileId());
            // 设置文件名
            userFile.setFileName(fileName);
            // 设置文件扩展名
            userFile.setFileExtension(fileExtension);
            // 设置父文件夹ID
            userFile.setFilePid(file_pid);
            // 设置文件类型为
            userFile.setFolderType(0); // 0表示文件
            // 设置删除标志为
            userFile.setDeleteFlag(0); // 2表示正常
            // 设置创建时间为当前时间
            userFile.setCreateTime(System.currentTimeMillis());
            // 设置最后更新时间为当前时间
            userFile.setLastUpdateTime(System.currentTimeMillis());

            // 增加引用计数
            fileMapper.increaseReferCount(existingFile.getFileId(), System.currentTimeMillis());

            // 添加到用户文件表
            fileMapper.addUserFile(userFile);

            //更新用户使用空间
            userMapper.updateUserSpace(userId, existingFile.getFileSize());

            return Result.success(UploadResponse.fileExists());
        } else {
            log.info("未查询到文件，获取上传token");
            //查找默认存储配置
            Storage storage = storageMapper.findDefaultStorage();

            // 获取七牛云上传token
            String uploadToken = KodoUtil.getUpToken(storage.getBucket());
            String[] domain = {storage.getEndpoint()};
            // 获取域名
            // 返回上传token给前端
            return Result.success(UploadResponse.withToken(storage.getStorageId(),domain,uploadToken));
        }
    }

    @Override
    @Transactional
    public Result uploadComplete(UserFile userFile) {
        log.info("处理上传完成: {}", userFile.getFileName());

        // 获取当前用户ID并设置
        Long userId = SecurityUtils.getCurrentUserId();
        userFile.setUserId(userId);

        // 设置文件相关属性
        userFile.setFolderType(0); // 0表示文件
        userFile.setDeleteFlag(0); // 0表示正常

        // 设置时间信息
        long currentTime = System.currentTimeMillis();
        userFile.setCreateTime(currentTime);
        userFile.setLastUpdateTime(currentTime);
        userFile.setFileCreateTime(currentTime); // 设置文件创建时间

        // 设置引用计数为1 (初始值)
        userFile.setReferCount(1);

        // 设置转码状态为未转码
        userFile.setTranscodeStatus(0);

        // 保存文件记录，fileId会在保存过程中被设置到userFile对象
        fileMapper.addFile(userFile);

        log.info("已保存文件记录，文件ID: {}", userFile.getFileId());

        // 添加到用户文件表
        fileMapper.addUserFile(userFile);

        // 更新用户使用空间
        userMapper.updateUserSpace(userId, userFile.getFileSize());

        log.info("文件上传完成，已保存记录。文件ID: {}", userFile.getFileId());

        return Result.success("文件上传成功",null);
    }

    /**
     * 根据文件ID获取其完整路径
     * @param fileId 文件ID
     * @return 完整路径字符串，格式为"/根目录/子目录/文件名"
     */
    private String getFilePath(Long fileId) {
        UserFile file = fileMapper.findUserFileById(fileId);
        if (file == null) {
            return "";
        }
        
        StringBuilder path = new StringBuilder();
        // 递归构建路径，直到找到根目录（pid=0）
        buildPath(file, path);
        
        return path.toString();
    }
    
    /**
     * 递归构建文件路径
     * @param file 当前文件
     * @param path 路径构建器
     */
    private void buildPath(UserFile file, StringBuilder path) {
        // 在路径前添加当前文件名
        path.insert(0, "/" + file.getFileName());
        
        // 如果pid为0，说明已经到达根目录，停止递归
        if (file.getFilePid() == 0) {
            return;
        }
        
        // 查找父文件夹并继续构建路径
        UserFile parentFile = fileMapper.findUserFileById(file.getFilePid());
        if (parentFile != null) {
            buildPath(parentFile, path);
        }
    }

    @Override
    public Result download(UserFile userFile) {
        // 检查文件是否存在
        UserFile existingFile = fileMapper.findByUserFileId(userFile.getId());
        System.out.println(existingFile);
        if (existingFile == null) {
            return Result.error("文件不存在");
        }
        // 文件存在
        // 检查文件是否已被放入回收站
        if (existingFile.getDeleteFlag() == 1) {
            return Result.error("文件已被删除");
        }
        //检查是否是文件夹
        if (existingFile.getFolderType() == 1) {
            // 文件夹
            log.info("下载文件夹: {}", existingFile.getFileName());
            List<DownloadFile> downloadFiles = new ArrayList<>();
            
            // 获取文件夹路径
            String folderPath = getFilePath(existingFile.getId());
            
            // 递归获取文件夹下所有文件并生成下载链接
            // 计算总大小
            long totalSize = getAllFilesInFolder(existingFile.getId(), downloadFiles, folderPath);
            
            if (downloadFiles.isEmpty()) {
                log.info("文件夹为空，无法下载");
                return Result.error("文件夹为空");
            }
            
            log.info("文件夹 {} 下载准备完成，共计 {} 个文件，总大小 {} 字节", existingFile.getFileName(), downloadFiles.size(), totalSize);
            
            // 检查所有文件的路径，如果有空路径则改为"/"
            for (DownloadFile df : downloadFiles) {
                if (df.getFilePath() == null || "".equals(df.getFilePath())) {
                    df.setFilePath("/");
                    log.info("修正文件 {} 的路径为: /", df.getFileName());
                }
            }
            
            return Result.success(DownloadResponse.withUrl(true, totalSize, downloadFiles));
        }else {
            // 文件
            UserFile download = fileMapper.findByFileId(existingFile.getFileId());
            System.out.println(download);
            String fileSHA256 = download.getFileSHA256();
            String key;
            // 带有效期
            long expireInSeconds = 3600*24*14;//两周，可以自定义链接过期时间
            try {
                // 生成下载链接
                key =generateDownloadKey(fileSHA256);
            }catch (IllegalArgumentException e) {
                return Result.error(e.getMessage());
            }
            String url;
            try {
                url = KodoUtil.getDownloadUrl(key, expireInSeconds);
            } catch (QiniuException e) {
                throw new RuntimeException(e);
            }
            DownloadFile downloadFile = new DownloadFile();
            downloadFile.setSHA256(download.getFileSHA256());
            downloadFile.setUrl(url);
            
            // 获取文件路径并设置
            String filePath = getFilePath(existingFile.getFilePid());
            downloadFile.setFilePath(filePath);
            downloadFile.setFileName(existingFile.getFileName());
            downloadFile.setFileExtension(existingFile.getFileExtension());
            downloadFile.setFolderType(false); // false表示文件
            // 使用原始字节大小
            downloadFile.setSize(download.getFileSize());
            
            List<DownloadFile> downloadFiles = new ArrayList<>();
            downloadFiles.add(downloadFile);
            log.info("文件 {} 下载准备完成，大小 {} 字节", existingFile.getFileName(), existingFile.getFileSize());
            
            // 检查文件路径是否为空，是则改为"/"
            if (downloadFiles.size() > 0 && (downloadFiles.get(0).getFilePath() == null || "".equals(downloadFiles.get(0).getFilePath()))) {
                downloadFiles.get(0).setFilePath("/");
                log.info("修正根目录文件路径为: /");
            }
            
            return Result.success(DownloadResponse.withUrl(false, download.getFileSize(), downloadFiles));
        }
    }

    /**
     * 递归获取文件夹下所有文件并生成下载链接
     * @param folderId 文件夹ID
     * @param downloadFiles 下载文件列表
     * @param parentPath 父路径
     * @return 文件夹总大小
     */
    private long getAllFilesInFolder(Long folderId, List<DownloadFile> downloadFiles, String parentPath) {
        // 获取文件夹下所有文件和子文件夹
        log.info("获取文件夹ID {} 下的所有文件和子文件夹", folderId);
        List<UserFile> files = fileMapper.findByFilePid(folderId);
        UserFile folderInfo = fileMapper.findUserFileById(folderId);
        log.info("文件夹信息: {}", folderInfo);
        
        if (files == null || files.isEmpty()) {
            log.info("文件夹ID {} 为空", folderId);
            
            // 添加空文件夹到响应列表
            if (folderInfo != null && folderInfo.getFolderType() == 1) {
                DownloadFile emptyFolder = new DownloadFile();
                emptyFolder.setUrl(null);
                emptyFolder.setFolderType(true);
                emptyFolder.setSize(null);
                
                // 对于根目录下的文件夹，设置路径为"/"
                if (folderInfo.getFilePid() == 0) {
                    emptyFolder.setFilePath("/");
                } else {
                    // 确保路径不包含当前文件夹名称
                    String folderPath = parentPath;
                    // 如果路径不为根目录，则去掉当前文件夹名称
                    if (!"/".equals(folderPath) && folderPath.lastIndexOf("/") > 0) {
                        folderPath = folderPath.substring(0, folderPath.lastIndexOf("/"));
                    }
                    emptyFolder.setFilePath(folderPath);
                }
                
                emptyFolder.setFileName(folderInfo.getFileName());
                emptyFolder.setFileExtension(null);
                emptyFolder.setSHA256(null);
                
                downloadFiles.add(emptyFolder);
                log.info("添加空文件夹: {}, 路径: {}", folderInfo.getFileName(), emptyFolder.getFilePath());
            }
            
            return 0;
        }
        
        log.info("文件夹ID {} 下有 {} 个文件或子文件夹", folderId, files.size());
        long totalSize = 0;
        
        // 先处理所有文件
        for (UserFile file : files) {
            // 跳过已删除的文件
            if (file.getDeleteFlag() == 1) {
                continue;
            }
            
            if (file.getFolderType() == 0) { // 只处理文件
                // 文件，生成下载链接
                UserFile fileInfo = fileMapper.findByFileId(file.getFileId());
                if (fileInfo != null) {
                    try {
                        String key = generateDownloadKey(fileInfo.getFileSHA256());
                        String url = KodoUtil.getDownloadUrl(key, 3600*24*14);
                        
                        DownloadFile downloadFile = new DownloadFile();
                        downloadFile.setSHA256(fileInfo.getFileSHA256());
                        downloadFile.setUrl(url);
                        downloadFile.setFilePath(parentPath);
                        // 检查路径是否为空
                        if (downloadFile.getFilePath() == null || "".equals(downloadFile.getFilePath())) {
                            downloadFile.setFilePath("/");
                            log.info("修正文件 {} 的路径为: /", file.getFileName());
                        }
                        downloadFile.setFileName(file.getFileName());
                        downloadFile.setFileExtension(file.getFileExtension());
                        downloadFile.setFolderType(false); // false表示文件
                        // 使用原始字节大小
                        downloadFile.setSize(file.getFileSize());
                        
                        downloadFiles.add(downloadFile);
                        
                        // 累加文件大小
                        totalSize += file.getFileSize();
                        log.info("处理文件: {}, 大小: {}, 路径: {}", file.getFileName(), file.getFileSize(), parentPath);
                    } catch (Exception e) {
                        log.error("获取文件下载链接失败: {}", e.getMessage());
                    }
                }
            }
        }
        
        // 再处理所有子文件夹
        for (UserFile file : files) {
            if (file.getDeleteFlag() == 1) {
                continue;
            }
            
            if (file.getFolderType() == 1) { // 处理文件夹
                // 子文件夹，递归处理
                String newPath = parentPath + "/" + file.getFileName();
                log.info("处理子文件夹: {}, 路径: {}", file.getFileName(), newPath);
                
                // 递归处理子文件夹
                totalSize += getAllFilesInFolder(file.getId(), downloadFiles, newPath);
            }
        }
        
        return totalSize;
    }
    
    /**
     * 根据文件SHA256哈希值生成多级目录下载路径
     * 格式：files/前8字符/次8字符/再次8字符/再次8字符/完整SHA256值
     *
     * @param fileSHA256 文件的SHA256哈希值
     * @return 生成的下载路径key
     */
    private String generateDownloadKey(String fileSHA256) {
//        if (fileSHA256 == null || fileSHA256.length() < 64) {
//            throw new IllegalArgumentException("Invalid SHA256 hash");
//        }

        // 按照指定规则分段生成目录
        String level1 = "files";
        String level2 = fileSHA256.substring(0, 8);
        String level3 = fileSHA256.substring(8, 16);
        String level4 = fileSHA256.substring(16, 24);
        String level5 = fileSHA256.substring(24, 32);

        // 组合路径
        return String.format("%s/%s/%s/%s/%s/%s",
                level1, level2, level3, level4, level5, fileSHA256);
    }
}
