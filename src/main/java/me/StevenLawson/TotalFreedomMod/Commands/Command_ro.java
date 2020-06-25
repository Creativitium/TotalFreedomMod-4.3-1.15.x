package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.World.TFM_AdminWorld;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH, blockHostConsole = false)
@CommandParameters(description = "Remove all blocks of a certain type in the radius of certain players.", usage = "/<command> <block> [radius (default=50)] [player]")
public class Command_ro extends TFM_Command {

    public static final List<Material> SHULKER_BOXES = Arrays.asList(
            Material.SHULKER_BOX,
            Material.WHITE_SHULKER_BOX,
            Material.RED_SHULKER_BOX,
            Material.ORANGE_SHULKER_BOX,
            Material.YELLOW_SHULKER_BOX,
            Material.GREEN_SHULKER_BOX,
            Material.LIME_SHULKER_BOX,
            Material.LIGHT_BLUE_SHULKER_BOX,
            Material.CYAN_SHULKER_BOX,
            Material.BLUE_SHULKER_BOX,
            Material.PURPLE_SHULKER_BOX,
            Material.MAGENTA_SHULKER_BOX,
            Material.PINK_SHULKER_BOX,
            Material.BROWN_SHULKER_BOX,
            Material.GRAY_SHULKER_BOX,
            Material.LIGHT_GRAY_SHULKER_BOX,
            Material.BLACK_SHULKER_BOX);

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length < 1 || args.length
                > 3) {
            return false;
        }
        final List<Material> materials = new ArrayList<Material>();
        String names = null;
        if (args[0].equalsIgnoreCase(
                "shulker_boxes") || args[0].equalsIgnoreCase("shulkers")) {
            materials.addAll(SHULKER_BOXES);
            names = "shulker boxes";
        } else {
            for (String materialName : StringUtils.split(args[0], ",")) {
                Material fromMaterial = Material.matchMaterial(materialName);

                if (fromMaterial == null || fromMaterial == Material.AIR || !fromMaterial.isBlock()) {
                    playerMsg("Invalid material: " + materialName, ChatColor.RED);
                    return true;
                }

                materials.add(fromMaterial);
            }
        }

        int radius = 20;
        if (args.length
                >= 2) {
            try {
                radius = Math.max(1, Math.min(50, Integer.parseInt(args[1])));
            } catch (NumberFormatException ex) {
                playerMsg("Invalid radius: " + args[1], ChatColor.RED);
                return true;
            }
        }

        final Player targetPlayer;
        if (args.length
                == 3) {
            targetPlayer = getPlayer(args[2]);
            if (targetPlayer == null) {
                playerMsg(TFM_Command.PLAYER_NOT_FOUND);
                return true;
            }
        } else {
            targetPlayer = null;
        }

        if (names
                == null) {
            names = StringUtils.join(materials, ", ");
        }

        World adminWorld = null;

        try {
            adminWorld = TFM_AdminWorld.getInstance().getWorld();
        } catch (Exception ex) {
        }

        int affected = 0;
        if (targetPlayer
                == null) {
            TFM_Util.adminAction(sender.getName(), "Removing all " + names + " within " + radius + " blocks of all players... Brace for lag!", false);

            for (final Player player : server.getOnlinePlayers()) {
                if (player.getWorld() == adminWorld) {
                    continue;
                }

                for (final Material material : materials) {
                    affected += replaceBlocks(player.getLocation(), material, Material.AIR, radius);
                }
            }
        } else {
            if (targetPlayer.getWorld() != adminWorld) {
                for (Material material : materials) {
                    TFM_Util.adminAction(sender.getName(), "Removing all " + names + " within " + radius + " blocks of " + targetPlayer.getName(), false);
                    affected += replaceBlocks(targetPlayer.getLocation(), material, Material.AIR, radius);
                }
            }
        }

        TFM_Util.adminAction(sender.getName(), "Remove complete! " + affected + " blocks removed.", false);

        return true;
    }

    public static int replaceBlocks(Location center, Material fromMaterial, Material toMaterial, int radius) {
        int affected = 0;

        Block centerBlock = center.getBlock();
        for (int xOffset = -radius; xOffset <= radius; xOffset++) {
            for (int yOffset = -radius; yOffset <= radius; yOffset++) {
                for (int zOffset = -radius; zOffset <= radius; zOffset++) {
                    Block block = centerBlock.getRelative(xOffset, yOffset, zOffset);

                    if (block.getType().equals(fromMaterial)) {
                        if (block.getLocation().distanceSquared(center) < (radius * radius)) {
                            block.setType(toMaterial);
                            affected++;
                        }
                    }
                }
            }
        }

        return affected;
    }
}
