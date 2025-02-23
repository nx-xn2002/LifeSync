package com.nx.lifesyncbackend.model.dto;

import com.nx.lifesyncbackend.model.entity.BasicHealth;
import lombok.Data;

import java.util.List;

/**
 * heart rate request
 *
 * @author nx-xn2002
 */
@Data
public class AnalyseRequest {
    private List<String> images;
    private String username;
    private BasicHealth basicHealth;
}