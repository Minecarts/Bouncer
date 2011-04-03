package com.minecarts.bouncer.helper;

public class BanInfo {
    public String reason;
    public String bannedBy;
    public BanInfo(String reason, String bannedBy){
        this.reason = reason;
        this.bannedBy = bannedBy;
    }
}
