package com.panjx.clouddrive.service.file;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.MoveFileRequest;
import org.springframework.stereotype.Service;

@Service
public interface FileMoveService {
    /**
     * 移动多个文件/文件夹到目标文件夹
     * @param moveFileRequest 包含文件/文件夹ID列表和目标文件夹ID的请求对象
     * @return 操作结果
     */
    Result moveFile(MoveFileRequest moveFileRequest);
}
