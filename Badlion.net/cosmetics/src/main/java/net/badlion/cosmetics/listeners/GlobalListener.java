package net.badlion.cosmetics.listeners;

import net.badlion.cosmetics.arrowtrails.ArrowTrail;
import net.badlion.cosmetics.gadgets.Gadget;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.cosmetics.morphs.Morph;
import net.badlion.cosmetics.particles.Particle;
import net.badlion.cosmetics.rodtrails.RodTrail;
import net.badlion.smellycases.events.RequestPlayerOwnedCases;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GlobalListener implements Listener {

    @EventHandler
    public void onRequestPlayerData(RequestPlayerOwnedCases event) {
        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(event.getPlayer().getUniqueId());

        for (Gadget gadget : cosmeticsSettings.getOwnedGadgets()) {
            event.addCase(gadget.getName());
        }

        for (Morph morph : cosmeticsSettings.getOwnedMorphs()) {
            event.addCase(morph.getName());
        }

        for (Particle particle : cosmeticsSettings.getOwnedParticles()) {
            event.addCase(particle.getName());
        }

        for (ArrowTrail arrowTrail : cosmeticsSettings.getOwnedArrowTrails()) {
            event.addCase(arrowTrail.getName());
        }

        for (String pet : cosmeticsSettings.getOwnedPets().keySet()) {
            event.addCase(pet);
        }

        for (RodTrail rodTrail : cosmeticsSettings.getOwnedRodTrails()) {
            event.addCase(rodTrail.getName());
        }
    }

}
