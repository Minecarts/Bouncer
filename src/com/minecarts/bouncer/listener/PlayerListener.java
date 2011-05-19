package com.minecarts.bouncer.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.entity.Player;
import com.minecarts.bouncer.Bouncer;
import java.text.MessageFormat;

public class PlayerListener extends org.bukkit.event.player.PlayerListener{
    private Bouncer plugin;
    private java.util.HashMap<String, Integer> playerFlagged = new java.util.HashMap<String, Integer>();
    
    public PlayerListener(Bouncer plugin){
        this.plugin = plugin;
    }
    
    @Override
    public void onPlayerPreLogin(PlayerPreLoginEvent e){
        String reason = plugin.dbHelper.isIdentiferBanned(e.getAddress().toString());
        if(reason != null){
            e.setResult(PlayerPreLoginEvent.Result.KICK_BANNED);
            e.setKickMessage("You have been banned: " + reason);
        }
    }
    @Override
    public void onPlayerLogin(PlayerLoginEvent e){
        String reason = plugin.dbHelper.isIdentiferBanned(e.getPlayer().getName());
        if(reason != null){
            e.setResult(PlayerLoginEvent.Result.KICK_BANNED);
            e.setKickMessage(reason);
            return;
        }
        if(e.getResult() == Result.KICK_FULL){
            if(plugin.objectData.shared.get(e.getPlayer(), "subscriptionType") != null){
                Player[] online = Bukkit.getServer().getOnlinePlayers();
                online[online.length - 1].kickPlayer(plugin.fullMessage); //Kick the most recent connecting player
                e.setResult(Result.ALLOWED); //And let the subscriber connect
                return;
            }
            e.setKickMessage(plugin.fullMessage);
        }
    }
    @Override
    public void onPlayerJoin(PlayerJoinEvent e){
        String playerName = e.getPlayer().getName();
        String playerDisplayName = e.getPlayer().getDisplayName();
        String message = plugin.dbHelper.getJoinMessage(playerName);
        if(message != null){
            if(message.equals("")) e.setJoinMessage(null);
            else e.setJoinMessage(MessageFormat.format("{0}" + message,ChatColor.GRAY,playerDisplayName));
        } else if(plugin.dbHelper.getKey("joinCount", playerName) == null){
            e.setJoinMessage(ChatColor.WHITE + playerDisplayName + " has joined the server for the first time!");
        } else {
            e.setJoinMessage(ChatColor.GRAY + playerDisplayName + ChatColor.GRAY + " has joined the server.");
        }

        if(this.playerFlagged.containsKey(playerName)){
            Integer taskId = this.playerFlagged.remove(playerName);
            e.setJoinMessage(null);  //They rejoined, no join message
            if(taskId != null){
                Bukkit.getServer().getScheduler().cancelTask(taskId); //Cancel leave message from showing
            }
        }
    }
    @Override
    public void onPlayerQuit(PlayerQuitEvent e){
        String playerName = e.getPlayer().getName();
        String playerDisplayName = e.getPlayer().getDisplayName();
        String message = plugin.dbHelper.getQuitMessage(playerName);
        if(message != null){
            if(message.equals("")) e.setQuitMessage(null);
            else e.setQuitMessage(MessageFormat.format("{0}" + message,ChatColor.GRAY,playerDisplayName));
        } else {
            e.setQuitMessage(ChatColor.GRAY + playerDisplayName + ChatColor.GRAY + " has left the server.");
        }
        this.delayedOptionalMessage(e.getQuitMessage(), playerName);
        e.setQuitMessage(null);
    }
    @Override
    public void onPlayerKick(PlayerKickEvent e){
        String playerName = e.getPlayer().getName();
        String playerDisplayName = e.getPlayer().getDisplayName();
        String message = plugin.dbHelper.getQuitMessage(playerName);
        if(message != null){
            if(message.equals("")) e.setLeaveMessage(null);
            else e.setLeaveMessage(MessageFormat.format("{0}" + message,ChatColor.GRAY,playerDisplayName));
        } else {
            e.setLeaveMessage(ChatColor.GRAY + playerDisplayName + ChatColor.GRAY + " has left the server.");
        }
    }
    
    
    //
    private void delayedOptionalMessage(String message, String playerName){
        Runnable delayedSend = new DelayedSend(message, playerName, plugin);
        int taskId = plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin,delayedSend,20 * 5); //5 seconds later
        this.playerFlagged.put(playerName, taskId);
    }
    
    private class DelayedSend implements Runnable{
        private String message;
        private String playerName;
        private Bouncer plugin;
        public DelayedSend(String message, String playerName, Bouncer plugin){
            this.message = message;
            this.playerName = playerName;
            this.plugin = plugin;
        }
        
        public void run(){
            Integer taskId = plugin.playerListener.playerFlagged.remove(playerName);
            Bukkit.getServer().broadcastMessage(message);
        }
    }
    
}
