package com.probablycoding.bukkit.playersimulator;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.EntitySenses;
import net.minecraft.server.v1_7_R4.EntitySheep;
import net.minecraft.server.v1_7_R4.MethodProfiler;
import net.minecraft.server.v1_7_R4.World;

public class BotSenses
		extends EntitySenses
{
	EntityBot entity;
	List seenEntities = new ArrayList();
	List unseenEntities = new ArrayList();

	public BotSenses(EntityBot bot)
	{
		super(new EntitySheep(bot.world));
		this.entity = bot;
	}

	public void a()
	{
		this.seenEntities.clear();
		this.unseenEntities.clear();
	}

	public boolean canSee(Entity entity)
	{
		if (this.seenEntities.contains(entity)) {
			return true;
		}
		if (this.unseenEntities.contains(entity)) {
			return false;
		}
		this.entity.world.methodProfiler.a("canSee");
		boolean flag = this.entity.hasLineOfSight(entity);

		this.entity.world.methodProfiler.b();
		if (flag) {
			this.seenEntities.add(entity);
		} else {
			this.unseenEntities.add(entity);
		}
		return flag;
	}
}