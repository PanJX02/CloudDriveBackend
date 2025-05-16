package com.panjx.clouddrive.controller;

import com.panjx.clouddrive.pojo.AdminDTO;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.AdminUpdateUserInfoRequest;
import com.panjx.clouddrive.pojo.request.CreateAnnouncementRequest;
import com.panjx.clouddrive.pojo.request.PageRequest;
import com.panjx.clouddrive.pojo.request.UpdateAdminRequest;
import com.panjx.clouddrive.pojo.request.UpdateAnnouncementRequest;
import com.panjx.clouddrive.pojo.request.UpdateFileRequest;
import com.panjx.clouddrive.pojo.request.UpdateShareRequest;
import com.panjx.clouddrive.pojo.request.UpdateInvitationCodeRequest;
import com.panjx.clouddrive.pojo.request.CreateInvitationCodeRequest;
import com.panjx.clouddrive.pojo.response.InvitationCodeList;
import com.panjx.clouddrive.pojo.response.RestResponse;
import com.panjx.clouddrive.service.admin.AdminAnnouncementService;
import com.panjx.clouddrive.service.admin.AdminService;
import com.panjx.clouddrive.service.InvitationCodeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admins")
public class AdminController {

    @Autowired
    private AdminService adminService;
    
    @Autowired
    private AdminAnnouncementService adminAnnouncementService;
    
    @Autowired
    private InvitationCodeService invitationCodeService;
    
    /**
     * 添加管理员（仅超级管理员可操作）
     * 
     * 请求参数：
     * - adminName: 管理员名称
     * - password: 密码
     * - identity: 身份（0:超级管理员，1:普通管理员），默认为1
     */
    @PostMapping
    public Result addAdmin(@Valid @RequestBody AdminDTO adminDTO, BindingResult bindingResult) {
        // 检查验证结果
        log.info("添加管理员");
        if (bindingResult.hasErrors()) {
            // 获取第一个错误信息
            String errorMessage = bindingResult.getAllErrors().getFirst().getDefaultMessage();
            return Result.error(errorMessage);
        }
        
        // 添加管理员
        return adminService.addAdmin(adminDTO);
    }
    
    /**
     * 获取所有文件信息（分页）
     */
    @GetMapping("/files")
    public Result getAllFiles(PageRequest pageRequest) {
        log.info("管理员获取所有文件信息，页码：{}，每页数量：{}", pageRequest.getPage(), pageRequest.getPageSize());
        return adminService.getAllFiles(pageRequest);
    }
    
    /**
     * 修改文件信息
     */
    @PutMapping("/files")
    public Result updateFileInfo(@RequestBody UpdateFileRequest updateFileRequest) {
        log.info("管理员修改文件信息，文件ID：{}", updateFileRequest.getFileId());
        if (updateFileRequest.getFileId() == null) {
            return Result.error("文件ID不能为空");
        }
        return adminService.updateFileInfo(updateFileRequest);
    }

    /**
     * 获取所有分享信息
     *
     * @param pageRequest 分页请求参数
     * @return 分页后的分享列表
     */
    @GetMapping("/shares")
    public Result getAllShares(PageRequest pageRequest) {
        log.info("管理员获取所有分享信息，页码：{}，每页数量：{}", pageRequest.getPage(), pageRequest.getPageSize());
        return adminService.getAllShares(pageRequest);
    }

    /**
     * 修改分享信息
     *
     * @param updateShareRequest 更新分享请求参数
     * @return 修改结果
     */
    @PutMapping("/shares")
    public Result updateShare(@RequestBody UpdateShareRequest updateShareRequest) {
        log.info("管理员修改分享信息，分享ID：{}", updateShareRequest.getShareId());
        return adminService.updateShare(updateShareRequest);
    }
    
    /**
     * 获取所有用户信息（分页）
     */
    @GetMapping("/users")
    public Result getAllUsers(PageRequest pageRequest) {
        log.info("管理员获取所有用户信息，页码：{}，每页数量：{}", pageRequest.getPage(), pageRequest.getPageSize());
        return adminService.getAllUsers(pageRequest);
    }
    
    /**
     * 获取用户详情
     */
    @GetMapping("/users/{userId}")
    public Result getUserDetail(@PathVariable("userId") Long userId) {
        log.info("管理员获取用户详情，用户ID：{}", userId);
        return adminService.getUserDetail(userId);
    }
    
    /**
     * 修改用户信息
     */
    @PutMapping("/users")
    public Result updateUserInfo(@RequestBody AdminUpdateUserInfoRequest request) {
        log.info("管理员修改用户信息，用户ID：{}", request.getUserId());
        return adminService.updateUserInfo(request);
    }
    
    /**
     * 获取所有管理员信息（分页）
     */
    @GetMapping
    public Result getAllAdmins(PageRequest pageRequest) {
        log.info("获取所有管理员信息，页码：{}，每页数量：{}", pageRequest.getPage(), pageRequest.getPageSize());
        return adminService.getAllAdmins(pageRequest);
    }
    
    /**
     * 修改管理员信息
     */
    @PutMapping
    public Result updateAdmin(@RequestBody UpdateAdminRequest request) {
        log.info("修改管理员信息，管理员ID：{}", request.getId());
        if (request.getId() == null) {
            return Result.error("管理员ID不能为空");
        }
        return adminService.updateAdmin(request);
    }

    /**
     * 获取所有公告信息（分页）
     * @param pageRequest 分页请求参数
     * @return 分页后的公告列表
     */
    @GetMapping("/announcements")
    public Result getAllAnnouncements(PageRequest pageRequest) {
        log.info("管理员获取所有公告信息，页码：{}，每页数量：{}", pageRequest.getPage(), pageRequest.getPageSize());
        return adminAnnouncementService.getAllAnnouncements(pageRequest);
    }
    
    /**
     * 修改公告信息
     * @param request 修改公告请求参数
     * @return 修改结果
     */
    @PutMapping("/announcements")
    public Result updateAnnouncement(@RequestBody UpdateAnnouncementRequest request) {
        log.info("管理员修改公告信息，公告ID：{}", request.getId());
        if (request.getId() == null) {
            return Result.error("公告ID不能为空");
        }
        return adminAnnouncementService.updateAnnouncement(request);
    }
    
    /**
     * 创建公告
     * @param request 创建公告请求参数
     * @return 创建结果
     */
    @PostMapping("/announcements")
    public Result createAnnouncement(@RequestBody CreateAnnouncementRequest request) {
        log.info("管理员创建公告，标题：{}", request.getTitle());
        return adminAnnouncementService.createAnnouncement(request);
    }

    /**
     * 获取所有邀请码（带分页）
     * @param pageRequest 分页请求参数
     * @return 邀请码列表及分页信息
     */
    @GetMapping("/invitation-codes")
    public Result getAllInvitationCodes(PageRequest pageRequest) {
        log.info("管理员获取所有邀请码信息，页码：{}，每页数量：{}", pageRequest.getPage(), pageRequest.getPageSize());
        InvitationCodeList invitationCodeList = invitationCodeService.getAllInvitationCodes(pageRequest.getPage(), pageRequest.getPageSize());
        return Result.success(invitationCodeList);
    }

    /**
     * 修改邀请码信息
     * @param request 修改邀请码请求参数
     * @param bindingResult 验证结果
     * @return 修改结果
     */
    @PutMapping("/invitation-codes")
    public Result updateInvitationCode(@Valid @RequestBody UpdateInvitationCodeRequest request, BindingResult bindingResult) {
        log.info("管理员修改邀请码信息，邀请码ID：{}", request.getId());
        
        // 检查验证结果
        if (bindingResult.hasErrors()) {
            // 获取第一个错误信息
            String errorMessage = bindingResult.getAllErrors().getFirst().getDefaultMessage();
            return Result.error(errorMessage);
        }
        
        return invitationCodeService.updateInvitationCode(request);
    }

    /**
     * 创建邀请码
     * @param request 创建邀请码请求
     * @param bindingResult 验证结果
     * @return 创建结果
     */
    @PostMapping("/invitation-codes")
    public Result createInvitationCode(@Valid @RequestBody CreateInvitationCodeRequest request, 
                                      BindingResult bindingResult) {
        // 检查验证结果
        if (bindingResult.hasErrors()) {
            // 获取第一个错误信息
            String errorMessage = bindingResult.getAllErrors().getFirst().getDefaultMessage();
            return Result.error(errorMessage);
        }
        
        log.info("管理员创建邀请码");
        return invitationCodeService.createInvitationCode(request);
    }

    /**
     * 根据ID删除邀请码
     * @param id 邀请码ID
     * @return 删除结果
     */
    @DeleteMapping("/invitation-codes/{id}")
    public Result deleteInvitationCode(@PathVariable Long id) {
        log.info("管理员删除邀请码，ID: {}", id);
        return invitationCodeService.deleteInvitationCode(id);
    }
    
    /**
     * 根据ID删除管理员
     * @param id 管理员ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result deleteAdmin(@PathVariable Long id) {
        log.info("超级管理员删除管理员，ID: {}", id);
        return adminService.deleteAdmin(id);
    }
} 