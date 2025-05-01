package com.probablycoding.bukkit.playersimulator;

import net.minecraft.server.v1_7_R4.AttributeInstance;
import net.minecraft.server.v1_7_R4.AxisAlignedBB;
import net.minecraft.server.v1_7_R4.Block;
import net.minecraft.server.v1_7_R4.Blocks;
import net.minecraft.server.v1_7_R4.ControllerMove;
import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.EntitySheep;
import net.minecraft.server.v1_7_R4.GenericAttributes;
import net.minecraft.server.v1_7_R4.Material;
import net.minecraft.server.v1_7_R4.MathHelper;
import net.minecraft.server.v1_7_R4.Navigation;
import net.minecraft.server.v1_7_R4.PathEntity;
import net.minecraft.server.v1_7_R4.PathPoint;
import net.minecraft.server.v1_7_R4.Vec3D;
import net.minecraft.server.v1_7_R4.World;

public class BotNavigation
		extends Navigation
{
	private EntityBot a;
	private World b;
	private PathEntity c;
	private double d;
	private AttributeInstance e;
	private boolean f;
	private int g;
	private int h;
	private Vec3D i = Vec3D.a(0.0D, 0.0D, 0.0D);
	private boolean j = true;
	private boolean k;
	private boolean l;
	private boolean m;

	public BotNavigation(EntityBot bot, World world)
	{
		super(new EntitySheep(world), world);
		this.a = bot;
		this.b = world;
		this.e = bot.getAttributeInstance(GenericAttributes.b);
	}

	public PathEntity a(double d0, double d1, double d2)
	{
		return !l() ? null : this.b.a(this.a, MathHelper.floor(d0), (int)d1, MathHelper.floor(d2), d(), this.j, this.k, this.l, this.m);
	}

	public PathEntity a(Entity entity)
	{
		return !l() ? null : this.b.findPath(this.a, entity, d(), this.j, this.k, this.l, this.m);
	}

	public void f()
	{
		this.g += 1;
		if (!g())
		{
			if (l()) {
				i();
			}
			if (!g())
			{
				Vec3D vec3d = this.c.a(this.a);
				if (vec3d != null) {
					this.a.getControllerMove().a(vec3d.a, vec3d.b, vec3d.c, this.d);
				}
			}
		}
	}

	private void i()
	{
		Vec3D vec3d = j();
		int i = this.c.d();
		for (int j = this.c.e(); j < this.c.d(); j++) {
			if (this.c.a(j).b != (int)vec3d.b)
			{
				i = j;
				break;
			}
		}
		float f = this.a.width * this.a.width;
		for (int k = this.c.e(); k < i; k++) {
			if (vec3d.distanceSquared(this.c.a(this.a, k)) < f) {
				this.c.c(k + 1);
			}
		}
		int k = MathHelper.f(this.a.width);
		int l = (int)this.a.length + 1;
		int i1 = k;
		for (int j1 = i - 1; j1 >= this.c.e(); j1--) {
			if (a(vec3d, this.c.a(this.a, j1), k, l, i1))
			{
				this.c.c(j1);
				break;
			}
		}
		if (this.g - this.h > 100)
		{
			if (vec3d.distanceSquared(this.i) < 2.25D) {
				h();
			}
			this.h = this.g;
			this.i.a = vec3d.a;
			this.i.b = vec3d.b;
			this.i.c = vec3d.c;
		}
	}

	private Vec3D j()
	{
		return Vec3D.a(this.a.locX, k(), this.a.locZ);
	}

	private int k()
	{
		if ((this.a.M()) && (this.m))
		{
			int i = (int)this.a.boundingBox.b;
			Block block = this.b.getType(MathHelper.floor(this.a.locX), i, MathHelper.floor(this.a.locZ));
			int j = 0;
			do
			{
				if ((block != Blocks.WATER) && (block != Blocks.STATIONARY_WATER)) {
					return i;
				}
				i++;
				block = this.b.getType(MathHelper.floor(this.a.locX), i, MathHelper.floor(this.a.locZ));
				j++;
			} while (j <= 16);
			return (int)this.a.boundingBox.b;
		}
		return (int)(this.a.boundingBox.b + 0.5D);
	}

	private boolean l()
	{
		return (this.a.onGround) || ((this.m) && (m()));
	}

	private boolean m()
	{
		return (this.a.M()) || (this.a.P());
	}

	private void n()
	{
		if (!this.b.i(MathHelper.floor(this.a.locX), (int)(this.a.boundingBox.b + 0.5D), MathHelper.floor(this.a.locZ))) {
			for (int i = 0; i < this.c.d(); i++)
			{
				PathPoint pathpoint = this.c.a(i);
				if (this.b.i(pathpoint.a, pathpoint.b, pathpoint.c))
				{
					this.c.b(i - 1);
					return;
				}
			}
		}
	}

	private boolean a(Vec3D vec3d, Vec3D vec3d1, int i, int j, int k)
	{
		int l = MathHelper.floor(vec3d.a);
		int i1 = MathHelper.floor(vec3d.c);
		double d0 = vec3d1.a - vec3d.a;
		double d1 = vec3d1.c - vec3d.c;
		double d2 = d0 * d0 + d1 * d1;
		if (d2 < 1.0E-8D) {
			return false;
		}
		double d3 = 1.0D / Math.sqrt(d2);

		d0 *= d3;
		d1 *= d3;
		i += 2;
		k += 2;
		if (!a(l, (int)vec3d.b, i1, i, j, k, vec3d, d0, d1)) {
			return false;
		}
		i -= 2;
		k -= 2;
		double d4 = 1.0D / Math.abs(d0);
		double d5 = 1.0D / Math.abs(d1);
		double d6 = l * 1 - vec3d.a;
		double d7 = i1 * 1 - vec3d.c;
		if (d0 >= 0.0D) {
			d6 += 1.0D;
		}
		if (d1 >= 0.0D) {
			d7 += 1.0D;
		}
		d6 /= d0;
		d7 /= d1;
		int j1 = d0 < 0.0D ? -1 : 1;
		int k1 = d1 < 0.0D ? -1 : 1;
		int l1 = MathHelper.floor(vec3d1.a);
		int i2 = MathHelper.floor(vec3d1.c);
		int j2 = l1 - l;
		int k2 = i2 - i1;
		do
		{
			if ((j2 * j1 <= 0) && (k2 * k1 <= 0)) {
				return true;
			}
			if (d6 < d7)
			{
				d6 += d4;
				l += j1;
				j2 = l1 - l;
			}
			else
			{
				d7 += d5;
				i1 += k1;
				k2 = i2 - i1;
			}
		} while (a(l, (int)vec3d.b, i1, i, j, k, vec3d, d0, d1));
		return false;
	}

	private boolean a(int i, int j, int k, int l, int i1, int j1, Vec3D vec3d, double d0, double d1)
	{
		int k1 = i - l / 2;
		int l1 = k - j1 / 2;
		if (!b(k1, j, l1, l, i1, j1, vec3d, d0, d1)) {
			return false;
		}
		for (int i2 = k1; i2 < k1 + l; i2++) {
			for (int j2 = l1; j2 < l1 + j1; j2++)
			{
				double d2 = i2 + 0.5D - vec3d.a;
				double d3 = j2 + 0.5D - vec3d.c;
				if (d2 * d0 + d3 * d1 >= 0.0D)
				{
					Block block = this.b.getType(i2, j - 1, j2);
					Material material = block.getMaterial();
					if (material == Material.AIR) {
						return false;
					}
					if ((material == Material.WATER) && (!this.a.M())) {
						return false;
					}
					if (material == Material.LAVA) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean b(int i, int j, int k, int l, int i1, int j1, Vec3D vec3d, double d0, double d1)
	{
		for (int k1 = i; k1 < i + l; k1++) {
			for (int l1 = j; l1 < j + i1; l1++) {
				for (int i2 = k; i2 < k + j1; i2++)
				{
					double d2 = k1 + 0.5D - vec3d.a;
					double d3 = i2 + 0.5D - vec3d.c;
					if (d2 * d0 + d3 * d1 >= 0.0D)
					{
						Block block = this.b.getType(k1, l1, i2);
						if (!block.b(this.b, k1, l1, i2)) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
}