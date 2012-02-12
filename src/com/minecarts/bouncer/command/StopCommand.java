package com.minecarts.bouncer.command;

import com.minecarts.bouncer.Bouncer;
import com.minecarts.bouncer.CommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.MessageFormat;
import java.util.ArrayList;

import static org.bukkit.Bukkit.*;


public class StopCommand extends CommandHandler {

    private int taskId = 0;

    public StopCommand(Bouncer plugin){
        super(plugin);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(!sender.hasPermission("bouncer.stop")) return true;

        Integer minutes;
        if(args.length > 0 && args[0].equalsIgnoreCase("abort")){
            if(taskId != 0){
                getScheduler().cancelTask(taskId);
                taskId = 0;
                plugin.log("Server shutdown sequence aborted by " + sender.getName());
                //getServer().broadcastMessage(ChatColor.YELLOW + "Server shutdown sequence aborted."); //Not sure we want to do this
            } else {
                sender.sendMessage("There is no shutdown sequence currently active.");
            }
            return true;
        }

        if(taskId != 0){
            sender.sendMessage("Shutdown sequence is already started, please abort it before starting another.");
            return true;
        }

        //Start the shutdown sequence
        if(args.length == 0 || args[0].equalsIgnoreCase("now")){
            minutes = 0;
        } else {
            try{
                minutes = Integer.parseInt(args[0]);
            } catch (NumberFormatException e){
                sender.sendMessage("Unknown shutdown countdown time: " + args[0]);
                return false;
            }
        }

        sender.sendMessage("Shutdown task started.");
        plugin.log("Shutdown task started by " + sender.getName() + " for " + minutes  + " minutes");


        //Determine the shutdown message from the command
        String am = plugin.getConfig().getString("messages.SHUTDOWN_ANNOUNCE");
        String km = plugin.getConfig().getString("messages.SHUTDOWN_KICK");
        if(label.equalsIgnoreCase("restart")){
            am = plugin.getConfig().getString("messages.RESTART_ANNOUNCE");
            km = plugin.getConfig().getString("messages.RESTART_KICK");
        }
        final String announceMessage = am;
        final String kickMessage = km;
        
        final Integer startingSeconds = minutes * 60;
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,new Runnable() {
            private int remainingSeconds = startingSeconds + 1;
            public void run() {
                this.remainingSeconds--;
                int remainingMinutes = remainingSeconds / 60;
                if(remainingMinutes > 0){ //Handle the minutes
                    if((remainingMinutes >= 5 && remainingSeconds % (5 * 60) == 0) || remainingSeconds == 2 * 60){
                        broadcastMessage(ChatColor.YELLOW + MessageFormat.format(announceMessage,remainingMinutes,"minutes"));
                    }
                    if(remainingSeconds == 1 * 60){
                        broadcastMessage(ChatColor.YELLOW + MessageFormat.format(announceMessage,remainingMinutes,"minute"));
                    }
                    return;
                }

                if(remainingSeconds == 30 || remainingSeconds == 15 || remainingSeconds == 5){
                    broadcastMessage(ChatColor.YELLOW + MessageFormat.format(announceMessage,remainingSeconds,"seconds"));
                    return;
                }

                if(remainingSeconds == 0){ //Shut down NOW!
                    //Activate the login lock
                    plugin.getConfig().set("locked", true);

                    //Kick players every N seconds until no more players online
                    plugin.kickTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,new Runnable(){
                        public void run() {
                            int t = 0;
                            int groupSize = plugin.getConfig().getInt("kick_group");
                            for(Player p : getOnlinePlayers()){
                                if(++t > groupSize) break; //Only kick N players at a time
                                p.kickPlayer(ChatColor.GRAY + kickMessage);
                            }
                            int numPlayersRemaining = getOnlinePlayers().length;
                            if(numPlayersRemaining == 0){
                                plugin.log("All players have been kicked. Shutting down server.");
                                Bukkit.getScheduler().cancelTask(plugin.kickTaskId);

                                getScheduler().cancelTask(taskId);
                                Bukkit.getServer().shutdown();

                            } else {
                                plugin.log("Kicked " + (t - 1) + " players. " + numPlayersRemaining + " players remaining.");
                            }
                        }
                    },1,20 * plugin.getConfig().getInt("kick_delay"));

                    if(label.equalsIgnoreCase("restart")){
                        Long osProcessId = Long.valueOf(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
                        final ArrayList<String> command = new ArrayList<String>();
                        command.add(plugin.getConfig().getString("restart_script"));
                        command.add(osProcessId.toString());
                        final ProcessBuilder builder = new ProcessBuilder(command);
                        try{
                            builder.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                            plugin.log("Could not launch restart process");
                        }
                    }
                    return;
                }
            }
        },0, 20);
        if(taskId == -1){
            plugin.log("Error creating shutdown task");
        }
        return true;
    }
}
