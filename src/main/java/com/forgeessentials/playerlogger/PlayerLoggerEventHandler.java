package com.forgeessentials.playerlogger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WorldArea;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.playerlogger.entity.Action01Block;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.ChatOutputHandler;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PlayerLoggerEventHandler extends ServerEventHandler
{

    public static class LoggerCheckInfo
    {

        public WorldPoint checkPoint;

        public Date checkStartTime;

    }

    public Map<EntityPlayer, LoggerCheckInfo> playerInfo = new WeakHashMap<>();
    public static int pickerRange = 0;
    public static int eventType  = 0b1111;
    public static String searchCriteria = "";

    private WorldArea getPoints(WorldPoint wp)
    {
        return getPoints(wp,pickerRange);
    }
    private WorldArea getPoints(WorldPoint wp, int radius)
    {
        int x1 = wp.getX() - radius, x2 = wp.getX() + radius, y1 = wp.getY() - radius, y2 = wp.getY() + radius, z1 = wp.getZ() - radius, z2 = wp.getZ() + radius, dia = radius*2;
        return new WorldArea(wp.getDimension(),new Point(x1,y1,z1), new Point(x2,y2,z2));
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        ItemStack stack = event.entityPlayer.getCurrentEquippedItem();
        if (stack == null || stack.getItem() != Items.clock)
            return;
        if (event.action == Action.RIGHT_CLICK_AIR)
            return;
        if (!APIRegistry.perms.checkPermission(event.entityPlayer, ModulePlayerLogger.PERM_WAND))
            return;
        event.setCanceled(true);

        LoggerCheckInfo info = playerInfo.get(event.entityPlayer);
        if (info == null)
        {
            info = new LoggerCheckInfo();
            playerInfo.put(event.entityPlayer, info);
        }

        WorldPoint point;
        if (event.action == Action.RIGHT_CLICK_BLOCK)
            point = new WorldPoint(event.entityPlayer.dimension, //
                    event.x + Facing.offsetsXForSide[event.face], //
                    event.y + Facing.offsetsYForSide[event.face], //
                    event.z + Facing.offsetsZForSide[event.face]);
        else
            point = new WorldPoint(event.entityPlayer.dimension, event.x, event.y, event.z);

        boolean newCheck = !point.equals(info.checkPoint);
        if (newCheck)
        {
            info.checkPoint = point;
            info.checkStartTime = new Date();
            if (event.action == Action.RIGHT_CLICK_BLOCK)
                ChatOutputHandler.chatNotification(event.entityPlayer, "Showing recent block changes (clicked side):");
            else
                ChatOutputHandler.chatNotification(event.entityPlayer, "Showing recent block changes (clicked block):");
        }

        if ((0b00100 & eventType) != 0)
        {
            List<Action01Block> changes = ModulePlayerLogger.getLogger().getLoggedBlockChanges(getPoints(point), null, info.checkStartTime, 4);

            if (changes.size() == 0 && !newCheck)
            {
                ChatOutputHandler.chatError(event.entityPlayer, "No more changes");
                return;
            }

            for (Action01Block change : changes)
            {
                info.checkStartTime = change.time;

                String msg = String.format("%1$tm/%1$te %1$tH:%1$tM:%1$tS", change.time);
                if (change.player != null)
                {
                    UserIdent player = UserIdent.get(change.player.uuid);
                    msg += " " + player.getUsernameOrUuid();
                }
                msg += ": ";

                String blockName = change.block != null ? change.block.name : "";
                if (blockName.contains(":"))
                    blockName = blockName.split(":", 2)[1];

                switch (change.type)
                {
                case PLACE:
                    msg += String.format("PLACED %s", blockName);
                    break;
                case BREAK:
                    msg += String.format("BROKE %s", blockName);
                    break;
                case DETONATE:
                    msg += String.format("EXPLODED %s", blockName);
                    break;
                case USE_LEFT:
                    msg += String.format("LEFT CLICK %s", blockName);
                    break;
                case USE_RIGHT:
                    msg += String.format("RIGHT CLICK %s", blockName);
                    break;
                case BURN:
                    msg += String.format("BURN %s", blockName);
                    break;
                default:
                    continue;
                }
                ChatOutputHandler.chatConfirmation(event.entityPlayer, msg);
            }
        }
        //Add other Action events (Command, Player, Explosion, etc)
    }

}
