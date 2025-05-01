package net.badlion.uhc.tasks;

import net.badlion.gberry.Gberry;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.events.BorderShrinkEvent;
import net.badlion.uhc.events.PermaDayEvent;
import net.badlion.uhc.listeners.WorldGenerationListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class BorderShrinkTask extends BukkitRunnable {

    public int startTime;
    public int shrinkInterval;
    public int shrinkAmount;
    public int minimumRadius;
    public int currentRadius;
    public int previousRadius;
    public boolean minRadiusHit = false;
    public int extraShrinkCounter = 0;
    public int extraShrinkTime;
    public int extraShrinkTime2;

    private int counter = 0;

    private boolean ranBefore = false;

    public static boolean useBedRockBorder = true;
    public static int EXTRA_BEDROCK_BORDER_HEIGHT = 5;

    // TODO: Maybe use the extra params if I feel like cleaning this fucking area up later
    public BorderShrinkTask(int startTime, int shrinkInterval, int shrinkAmount, int minimumRadius, int extraShrinkTime, int extraShrinkTime2) {
        this.startTime = startTime;
        this.shrinkInterval = shrinkInterval;
        this.shrinkAmount = shrinkAmount;
        this.minimumRadius = minimumRadius;

        this.currentRadius = (int) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.RADIUS.name()).getValue();
        this.previousRadius = this.currentRadius;
    }

    @Override
    public void run() {
        // First time stuff
        if (!this.ranBefore) {
            BorderShrinkTask ezTask = BadlionUHC.getInstance().getBorderShrinkTask();
            Gberry.broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "[Border] The world border will start shrinking in " + this.shrinkInterval + " minutes by "
                                            + this.shrinkAmount + " blocks every " + ezTask.shrinkInterval + " minutes until the border size becomes "
                                            + ezTask.minimumRadius + "x" + ezTask.minimumRadius);

            PermaDayEvent event = new PermaDayEvent();
            BadlionUHC.getInstance().getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                Gberry.broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "[Border] Permanent day has been enabled!");

                PermanentDayTask permanentDayTask = new PermanentDayTask();
                BadlionUHC.getInstance().setPermanentDayTask(permanentDayTask);
                permanentDayTask.runTaskTimer(BadlionUHC.getInstance(), 0L, 20L); // run every second, looks nicer
            }

            this.ranBefore = true;
        }

        this.counter++;

        // counter = # of ticks since we had last shrinkage
        // shrinkinterval is in minutes
        if (counter >= shrinkInterval * 60) {
            counter = 0;
            this.shrinkWorld();
        } else if ((!this.minRadiusHit && counter >= (shrinkInterval * 60 - 10)) || (this.minRadiusHit && this.extraShrinkCounter >= 1 && counter >= (shrinkInterval * 60 - 10))) {
            // Don't delay this, use Bukkit.broadcastMessage() (want to give them time to run)
            Bukkit.broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "[Border] The world border is going to shrink in " +
                                            (shrinkInterval * 60 - counter) + " second(s)");

            // At 10 seconds start adding the initial bedrock border
            if (shrinkInterval * 60 - counter == 10) {
                // Use min radius if final shrink to fix extra 0,0 bedrock 4 block bug
                if (BorderShrinkTask.useBedRockBorder) {
                    WorldGenerationListener.addBedrockBorder(this.currentRadius - this.shrinkAmount > this.minimumRadius ? this.currentRadius - this.shrinkAmount : this.minimumRadius);
                }
            }
        } else if (counter == ((shrinkInterval - 3) * 60)) {
            if (!this.minRadiusHit) {
                Gberry.broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "[Border] The world border will shrink by "
                                                + this.shrinkAmount + " blocks in 3 minutes!");
            }
        }

    }

    public void shrinkWorld() {
        this.previousRadius = this.currentRadius;
        this.currentRadius = this.currentRadius - this.shrinkAmount;

        if (!this.minRadiusHit && this.currentRadius <= this.minimumRadius) {
            // Fire Event
            this.setCurrentRadius(this.minimumRadius);

            this.minRadiusHit = true;
            this.currentRadius = this.minimumRadius;
        } else if (this.minRadiusHit && ++this.extraShrinkCounter == 2) {
            this.setCurrentRadius(this.minimumRadius / 2);
        } else if (this.minRadiusHit && this.extraShrinkCounter == 3) {
            this.setCurrentRadius(this.minimumRadius / 4);
	        this.cancel();
        //} else if (this.minRadiusHit && this.extraShrinkCounter == 4) {
           // this.setCurrentRadius(this.minimumRadius / 10); Remove 10x10 border coz it's aids

            //this.cancel();
        } else if (this.minRadiusHit && this.extraShrinkCounter < 2) {
            // pass do nothing
        } else {
            this.setCurrentRadius(this.currentRadius);
        }
    }

    public void setCurrentRadius(int radius) {
        BadlionUHC.getInstance().getServer().getPluginManager().callEvent(new BorderShrinkEvent(this.previousRadius, radius));

        BadlionUHC.getInstance().getServer().dispatchCommand(BadlionUHC.getInstance().getServer().getConsoleSender(), "wb uhcworld setcorners -"
                                                                                                                              + radius + " -" + radius + " " + radius + " " + radius);

        Gberry.broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "[Border] The world border has shrunk to " + radius + "x" + radius);
        WorldGenerationListener.addBedrockBorder(radius, BorderShrinkTask.EXTRA_BEDROCK_BORDER_HEIGHT);
    }

}
