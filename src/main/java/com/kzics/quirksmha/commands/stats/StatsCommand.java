package com.kzics.quirksmha.commands.stats;

import com.kzics.quirksmha.commands.CommandBase;
import com.kzics.quirksmha.commands.stats.sub.CheckStatsCommand;
import com.kzics.quirksmha.manager.ManagerHandler;
import com.kzics.quirksmha.menu.StatsMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand extends CommandBase {
    private final ManagerHandler managerHandler;
    public StatsCommand(ManagerHandler managerHandler) {
        super(managerHandler);
        this.managerHandler = managerHandler;

        registerSubCommand("check", new CheckStatsCommand(managerHandler));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) {
            new StatsMenu(managerHandler).open((Player) sender);
            return true;
        }

        new CheckStatsCommand(managerHandler).execute(sender, args);

        return false;
    }
}
