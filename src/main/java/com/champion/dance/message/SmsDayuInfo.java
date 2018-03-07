package com.champion.dance.message;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SmsDayuInfo {
    private String phoneNumber;
    private String signName;
    private String templateParam;//json str
    private String templateCode;
}