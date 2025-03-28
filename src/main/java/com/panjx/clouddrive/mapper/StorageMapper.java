package com.panjx.clouddrive.mapper;

import com.panjx.clouddrive.pojo.Storage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StorageMapper {

    // 通过storageId查找storage
    Storage finByStorageId(Long storageId);

    //查找默认的storage
    Storage findDefaultStorage();
}
