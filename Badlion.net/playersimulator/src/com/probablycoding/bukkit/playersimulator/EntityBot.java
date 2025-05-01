package com.probablycoding.bukkit.playersimulator;

import net.minecraft.server.v1_7_R4.ControllerJump;
import net.minecraft.server.v1_7_R4.ControllerLook;
import net.minecraft.server.v1_7_R4.ControllerMove;
import net.minecraft.server.v1_7_R4.DamageSource;
import net.minecraft.server.v1_7_R4.EntityAIBodyControl;
import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.EntitySenses;
import net.minecraft.server.v1_7_R4.MethodProfiler;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.Navigation;
import net.minecraft.server.v1_7_R4.PathfinderGoalSelector;
import net.minecraft.server.v1_7_R4.PlayerInteractManager;
import net.minecraft.server.v1_7_R4.World;
import net.minecraft.server.v1_7_R4.WorldServer;
import net.minecraft.util.com.mojang.authlib.GameProfile;

public class EntityBot
		extends EntityPlayer
{
	private ControllerLook h;
	private ControllerMove moveController;
	private ControllerJump lookController;
	private EntityAIBodyControl bn;
	private Navigation navigation;
	protected final PathfinderGoalSelector goalSelector;
	protected final PathfinderGoalSelector targetSelector;
	private EntityLiving goalTarget;
	private EntitySenses bq;

	public static String BLANK_TEXTURE_VALUE = "eyJ0aW1lc3RhbXAiOjE0MTEyNjg3OTI3NjUsInByb2ZpbGVJZCI6IjNmYmVjN2RkMGE1ZjQwYmY5ZDExODg1YTU0NTA3MTEyIiwicHJvZmlsZU5hbWUiOiJsYXN0X3VzZXJuYW1lIiwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg0N2I1Mjc5OTg0NjUxNTRhZDZjMjM4YTFlM2MyZGQzZTMyOTY1MzUyZTNhNjRmMzZlMTZhOTQwNWFiOCJ9fX0=";
	public static String BLANK_TEXTURE_SIG = "u8sG8tlbmiekrfAdQjy4nXIcCfNdnUZzXSx9BE1X5K27NiUvE1dDNIeBBSPdZzQG1kHGijuokuHPdNi/KXHZkQM7OJ4aCu5JiUoOY28uz3wZhW4D+KG3dH4ei5ww2KwvjcqVL7LFKfr/ONU5Hvi7MIIty1eKpoGDYpWj3WjnbN4ye5Zo88I2ZEkP1wBw2eDDN4P3YEDYTumQndcbXFPuRRTntoGdZq3N5EBKfDZxlw4L3pgkcSLU5rWkd5UH4ZUOHAP/VaJ04mpFLsFXzzdU4xNZ5fthCwxwVBNLtHRWO26k/qcVBzvEXtKGFJmxfLGCzXScET/OjUBak/JEkkRG2m+kpmBMgFRNtjyZgQ1w08U6HHnLTiAiio3JswPlW5v56pGWRHQT5XWSkfnrXDalxtSmPnB5LmacpIImKgL8V9wLnWvBzI7SHjlyQbbgd+kUOkLlu7+717ySDEJwsFJekfuR6N/rpcYgNZYrxDwe4w57uDPlwNL6cJPfNUHV7WEbIU1pMgxsxaXe8WSvV87qLsR7H06xocl2C0JFfe2jZR4Zh3k9xzEnfCeFKBgGb4lrOWBu1eDWYgtKV67M2Y+B3W5pjuAjwAxn0waODtEn/3jKPbc/sxbPvljUCw65X+ok0UUN1eOwXV5l2EGzn05t3Yhwq19/GxARg63ISGE8CKw=";

	public EntityBot(MinecraftServer minecraftserver, WorldServer worldserver, GameProfile gameprofile, PlayerInteractManager playerinteractmanager)
	{
		super(minecraftserver, worldserver, gameprofile, playerinteractmanager);

		this.goalSelector = new PathfinderGoalSelector((this.world != null) && (this.world.methodProfiler != null) ? this.world.methodProfiler : null);
		this.targetSelector = new PathfinderGoalSelector((this.world != null) && (this.world.methodProfiler != null) ? this.world.methodProfiler : null);
		this.h = new BotControllerLook(this);
		this.moveController = new BotControllerMove(this);
		this.lookController = new BotControllerJump(this);
		this.bn = new EntityAIBodyControl(this);
		this.navigation = new BotNavigation(this, this.world);
		this.bq = new BotSenses(this);

		getNavigation().b(true);
		getNavigation().a(true);
		this.goalSelector.a(0, new PathfinderGoalFloat(this));
	}

	public ControllerLook getControllerLook()
	{
		return this.h;
	}

	public ControllerMove getControllerMove()
	{
		return this.moveController;
	}

	public ControllerJump getControllerJump()
	{
		return this.lookController;
	}

	public Navigation getNavigation()
	{
		return this.navigation;
	}

	protected void a(double d0, boolean flag)
	{
		b(d0, flag);
	}

	public boolean damageEntity(DamageSource damagesource, float f)
	{
		return true;
	}

	public void h()
	{
		this.invulnerableTicks -= 1;
		if (this.noDamageTicks > 0) {
			this.noDamageTicks -= 1;
		}
		super.i();
	}

	public boolean br()
	{
		return true;
	}

	protected void bn()
	{
		++this.aU;
		this.world.methodProfiler.a("checkDespawn");

		this.world.methodProfiler.b();
		this.world.methodProfiler.a("sensing");
		this.bq.a();
		this.world.methodProfiler.b();
		this.world.methodProfiler.a("targetSelector");
		this.targetSelector.a();
		this.world.methodProfiler.b();
		this.world.methodProfiler.a("goalSelector");
		this.goalSelector.a();
		this.world.methodProfiler.b();
		this.world.methodProfiler.a("navigation");
		this.navigation.f();
		this.world.methodProfiler.b();
		this.world.methodProfiler.a("mob tick");
		this.bp();
		this.world.methodProfiler.b();
		this.world.methodProfiler.a("controls");
		this.world.methodProfiler.a("move");
		this.moveController.c();
		this.world.methodProfiler.c("look");
		this.lookController.a();
		this.world.methodProfiler.c("jump");
		this.lookController.b();
		this.world.methodProfiler.b();
		this.world.methodProfiler.b();
	}

	public void n(float f)
	{
		this.bf = f;
	}
}