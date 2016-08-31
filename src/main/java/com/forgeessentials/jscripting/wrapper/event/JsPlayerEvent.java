package com.forgeessentials.jscripting.wrapper.event;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.event.entity.player.PlayerEvent;

import com.forgeessentials.jscripting.wrapper.entity.JsEntityPlayer;

public abstract class JsPlayerEvent<T extends PlayerEvent> extends JsLivingEvent<T>
{

    // public JsPlayerEvent(ScriptInstance script, Object handler)
    // {
    // super(script, handler);
    // }

    // @Override
    // @SubscribeEvent
    // public void _handle(T event)
    // {
    // _callEvent(event);
    // }

    @Override
    public JsEntityPlayer getPlayer()
    {
        return new JsEntityPlayer(_event.getEntityPlayer());
    }

    @Override
    public ICommandSender _getSender()
    {
        return _event.getEntityPlayer();
    }

}
