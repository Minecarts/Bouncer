package com.minecarts.bouncer;

import java.util.logging.Logger;

import com.minecarts.dbquery.DBQuery;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.event.*;
import org.bukkit.ChatColor;
import com.minecarts.bouncer.command.BouncerCommand;
import com.minecarts.bouncer.listener.*;
import com.minecarts.bouncer.helper.DBHelper;
import com.minecarts.objectdata.ObjectData;
import com.minecarts.barrenschat.BarrensChat;
import org.bukkit.Bukkit;

public class Bouncer extends org.bukkit.plugin.java.JavaPlugin{
    public final Logger log = Logger.getLogger("com.minecarts.bouncer");
    public DBQuery dbq;
    public ObjectData objectData;
    public BarrensChat barrensChat;

    public final static String fullMessage = ChatColor.GRAY + "Server is full. Please visit " + ChatColor.YELLOW + "Minecarts.com" + ChatColor.GRAY + " to get a guaranteed slot.";
    public final static String whitelistMissing = ChatColor.GRAY + "Please visit " + ChatColor.YELLOW + "Minecarts.com" + ChatColor.GRAY + " to add your name to our whitelist.";
    public final static String whitelistExpired = ChatColor.GRAY + "Your whitelist entry has expired. Please visit " + ChatColor.YELLOW + "Minecarts.com" + ChatColor.GRAY + " to reapply.";
    public final static String maintenance = ChatColor.GRAY + "Our admins are working on updating the server currently, please try again soon!";

    public final PlayerListener playerListener = new PlayerListener(this);

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        PluginDescriptionFile pdf = getDescription();
        dbq = (DBQuery) pm.getPlugin("DBQuery");
        objectData = (ObjectData) pm.getPlugin("ObjectData");
        barrensChat = (BarrensChat) pm.getPlugin("BarrensChat");

        //Register our events
        pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Low, this);
        pm.registerEvent(Event.Type.PLAYER_PRELOGIN, this.playerListener, Event.Priority.Low, this);
        pm.registerEvent(Event.Type.PLAYER_LOGIN, this.playerListener, Event.Priority.Low, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Event.Priority.Low, this);
        pm.registerEvent(Event.Type.PLAYER_KICK, this.playerListener, Event.Priority.Low, this);

        //Register commands
        getCommand("bouncer").setExecutor(new BouncerCommand(this));

        log.info("[" + pdf.getName() + "] version " + pdf.getVersion() + " enabled.");
    }

    public void onDisable(){
        this.getServer().getScheduler().cancelTasks(this);
    }
}
