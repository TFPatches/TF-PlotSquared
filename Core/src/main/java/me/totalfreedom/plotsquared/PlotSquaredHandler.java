package me.totalfreedom.plotsquared;

import com.google.common.base.Function;
import com.plotsquared.core.command.CommandCaller;
import com.plotsquared.core.player.PlotPlayer;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlotSquaredHandler
{
    public static final boolean DEBUG = true;
    public static final Logger LOGGER = Bukkit.getPluginManager().getPlugin("PlotSquared").getLogger();
    private static Function<Player, Boolean> adminProvider;

    public boolean isStaff(CommandCaller commandCaller)
    {
        final Player player = getPlayer(commandCaller.toString());
        if (player == null)
        {
            return false;
        }
        return isStaff(player);
    }

    @SuppressWarnings("unchecked")
    public boolean isStaff(Player player)
    {
        TotalFreedomMod tfm = getTFM();
        return tfm.sl.isStaff(player);
    }

    public static Player getPlayer(CommandCaller commandCaller)
    {
        return Bukkit.getPlayer(commandCaller.toString());
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
                "plots.createroadschematic", "plots.merge", "plots.unlink", "plots.area", "plots.setup", "plots.set.flag.other", "plots.reload",
                "plots.backup", "plots.debug");
        if (!isStaff(player))
        {
            return !permission.startsWith("plots.admin") && !adminOnlyPermissions.contains(permission);
        }
        return true;
    }

    public static Player getPlayer(String match)
    {
        match = match.toLowerCase();

        Player found = null;
        int delta = Integer.MAX_VALUE;
        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (player.getName().toLowerCase().startsWith(match))
            {
                int curDelta = player.getName().length() - match.length();
                if (curDelta < delta)
                {
                    found = player;
                    delta = curDelta;
                }
                if (curDelta == 0)
                {
                    break;
                }
            }
        }

        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (player.getName().toLowerCase().contains(match))
            {
                return player;
            }
        }
        return found;
    }

    public static TotalFreedomMod getTFM()
    {
        final Plugin tfm = Bukkit.getPluginManager().getPlugin("TotalFreedomMod");
        if (tfm == null)
        {
            LOGGER.warning("Could not resolve plugin: TotalFreedomMod");
        }

        return (TotalFreedomMod)tfm;
    }

    public void debug(String debug)
    {
        if (DEBUG)
        {
            info(debug);
        }
    }

    public void warning(String warning)
    {
        LOGGER.warning(warning);
    }

    public void info(String info)
    {
        LOGGER.info(info);
    }
}