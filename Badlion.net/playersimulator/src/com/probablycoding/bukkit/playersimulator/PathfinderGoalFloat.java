package com.probablycoding.bukkit.playersimulator;

import java.util.Random;
import net.minecraft.server.v1_7_R4.ControllerJump;
import net.minecraft.server.v1_7_R4.Navigation;
import net.minecraft.server.v1_7_R4.PathfinderGoal;

public class PathfinderGoalFloat
		extends PathfinderGoal
{
	private EntityBot a;

	public PathfinderGoalFloat(EntityBot bot)
	{
		this.a = bot;
		a(4);
		bot.getNavigation().e(true);
	}

	public boolean a()
	{
		return (this.a.M()) || (this.a.P());
	}

	public void e()
	{
		if (this.a.aI().nextFloat() < 0.8F) {
			this.a.getControllerJump().a();
		}
	}
}