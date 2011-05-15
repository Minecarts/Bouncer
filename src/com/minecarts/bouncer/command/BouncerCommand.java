package com.minecarts.bouncer.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.minecarts.bouncer.*;

public class BouncerCommand extends CommandHandler{
    
    public BouncerCommand(Bouncer plugin){
        super(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return true;
    }
}
