package net.badlion.cosmetics.tasks;

import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.cosmetics.managers.PetManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PetAITask implements Runnable {

    @Override
    public void run() {
        if (!Cosmetics.getInstance().isPetsEnabled()) {
            return; // Not really needed since pets will be despawned, but save some hertz of powaaaaa
        }

        for (UUID uuid : PetManager.getSpawnedPets().keySet()) {
            LivingEntity entity = PetManager.getSpawnedPets().get(uuid);

            Player player = Cosmetics.getInstance().getServer().getPlayer(uuid);
            CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(uuid);
            if (entity.getWorld().getName().equals(player.getWorld().getName())) {
                // Call the run method for every pet
                cosmeticsSettings.getActivePet().run(player, entity);
            }
        }
    }
}
