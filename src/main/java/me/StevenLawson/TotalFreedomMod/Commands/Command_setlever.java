package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.Lever;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
@CommandParameters(description = "Set the on/off state of the lever at position x, y, z in world 'worldname'.", usage = "/<command> <x> <y> <z> <worldname> <on|off>")
public class Command_setlever extends TFM_Command {

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length != 5) {
            return false;
        }

        double x, y, z;
        try {
            x = Double.parseDouble(args[0]);
            y = Double.parseDouble(args[1]);
            z = Double.parseDouble(args[2]);
        } catch (NumberFormatException ex) {
            playerMsg("§cERROR: §7Invalid coordinates.");
            return true;
        }

        World world = null;
        final String needleWorldName = args[3].trim();
        final List<World> worlds = server.getWorlds();
        for (final World testWorld : worlds) {
            if (testWorld.getName().trim().equalsIgnoreCase(needleWorldName)) {
                world = testWorld;
                break;
            }
        }

        if (world == null) {
            playerMsg("§cERROR: §7Invalid world name.");
            return true;
        }

        final Location leverLocation = new Location(world, x, y, z);

        final boolean leverOn = (args[4].trim().equalsIgnoreCase("on") || args[4].trim().equalsIgnoreCase("1"));

        final Block targetBlock = leverLocation.getBlock();

        if (targetBlock.getType() == Material.LEVER) {
            BlockState state = targetBlock.getState();
            Lever lever = (Lever) state.getData();
            lever.setPowered(leverOn);
            state.setData(lever);
            state.update();
        } else {
            playerMsg("§cERROR: §7Target block §e" + targetBlock + "  §7is not a lever.");
            return true;
        }

        return true;
    }
}
