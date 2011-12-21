package com.minecarts.bouncer.helper;


public class LoginStatus {
    public enum WhitelistStatus{
        NOT_ON_LIST,
        EXPIRED,
        OK
    }
    
    public LoginStatus(boolean isBanned, String banReason){
        this.isBanned = isBanned;
        this.banReason = banReason;
    }

    public LoginStatus(WhitelistStatus status){
        this.whitelistStatus = status;
    }

    public boolean isBanned = false;
    public WhitelistStatus whitelistStatus = WhitelistStatus.NOT_ON_LIST;
    public String banReason = null;
}
