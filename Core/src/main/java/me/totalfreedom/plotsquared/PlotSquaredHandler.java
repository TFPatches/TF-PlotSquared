package me.totalfreedom.plotsquared;

import com.github.intellectualsites.plotsquared.commands.CommandCaller;
import com.google.common.base.Function;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import com.github.intellectualsites.plotsquared.plot.object.PlotPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class PlotSquaredHandler
{

    public static final boolean DEBUG = true;
    public static final Logger LOGGER = Bukkit.getPluginManager().getPlugin("PlotSquared").getLogger();
    private static Function<Player, Boolean> adminProvider;

    public boolean isAdmin(PlotPlayer plotPlayer)
    {
        final Player player = getPlayer(plotPlayer);
        if (player == null) {
            return false;
        }
        return isAdmin(player);
    }

    @SuppressWarnings("unchecked")
    public boolean isAdmin(Player player) {
        if (adminProvider == null) {
            final Plugin tfm = getTFM();
            if (tfm == null) {
                return false;
            }

            Object provider = null;
            for (RegisteredServiceProvider<?> serv : Bukkit.getServicesManager().getRegistrations(tfm)) {
                if (Function.class.isAssignableFrom(serv.getService())) {
                    provider = serv.getProvider();
                }
            }

            if (provider == null) {
                warning("Could not obtain admin service provider!");
                return false;
            }

            adminProvider = (Function<Player, Boolean>) provider;
        }

        return adminProvider.apply(player);
    }

    public static Player getPlayer(PlotPlayer plotPlayer) {
        final Player player = Bukkit.getPlayer(plotPlayer.getUUID());

        if (player == null) {
            return null;
        }

        return player;
    }

    public boolean hasTFMPermission(CommandCaller caller, String permission)
    {
        if (caller instanceof PlotPlayer)
        {
            PlotPlayer player = (PlotPlayer)caller;
            return hasTFMPermission(player, permission);
        }
        else
        {
            // Console?
            return true;
        }
    }

    public boolean hasTFMPermission(PlotPlayer player, String permission)
    {
        List<String> adminOnlyPermissions = Arrays.asList(
                "plots.worldedit.bypass", "plots.area", "plots.grant.add", "plots.debugallowunsafe", "plots.debugroadgen", "plots.debugpaste",
                "plots.createroadschematic", "plots.merge", "plots.unlink", "plots.area", "plots.setup", "plots.set.flag.other");
        if (!isAdmin(player))
        {
            if (permission.startsWith("plots.admin") || adminOnlyPermissions.contains(permission))
            {
                return false;
            }
        }
        return true;
    }

    public static Player getPlayer(String match) {
        match = match.toLowerCase();

        Player found = null;
        int delta = Integer.MAX_VALUE;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().toLowerCase().startsWith(match)) {
                int curDelta = player.getName().length() - match.length();
                if (curDelta < delta) {
                    found = player;
                    delta = curDelta;
                }
                if (curDelta == 0) {
                    break;
                }
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().toLowerCase().contains(match)) {
                return player;
            }
        }
        return found;
    }

    public static Plugin getTFM() {
        final Plugin tfm = Bukkit.getPluginManager().getPlugin("TotalFreedomMod");
        if (tfm == null) {
            LOGGER.warning("Could not resolve plugin: TotalFreedomMod");
        }

        return tfm;
    }

    public void debug(String debug) {
        if (DEBUG) {
            info(debug);
        }
    }

    public void warning(String warning) {
        LOGGER.warning(warning);
    }

    public void info(String info) {
        LOGGER.info(info);
    }

}