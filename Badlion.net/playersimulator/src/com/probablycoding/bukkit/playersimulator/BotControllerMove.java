package com.probablycoding.bukkit.playersimulator;

import net.minecraft.server.v1_7_R4.AttributeInstance;
import net.minecraft.server.v1_7_R4.AxisAlignedBB;
import net.minecraft.server.v1_7_R4.ControllerJump;
import net.minecraft.server.v1_7_R4.ControllerMove;
import net.minecraft.server.v1_7_R4.EntitySheep;
import net.minecraft.server.v1_7_R4.GenericAttributes;
import net.minecraft.server.v1_7_R4.MathHelper;
import org.bukkit.craftbukkit.v1_7_R4.TrigMath;

public class BotControllerMove
		extends ControllerMove
{
	private EntityBot a;
	private double b;
	private double c;
	private double d;
	private double e;
	private boolean f;

	public BotControllerMove(EntityBot bot)
	{
		super(new EntitySheep(bot.world));
		this.a = bot;
		this.b = bot.locX;
		this.c = bot.locY;
		this.d = bot.locZ;
	}

	public boolean a()
	{
		return this.f;
	}

	public double b()
	{
		return this.e;
	}

	public void a(double d0, double d1, double d2, double d3)
	{
		this.b = d0;
		this.c = d1;
		this.d = d2;
		this.e = d3;
		this.f = true;
	}

	public void c()
	{
		this.a.n(0.0F);
		if (this.f)
		{
			this.f = false;
			int i = MathHelper.floor(this.a.boundingBox.b + 0.5D);
			double d0 = this.b - this.a.locX;
			double d1 = this.d - this.a.locZ;
			double d2 = this.c - i;
			double d3 = d0 * d0 + d2 * d2 + d1 * d1;
			if (d3 >= 2.500000277905201E-7D)
			{
				float f = (float)(TrigMath.atan2(d1, d0) * 180.0D / 3.1415927410125732D) - 90.0F;

				this.a.yaw = a(this.a.yaw, f, 30.0F);
				this.a.i((float)(this.e * this.a.getAttributeInstance(GenericAttributes.d).getValue()));
				if ((d2 > 0.0D) && (d0 * d0 + d1 * d1 < 1.0D)) {
					this.a.getControllerJump().a();
				}
			}
		}
	}

	private float a(float f, float f1, float f2)
	{
		float f3 = MathHelper.g(f1 - f);
		if (f3 > f2) {
			f3 = f2;
		}
		if (f3 < -f2) {
			f3 = -f2;
		}
		return f + f3;
	}
}