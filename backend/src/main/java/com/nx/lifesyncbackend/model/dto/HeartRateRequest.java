package com.nx.lifesyncbackend.model.dto;

import lombok.Data;

import java.util.List;

/**
 * heart rate request
 *
 * @author nx-xn2002
 */
@Data
public class HeartRateRequest {
    private List<String> images;
}