package com.probablycoding.bukkit.playersimulator;

import net.minecraft.server.v1_7_R4.ControllerJump;
import net.minecraft.server.v1_7_R4.EntitySheep;

public class BotControllerJump
		extends ControllerJump
{
	private EntityBot a;
	private boolean b;

	public BotControllerJump(EntityBot bot)
	{
		super(new EntitySheep(bot.world));
		this.a = bot;
	}

	public void a()
	{
		this.b = true;
	}

	public void b()
	{
		this.a.f(this.b);
		this.b = false;
	}
}