package com.mingzhi.controller.center;

import com.mingzhi.controller.BaseController;
import com.mingzhi.pojo.Users;
import com.mingzhi.pojo.bo.center.CenterUserBO;
import com.mingzhi.pojo.vo.UsersVO;
import com.mingzhi.service.center.CenterUserService;
import com.mingzhi.utils.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@Tag(name = "用户信息中心", description = "用户信息中心")
@RestController()
@ResponseBody()
@RequestMapping("userInfo")
public class CenterUserController extends BaseController {
    @Autowired
    private CenterUserService centerUserService;

    @Autowired
    private Environment env;

    @Operation(summary = "修改用户信息", description = "修改用户信息", method = "POST")
    @PostMapping("update")
    public MingzhiJSONResult updateUserInfo(
            @Parameter(name = "userId", required = true, description = "用户id")
            @RequestParam String userId,
            @Parameter(name = "centerUserBO", required = true, description = "用户中心BO1")
            @RequestBody @Valid CenterUserBO centerUserBO,
            BindingResult validationResult,
            HttpServletRequest request,
            HttpServletResponse response) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (validationResult.hasErrors()) {
            Map<String, String> map = ValidationUtil.getErrors(validationResult);
            return MingzhiJSONResult.errorMap(map);

        }
        Users user = centerUserService.updateUserInfo(userId, centerUserBO);

//        PojoUtils.setPojoNullProperty(user, new String[]{
//                "password",
//                "mobile",
//                "email",
//                "updatedTime",
//                "birthday",
//                "createdTime",
//        });
        UsersVO usersVO = conventUserToUsersVO(user);
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(usersVO), true);

        return MingzhiJSONResult.ok(user);

    }

    @Operation(summary = "修改用户头像", description = "修改用户头像", method = "POST")
    @PostMapping("uploadFace")
    public MingzhiJSONResult uploadUserFace(
            @Parameter(name = "userId", required = true, description = "用户id")
            @RequestParam String userId,
            MultipartFile file,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
//
        String fileSpace = "../" + env.getProperty("baseCfg.fileUploadUrl");
        fileSpace = fileSpace.replace("/", File.separator);
        String uploadPathPrefix = File.separator + userId;
        String newFileName = null;
        if (file != null) {
            String fileName = file.getOriginalFilename();
            if (StringUtils.isNotBlank(fileName)) {
                String[] fileNames = fileName.split("\\.");
                String suffix = fileNames[fileNames.length - 1];
                if (!(suffix.equalsIgnoreCase("png")
                        || suffix.equalsIgnoreCase("ipg")
                        || suffix.equalsIgnoreCase("jpeg"))) {
                    return MingzhiJSONResult.errorMsg("图片格式不正确，请上传png,jpeg,jpeg格式图片");

                }
                newFileName = "face-" + userId + "." + suffix;
                String facePath = fileSpace + uploadPathPrefix + File.separator + newFileName;
                File imgFile = new File(facePath);
                if (imgFile.getParentFile() != null) {
                    imgFile.getParentFile().mkdirs();
                }
                FileOutputStream outputStream = new FileOutputStream(imgFile);
                InputStream inputStream = file.getInputStream();
                IOUtils.copy(inputStream, outputStream);
                outputStream.flush();
                outputStream.close();
            }
        } else {
            return MingzhiJSONResult.errorMsg("不能上传空文件");
        }
        // 时间戳?t=1123445是防止浏览器可能存在的缓存情况
        String fileUrl = "http://localhost:"
                + env.getProperty("server.port")
                + "/"
                + userId + "/"
                + newFileName
                + "?t="
                + DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN);
        Users user = centerUserService.updateUserFace(userId, fileUrl);
        UsersVO usersVO = conventUserToUsersVO(user);
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(usersVO), true);
        return MingzhiJSONResult.ok();


    }


}
