// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.util.dummy;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.WorldServer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityPlayer;

public class DummyPlayer extends EntityPlayer
{
    public DummyPlayer(final Entity entity, final String name) {
        super(MinecraftServer.getServer(), (WorldServer)entity.getWorld(), new GameProfile(UUID.randomUUID(), name), (PlayerInteractManager)new DummyPlayerInteractManager(entity.getWorld()));
    }
}
