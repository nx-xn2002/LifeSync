package com.nx.lifesyncbackend.controller;

import com.nx.lifesyncbackend.common.BaseResponse;
import com.nx.lifesyncbackend.common.utils.ResultUtils;
import com.nx.lifesyncbackend.model.dto.AnalyseRequest;
import com.nx.lifesyncbackend.model.dto.ListRecordRequest;
import com.nx.lifesyncbackend.model.entity.DetectionRecord;
import com.nx.lifesyncbackend.service.DetectionRecordService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * monitor controller
 *
 * @author nx-xn2002
 */
@RestController
@RequestMapping("/monitor")
@Slf4j
public class MonitorController {
    @Resource
    private DetectionRecordService detectionRecordService;


    @PostMapping("/analyse")
    public BaseResponse<DetectionRecord> analyse(@RequestBody AnalyseRequest request) throws IOException {
        DetectionRecord result = detectionRecordService.analyse(request.getImages(), request.getBasicHealth(),
                request.getUsername());
        return ResultUtils.success(result);
    }

    @PostMapping("/listDetectionRecord")
    public BaseResponse<List<DetectionRecord>> listDetectionRecord(@RequestBody ListRecordRequest request) {
        return ResultUtils.success(detectionRecordService.listByUsername(request.getUsername()));
    }
}