package com.nx.lifesyncbackend.controller;

import com.nx.lifesyncbackend.common.BaseResponse;
import com.nx.lifesyncbackend.common.utils.ResultUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * monitor controller
 *
 * @author nx-xn2002
 */
@RestController
@RequestMapping("/monitor")
public class MonitorController {
    @PostMapping("/photo")
    public BaseResponse<Integer> getPhoto(List<MultipartFile> fileList) {
        return ResultUtils.success(fileList.size());
    }
}
