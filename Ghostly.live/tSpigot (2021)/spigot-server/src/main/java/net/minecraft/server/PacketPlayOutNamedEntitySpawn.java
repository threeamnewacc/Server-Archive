package net.minecraft.server;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class PacketPlayOutNamedEntitySpawn implements Packet<PacketListenerPlayOut> {

    private int a;
    private UUID b;
    private int c;
    private int d;
    private int e;
    private byte f;
    private byte g;
    private int h;
    private DataWatcher i;
    private List<DataWatcher.WatchableObject> j;

    public PacketPlayOutNamedEntitySpawn() {}

    public PacketPlayOutNamedEntitySpawn(EntityHuman entityhuman) {
        this.a = entityhuman.getId();
        this.b = entityhuman.getProfile().getId();
        this.c = MathHelper.floor(entityhuman.locX * 32.0D);
        this.d = MathHelper.floor(entityhuman.locY * 32.0D);
        this.e = MathHelper.floor(entityhuman.locZ * 32.0D);
        this.f = (byte) ((int) (entityhuman.yaw * 256.0F / 360.0F));
        this.g = (byte) ((int) (entityhuman.pitch * 256.0F / 360.0F));
        ItemStack itemstack = entityhuman.inventory.getItemInHand();

        this.h = itemstack == null ? 0 : Item.getId(itemstack.getItem());
        this.i = entityhuman.getDataWatcher();
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e();
        this.b = packetdataserializer.g();
        this.c = packetdataserializer.readInt();
        this.d = packetdataserializer.readInt();
        this.e = packetdataserializer.readInt();
        this.f = packetdataserializer.readByte();
        this.g = packetdataserializer.readByte();
        this.h = packetdataserializer.readShort();
        this.j = DataWatcher.b(packetdataserializer);
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.b(this.a);
        packetdataserializer.a(this.b);
        packetdataserializer.writeInt(this.c);
        packetdataserializer.writeInt(this.d);
        packetdataserializer.writeInt(this.e);
        packetdataserializer.writeByte(this.f);
        packetdataserializer.writeByte(this.g);
        packetdataserializer.writeShort(this.h);
        this.i.a(packetdataserializer);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int getA() {
		return a;
	}
    
    public void setA(int a) {
		this.a = a;
	}
    
    public UUID getB() {
		return b;
	}
    
    public void setB(UUID b) {
		this.b = b;
	}
    
    public int getC() {
		return c;
	}
    
    public void setC(int c) {
		this.c = c;
	}
    
    public int getD() {
		return d;
	}
    
    public void setD(int d) {
		this.d = d;
	}
    
    public int getE() {
		return e;
	}
    
    public void setE(int e) {
		this.e = e;
	}
    
    public byte getF() {
		return f;
	}
    
    public void setF(byte f) {
		this.f = f;
	}
    
    public byte getG() {
		return g;
	}
    
    public void setG(byte g) {
		this.g = g;
	}
    
    public int getH() {
		return h;
	}
    
    public void setH(int h) {
		this.h = h;
	}
    
    public DataWatcher getI() {
		return i;
	}
    
    public void setI(DataWatcher i) {
		this.i = i;
	}
    
    public List<DataWatcher.WatchableObject> getJ() {
		return j;
	}
}