package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutEntityTeleport implements Packet<PacketListenerPlayOut> {

    private int a;
    private int b;
    private int c;
    private int d;
    private byte e;
    private byte f;
    private boolean g;

    public PacketPlayOutEntityTeleport() {}

    public PacketPlayOutEntityTeleport(Entity entity) {
        this.a = entity.getId();
        this.b = MathHelper.floor(entity.locX * 32.0D);
        this.c = MathHelper.floor(entity.locY * 32.0D);
        this.d = MathHelper.floor(entity.locZ * 32.0D);
        this.e = (byte) ((int) (entity.yaw * 256.0F / 360.0F));
        this.f = (byte) ((int) (entity.pitch * 256.0F / 360.0F));
        this.g = entity.onGround;
    }

    public PacketPlayOutEntityTeleport(int i, int j, int k, int l, byte b0, byte b1, boolean flag) {
        this.a = i;
        this.b = j;
        this.c = k;
        this.d = l;
        this.e = b0;
        this.f = b1;
        this.g = flag;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e();
        this.b = packetdataserializer.readInt();
        this.c = packetdataserializer.readInt();
        this.d = packetdataserializer.readInt();
        this.e = packetdataserializer.readByte();
        this.f = packetdataserializer.readByte();
        this.g = packetdataserializer.readBoolean();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.b(this.a);
        packetdataserializer.writeInt(this.b);
        packetdataserializer.writeInt(this.c);
        packetdataserializer.writeInt(this.d);
        packetdataserializer.writeByte(this.e);
        packetdataserializer.writeByte(this.f);
        packetdataserializer.writeBoolean(this.g);
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
    
    public int getB() {
		return b;
	}
    
    public void setB(int b) {
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
    
    public byte getE() {
		return e;
	}
    
    public void setE(byte e) {
		this.e = e;
	}
    
    public byte getF() {
		return f;
	}
    
    public void setF(byte f) {
		this.f = f;
	}
    
    public boolean isG() {
		return g;
	}
    
    public void setG(boolean g) {
		this.g = g;
	}
}