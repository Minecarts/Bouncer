package com.minecarts.bouncer;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.event.*;
import org.bukkit.ChatColor;
import com.minecarts.dbconnector.DBConnector;
import com.minecarts.bouncer.command.BouncerCommand;
import com.minecarts.bouncer.listener.*;
import com.minecarts.bouncer.helper.DBHelper;

public class Bouncer extends org.bukkit.plugin.java.JavaPlugin{
	public final Logger log = Logger.getLogger("com.minecarts.bouncer");
	public DBConnector dbc;
	public DBHelper dbHelper;
	public final String fullMessage = ChatColor.GRAY + "Server is full. Please visit " + ChatColor.YELLOW + "Minecarts.com" + ChatColor.GRAY + " to get a guaranteed slot.";
	
	private final PlayerListener playerListener = new PlayerListener(this);

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        PluginDescriptionFile pdf = getDescription();
        dbc = (DBConnector) pm.getPlugin("DBConnector");
        dbHelper = new DBHelper(this);

        //Register our events
        pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Low, this);
        pm.registerEvent(Event.Type.PLAYER_PRELOGIN, this.playerListener, Event.Priority.Low, this);
        pm.registerEvent(Event.Type.PLAYER_LOGIN, this.playerListener, Event.Priority.Low, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Event.Priority.Monitor, this);
        
        //Register commands
        getCommand("bouncer").setExecutor(new BouncerCommand(this));
        
        log.info("[" + pdf.getName() + "] version " + pdf.getVersion() + " enabled.");
    }
    
    public void onDisable(){
        
    }
}
