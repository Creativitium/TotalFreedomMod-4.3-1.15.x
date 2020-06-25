package me.StevenLawson.TotalFreedomMod;

import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static me.StevenLawson.TotalFreedomMod.TFM_Jumppads.JumpPadMode.MADGEEK;
import org.bukkit.Material;
import static org.bukkit.Material.BLACK_WOOL;
import static org.bukkit.Material.BLUE_WOOL;
import static org.bukkit.Material.BROWN_WOOL;
import static org.bukkit.Material.CYAN_WOOL;
import static org.bukkit.Material.GRAY_WOOL;
import static org.bukkit.Material.GREEN_WOOL;
import static org.bukkit.Material.LIGHT_BLUE_WOOL;
import static org.bukkit.Material.LIGHT_GRAY_WOOL;
import static org.bukkit.Material.LIME_WOOL;
import static org.bukkit.Material.MAGENTA_WOOL;
import static org.bukkit.Material.ORANGE_WOOL;
import static org.bukkit.Material.PINK_WOOL;
import static org.bukkit.Material.PURPLE_WOOL;
import static org.bukkit.Material.RED_WOOL;
import static org.bukkit.Material.WHITE_WOOL;
import static org.bukkit.Material.YELLOW_WOOL;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class TFM_Jumppads {

    public static final double DAMPING_COEFFICIENT;
    public static final Map<Player, Boolean> PUSH_MAP;
    private static JumpPadMode mode;
    private static double strength;
    public static final List<Material> WOOL_COLOURS = asList(WHITE_WOOL, RED_WOOL, ORANGE_WOOL, YELLOW_WOOL, GREEN_WOOL, LIME_WOOL, LIGHT_BLUE_WOOL, CYAN_WOOL, BLUE_WOOL, PURPLE_WOOL, MAGENTA_WOOL, PINK_WOOL, BROWN_WOOL, GRAY_WOOL, LIGHT_GRAY_WOOL, BLACK_WOOL);

    static {
        DAMPING_COEFFICIENT = 0.8;
        PUSH_MAP = new HashMap<Player, Boolean>();
        mode = MADGEEK;
        strength = 0.4;
    }

    public static void PlayerMoveEvent(PlayerMoveEvent event) {
        if (mode == JumpPadMode.OFF) {
            return;
        }

        final Player player = event.getPlayer();
        final Block block = event.getTo().getBlock();
        final Vector velocity = player.getVelocity().clone();

        if (mode == JumpPadMode.MADGEEK) {
            Boolean canPush = PUSH_MAP.get(player);
            if (canPush == null) {
                canPush = true;
            }
            if (WOOL_COLOURS.contains(block.getRelative(0, -1, 0).getType())) {
                if (canPush) {
                    velocity.multiply(strength + 0.85).multiply(-1.0);
                }
                canPush = false;
            } else {
                canPush = true;
            }
            PUSH_MAP.put(player, canPush);
        } else {
            if (WOOL_COLOURS.contains(block.getRelative(0, -1, 0).getType())) {
                velocity.add(new Vector(0.0, strength, 0.0));
            }

            if (mode == JumpPadMode.NORMAL_AND_SIDEWAYS) {
                if (WOOL_COLOURS.contains(block.getRelative(1, 0, 0).getType())) {
                    velocity.add(new Vector(-DAMPING_COEFFICIENT * strength, 0.0, 0.0));
                }

                if (WOOL_COLOURS.contains(block.getRelative(-1, 0, 0).getType())) {
                    velocity.add(new Vector(DAMPING_COEFFICIENT * strength, 0.0, 0.0));
                }

                if (WOOL_COLOURS.contains(block.getRelative(0, 0, 1).getType())) {
                    velocity.add(new Vector(0.0, 0.0, -DAMPING_COEFFICIENT * strength));
                }

                if (WOOL_COLOURS.contains(block.getRelative(0, 0, -1).getType())) {
                    velocity.add(new Vector(0.0, 0.0, DAMPING_COEFFICIENT * strength));
                }
            }
        }

        if (!player.getVelocity().equals(velocity)) {
            player.setFallDistance(0.0f);
            player.setVelocity(velocity);
        }
    }

    public static JumpPadMode getMode() {
        return mode;
    }

    public static void setMode(JumpPadMode mode) {
        TFM_Jumppads.mode = mode;
    }

    public static double getStrength() {
        return strength;
    }

    public static void setStrength(double strength) {
        TFM_Jumppads.strength = strength;
    }

    public static enum JumpPadMode {
        OFF(false), NORMAL(true), NORMAL_AND_SIDEWAYS(true), MADGEEK(true);
        private boolean on;

        private JumpPadMode(boolean on) {
            this.on = on;
        }

        public boolean isOn() {
            return on;
        }
    }
}
