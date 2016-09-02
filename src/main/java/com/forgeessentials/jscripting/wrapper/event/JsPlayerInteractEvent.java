package com.forgeessentials.jscripting.wrapper.event;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class JsPlayerInteractEvent<T extends PlayerInteractEvent> extends JsPlayerEvent<T>
{

    @SubscribeEvent
    public void _handle(T event)
    {
        _callEvent(event);
    }

}
