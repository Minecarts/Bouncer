package com.minecarts.bouncer.listener;

import com.minecarts.bouncer.helper.LoginStatus;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.entity.Player;
import com.minecarts.bouncer.Bouncer;

import java.text.MessageFormat;

import com.minecarts.barrenschat.cache.CacheIgnore;

public class PlayerListener extends org.bukkit.event.player.PlayerListener{
    private Bouncer plugin;
    private java.util.HashMap<String, Integer> playerFlagged = new java.util.HashMap<String, Integer>();
    
    public PlayerListener(Bouncer plugin){
        this.plugin = plugin;
    }

//Bans and whitelist support
    @Override
    public void onPlayerLogin(PlayerLoginEvent e){

        //Do our ban and whitelist queries (sync)
        plugin.doIdentifierCheck(e.getKickMessage(),e.getPlayer().getName());
        if(plugin.getConfig().getBoolean("whitelist")){
            plugin.doWhitelistCheck(e.getKickMessage(), e.getPlayer().getName());
        }

        //Check the status of the bans
        LoginStatus status = plugin.getLoginStatus(e.getPlayer().getName());
        if(status.isBanned){
            e.setResult(PlayerLoginEvent.Result.KICK_BANNED);
            e.setKickMessage(status.banReason);
            return;
        }

        //Development / op debug mode
        if(plugin.getConfig().getBoolean("locked",false)){
            if(!e.getPlayer().hasPermission("bouncer.admin")){
                e.setKickMessage(ChatColor.GRAY + plugin.getConfig().getString("messages.ADMIN_LOCK"));
                e.setResult(PlayerLoginEvent.Result.KICK_BANNED);
                return;
            }
        }

        //Check the status of the whitelist, if whitelisting is enabled
        if(plugin.getConfig().getBoolean("whitelist")){
            switch(status.whitelistStatus){
                case NOT_ON_LIST:
                    e.setKickMessage(Bouncer.whitelistMissing);
                    e.setResult(PlayerLoginEvent.Result.KICK_BANNED);
                    return;
                case EXPIRED:
                    e.setKickMessage(Bouncer.whitelistExpired);
                    e.setResult(PlayerLoginEvent.Result.KICK_BANNED);
                    return;
            }
        }

        //Check if the server is full if a sub is connecting
        if(e.getResult() == Result.KICK_FULL){
            if(e.getPlayer().hasPermission("subscriber")){
                Player[] online = Bukkit.getServer().getOnlinePlayers();
                online[online.length - 1].kickPlayer(Bouncer.fullMessage); //Kick the most recent connecting player
                e.setResult(Result.ALLOWED); //And let the subscriber connect
                return;
            }
            e.setKickMessage(Bouncer.fullMessage);
        }
    }
//Login messages
    @Override
    public void onPlayerJoin(PlayerJoinEvent e){
        e.setJoinMessage(null);
        plugin.doLoginMessage(e.getPlayer());
    }
    @Override
    public void onPlayerQuit(PlayerQuitEvent e){
      //Always clear the message, because we send it to all players ourselves for ignore list support
        e.setQuitMessage(null);
        plugin.doLogoutMessage(e.getPlayer());
    }

    //Clear any kick or timeout messages
    @Override
    public void onPlayerKick(PlayerKickEvent e){
        e.setLeaveMessage(null);
    }


    
}