package com.panjx.clouddrive.service.file;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.MoveFileRequest;
import org.springframework.stereotype.Service;

@Service
public interface FileMoveService {
    Result moveFile(MoveFileRequest moveFileRequest);
}
