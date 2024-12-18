package com.kzics.quirksmha.commands.abilities;

import com.kzics.quirksmha.commands.CommandBase;
import com.kzics.quirksmha.manager.ManagerHandler;
import com.kzics.quirksmha.menu.QuirksAbilityMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuirkAbilitiesCommand extends CommandBase {
    private final ManagerHandler managerHandler;

    public QuirkAbilitiesCommand(ManagerHandler managerHandler) {
        super(managerHandler);
        this.managerHandler = managerHandler;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to use this command");
            return true;
        }

        new QuirksAbilityMenu(managerHandler.quirkManager()).open(player);
        return true;
    }
}
