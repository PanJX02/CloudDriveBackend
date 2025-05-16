package com.panjx.clouddrive.service.impl;

import com.panjx.clouddrive.mapper.InvitationCodeMapper;
import com.panjx.clouddrive.pojo.InvitationCode;
import com.panjx.clouddrive.pojo.InviteCodeUsage;
import com.panjx.clouddrive.service.InvitationCodeService;
import com.panjx.clouddrive.pojo.response.InvitationCodeList;
import com.panjx.clouddrive.pojo.response.PageMeta;
import com.panjx.clouddrive.pojo.request.UpdateInvitationCodeRequest;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.CreateInvitationCodeRequest;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.Pattern;
import com.panjx.clouddrive.utils.SecurityUtil;

@Service
public class InvitationCodeServiceImpl implements InvitationCodeService {

    private static final Logger log = LoggerFactory.getLogger(InvitationCodeServiceImpl.class);

    @Autowired
    private InvitationCodeMapper invitationCodeMapper;

    @Override
    public InvitationCode validateInviteCode(String inviteCode) {
        if (inviteCode == null || inviteCode.trim().isEmpty()) {
            return null;
        }

        // 查询邀请码
        InvitationCode invitationCode = invitationCodeMapper.findByCode(inviteCode);
        if (invitationCode == null) {
            return null;
        }

        // 检查邀请码状态
        if (invitationCode.getStatus() != 1) {
            return null; // 邀请码已禁用
        }

        // 检查是否过期
        long currentTime = System.currentTimeMillis() / 1000; // 转换为秒级时间戳
        if (invitationCode.getExpirationTime() > 0 && currentTime > invitationCode.getExpirationTime()) {
            return null; // 邀请码已过期
        }

        // 检查使用次数是否达到上限
        if (invitationCode.getMaxUsage() > 0 && invitationCode.getUsageCount() >= invitationCode.getMaxUsage()) {
            return null; // 邀请码使用次数已达上限
        }

        return invitationCode;
    }

    @Override
    @Transactional
    public boolean recordInviteCodeUsage(InvitationCode invitationCode, Long userId, String ip) {
        if (invitationCode == null || userId == null) {
            return false;
        }

        try {
            // 增加邀请码使用次数
            invitationCodeMapper.incrementUsageCount(invitationCode.getId());

            // 记录邀请码使用情况
            InviteCodeUsage usage = new InviteCodeUsage();
            usage.setInviteCodeId(invitationCode.getId());
            usage.setUsedBy(userId);
            usage.setUsedAt(System.currentTimeMillis() / 1000); // 秒级时间戳
            usage.setUsedIp(ip);
            
            invitationCodeMapper.addUsageRecord(usage);
            
            return true;
        } catch (Exception e) {
            // 记录日志
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public InvitationCodeList getAllInvitationCodes(int page, int pageSize) {
        // 获取当前登录的管理员ID
        Long adminId = SecurityUtil.getCurrentAdminId();
        if (adminId == null) {
            // 如果不是管理员，返回空列表
            return new InvitationCodeList(List.of(), new PageMeta(0, 0, pageSize, page));
        }
        
        // 计算总数
        int total = invitationCodeMapper.countInvitationCodes();
        
        // 计算总页数
        int totalPage = (total + pageSize - 1) / pageSize;
        
        // 确保页码在有效范围内
        if (page < 1) {
            page = 1;
        }
        if (page > totalPage && totalPage > 0) {
            page = totalPage;
        }
        
        // 计算偏移量
        int offset = (page - 1) * pageSize;
        
        // 查询数据
        List<InvitationCode> invitationCodes = invitationCodeMapper.getInvitationCodesByPage(offset, pageSize);
        
        // 构造分页元数据
        PageMeta pageMeta = new PageMeta(total, totalPage, pageSize, page);
        
        // 构造返回结果
        return new InvitationCodeList(invitationCodes, pageMeta);
    }

    @Override
    public Result updateInvitationCode(UpdateInvitationCodeRequest request) {
        if (request.getId() == null) {
            return Result.error("邀请码ID不能为空");
        }
        
        // 获取当前登录的管理员ID
        Long adminId = SecurityUtil.getCurrentAdminId();
        if (adminId == null) {
            return Result.error("未获取到管理员信息，请重新登录");
        }
        
        try {
            // 如果提供了邀请码，验证格式
            if (request.getInviteCode() != null && !request.getInviteCode().isEmpty()) {
                // 验证邀请码格式
                if (!validateInviteCodeFormat(request.getInviteCode())) {
                    return Result.error("邀请码格式不正确，只能包含数字和字母，且最长16位");
                }
                
                // 检查新邀请码是否与其他邀请码冲突
                InvitationCode existingCode = invitationCodeMapper.findByCode(request.getInviteCode());
                if (existingCode != null && !existingCode.getId().equals(request.getId())) {
                    return Result.error("邀请码已存在");
                }
            }
            
            // 将请求对象转换为实体对象
            InvitationCode invitationCode = new InvitationCode();
            invitationCode.setId(request.getId());
            invitationCode.setInviteCode(request.getInviteCode());
            invitationCode.setUserId(request.getUserId());
            invitationCode.setAdminId(request.getAdminId() != null ? request.getAdminId() : adminId); // 如果没有指定新的adminId，则使用当前管理员ID
            invitationCode.setCreatedAt(request.getCreatedAt()); // 设置创建时间
            invitationCode.setExpirationTime(request.getExpirationTime());
            invitationCode.setStatus(request.getStatus());
            invitationCode.setUsageCount(request.getUsageCount()); // 设置使用次数
            invitationCode.setMaxUsage(request.getMaxUsage());
            invitationCode.setIdentity(request.getIdentity());
            
            // 执行更新操作
            int result = invitationCodeMapper.updateInvitationCode(invitationCode);
            return result > 0 ? Result.success("修改邀请码成功") : Result.error("修改邀请码信息失败");
        } catch (Exception e) {
            log.error("更新邀请码信息失败", e);
            return Result.error("更新邀请码信息失败：" + e.getMessage());
        }
    }

    @Override
    public Result createInvitationCode(CreateInvitationCodeRequest request) {
        try {
            // 获取当前登录的管理员ID
            Long adminId = SecurityUtil.getCurrentAdminId();
            if (adminId == null) {
                return Result.error("未获取到管理员信息，请重新登录");
            }
            
            InvitationCode invitationCode = new InvitationCode();
            
            // 设置邀请码字符串，如果未提供则自动生成
            String code = request.getInviteCode();
            if (code == null || code.trim().isEmpty()) {
                // 生成随机邀请码
                code = generateInviteCode();
            } else {
                // 验证邀请码格式
                if (!validateInviteCodeFormat(code)) {
                    return Result.error("邀请码格式不正确，只能包含数字和字母，且最长16位");
                }
                
                // 检查邀请码是否已存在
                InvitationCode existingCode = invitationCodeMapper.findByCode(code);
                if (existingCode != null) {
                    return Result.error("邀请码已存在");
                }
            }
            
            // 设置邀请码信息
            invitationCode.setInviteCode(code);
            invitationCode.setAdminId(adminId);
            invitationCode.setCreatedAt(System.currentTimeMillis() / 1000); // 当前时间（秒级时间戳）
            invitationCode.setExpirationTime(request.getExpirationTime() != null ? request.getExpirationTime() : 0L);
            invitationCode.setStatus(request.getStatus() != null ? request.getStatus() : 1);
            invitationCode.setUsageCount(0); // 新建邀请码，使用次数为0
            invitationCode.setMaxUsage(request.getMaxUsage() != null ? request.getMaxUsage() : 0);
            invitationCode.setIdentity(request.getIdentity() != null ? request.getIdentity() : 1);
            
            // 插入数据库
            int result = invitationCodeMapper.insertInvitationCode(invitationCode);
            if (result > 0) {
                return Result.success("创建邀请码成功");
            } else {
                return Result.error("创建邀请码失败");
            }
        } catch (Exception e) {
            log.error("创建邀请码失败", e);
            return Result.error("创建邀请码失败：" + e.getMessage());
        }
    }

    /**
     * 验证邀请码格式
     * @param code 邀请码
     * @return 是否符合格式要求
     */
    private boolean validateInviteCodeFormat(String code) {
        // 验证邀请码只包含数字和字母，且最多16位
        String regex = "^[a-zA-Z0-9]{1,16}$";
        return Pattern.matches(regex, code);
    }

    /**
     * 生成随机邀请码
     * @return 邀请码字符串
     */
    private String generateInviteCode() {
        // 使用UUID生成随机字符串，并截取一部分作为邀请码
        // 由于我们限制了邀请码格式，确保生成的邀请码符合要求
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return uuid.substring(0, 8); // 8位字母数字组合
    }

    @Override
    public Result deleteInvitationCode(Long id) {
        // 获取当前登录的管理员ID
        Long adminId = SecurityUtil.getCurrentAdminId();
        if (adminId == null) {
            return Result.error("未获取到管理员信息，请重新登录");
        }

        if (id == null) {
            return Result.error("邀请码ID不能为空");
        }

        try {
            int result = invitationCodeMapper.deleteInvitationCodeById(id);
            if (result > 0) {
                return Result.success("删除邀请码成功");
            } else {
                return Result.error("删除邀请码失败，可能邀请码不存在");
            }
        } catch (Exception e) {
            log.error("删除邀请码失败, ID: {}", id, e);
            return Result.error("删除邀请码失败：" + e.getMessage());
        }
    }
} 