package com.kzics.quirksmha.commands.stats.sub;

import com.kzics.quirksmha.commands.ICommand;
import com.kzics.quirksmha.data.PlayerData;
import com.kzics.quirksmha.manager.ManagerHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckStatsCommand implements ICommand {

    private final ManagerHandler managerHandler;

    public CheckStatsCommand(ManagerHandler managerHandler) {
        this.managerHandler = managerHandler;
    }

    @Override
    public String getName() {
        return "check";
    }

    @Override
    public String getDescription() {
        return "Check your stats.";
    }

    @Override
    public String getPermission() {
        return "quirksmha.stats.check";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return;

        PlayerData playerData = managerHandler.playerDataManager().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            sender.sendMessage(Component.text("Player data not found!", NamedTextColor.RED));
            return;
        }

        sender.sendMessage(Component.text("=== Your Stats ===", NamedTextColor.GOLD));

        for (PlayerData.Stat stat : PlayerData.Stat.values()) {
            int level = playerData.getStatLevel(stat);
            Component progressBar = createProgressBar(level, 8);
            String rank = getRankFromLevel(level);

            sender.sendMessage(Component.text()
                    .append(Component.text(stat.name() + ": ", NamedTextColor.AQUA))
                    .append(progressBar)
                    .append(Component.text(" " + rank, NamedTextColor.YELLOW))
            );
        }

        sender.sendMessage(Component.text("==================", NamedTextColor.GOLD));
    }


    private Component createProgressBar(int currentLevel, int maxLevel) {
        int filled = Math.min(currentLevel, maxLevel);
        int empty = maxLevel - filled;

        Component filledBars = Component.text("᠆".repeat(filled), TextColor.color(0x00FF00)); // Vert
        Component emptyBars = Component.text("᠆".repeat(empty), TextColor.color(0x808080)); // Gris

        return Component.text().append(filledBars).append(emptyBars).build();
    }

    private String getRankFromLevel(int level) {
        if (level >= 8) return "S";
        if (level >= 5) return "A";
        if (level == 4) return "B";
        return "C";
    }
}
