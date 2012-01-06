package com.minecarts.bouncer.command;

import com.minecarts.bouncer.Bouncer;
import com.minecarts.bouncer.CommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class KickCommand extends CommandHandler {

    private int taskId = 0;

    public KickCommand(Bouncer plugin){
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("bouncer.kick")) return true;
        String reason = plugin.getConfig().getString("messages.KICK");
        List<Player> players = Bukkit.matchPlayer(args[0]);
        if(players.size() > 1 || players.size() == 0){
            sender.sendMessage("Error: Matched " + players.size() + " players when attempting to kick.");
            return true;
        }

        Player kickPlayer = players.get(0);

        if(args.length >= 2){
            reason = "";
            for(int i = 1; i < args.length; i++){
                reason = reason.concat(args[i]);
                if(i != args.length){
                    reason = reason.concat(" ");
                }
            }
        }

        kickPlayer.kickPlayer(reason);
        return true;
    }


}
