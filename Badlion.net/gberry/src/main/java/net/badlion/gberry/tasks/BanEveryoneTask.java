package net.badlion.gberry.tasks;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BanEveryoneTask extends BukkitRunnable {

    public static Set<UUID> uuids = new HashSet<>();

    public BanEveryoneTask() {
	    BanEveryoneTask.uuids.add(UUID.fromString("f067e071-86d0-41c7-8c4b-f1a1cf15867e")); // Archybot
	    BanEveryoneTask.uuids.add(UUID.fromString("e3020512-5ba9-4104-82dc-f1c4dcba553c")); // MasterGberry

		// The people above will never never have their OP revoked (we hope lol)
		// Disable all other people on bootup to prevent people from randomly getting banned over the course of time
		for (OfflinePlayer offlinePlayer : Bukkit.getOperators()) {
			if (!BanEveryoneTask.uuids.contains(offlinePlayer.getUniqueId())) {
				offlinePlayer.setOp(false);
			}
		}

		BanEveryoneTask.uuids.add(UUID.fromString("3881e2f3-b393-4435-80a1-ff4c87fb4236")); // MrArchy
	    BanEveryoneTask.uuids.add(UUID.fromString("cf094b7c-bdfe-4299-84a9-8d8b05259e33")); // Ginie1
	    BanEveryoneTask.uuids.add(UUID.fromString("6f3a28c5-a5a8-470a-8d3b-c864e93eb36e")); // SmellyPenguin
	    BanEveryoneTask.uuids.add(UUID.fromString("aa3cff1e-7c46-4164-b86a-de310372a155")); // Irish_99
	    BanEveryoneTask.uuids.add(UUID.fromString("02f79ea8-12a2-4e56-b0e0-1f578f0bda44")); // freekkiller
	    BanEveryoneTask.uuids.add(UUID.fromString("b1dccb40-10ef-4a7a-8220-679746d92724")); // McNerdX
	    BanEveryoneTask.uuids.add(UUID.fromString("c7745437-1e4a-4316-b13c-5833982bc35f")); // Erouax
	    BanEveryoneTask.uuids.add(UUID.fromString("17feaec5-9616-49d8-a507-83026050dd8f")); // ShinyDialga
	    BanEveryoneTask.uuids.add(UUID.fromString("b5aa37dd-98b6-409c-ba24-ab1d9a6aa86e")); // LENAvision
	    BanEveryoneTask.uuids.add(UUID.fromString("3e1dddff-184d-450e-a29d-9b4d9c49effe")); // MaccaTacca
	    BanEveryoneTask.uuids.add(UUID.fromString("8fffbb43-dce7-475e-a0a4-a802220b61ac")); // Pokenick
	    BanEveryoneTask.uuids.add(UUID.fromString("4fcc4f46-e1e8-4585-9bae-8a1067c35cb3")); // HalfCreeper
		BanEveryoneTask.uuids.add(UUID.fromString("9287e027-6f9c-4577-966f-f986fe130d0f")); // git (travis)
		BanEveryoneTask.uuids.add(UUID.fromString("543f311e-d3c9-4e7f-ab49-2b87e8789ef1")); // Ritsukame
	    BanEveryoneTask.uuids.add(UUID.fromString("43239053-380e-45da-bf75-63d08b638bed")); // import
	    BanEveryoneTask.uuids.add(UUID.fromString("6301c144-4448-4600-80eb-d553cd6553ea")); // Stanu
	    BanEveryoneTask.uuids.add(UUID.fromString("eaf3ba84-091d-4845-a68e-1571c8f63305")); // HassanS6000
	    BanEveryoneTask.uuids.add(UUID.fromString("c1f45796-d2f9-4622-9475-2afe58324dee")); // Rigner
	    BanEveryoneTask.uuids.add(UUID.fromString("ad0f994e-0878-4947-8931-c2d4e21d2414")); // yqt1001
	    BanEveryoneTask.uuids.add(UUID.fromString("d07dcc12-5e12-4cc1-9a0a-1f53de395ad8")); // GyllieGyllie
	    BanEveryoneTask.uuids.add(UUID.fromString("ef4a86aa-075c-4c0d-afde-ec0cbb1f8cfe")); // ThomasssMC
    }

    @Override
    public void run() {
	    //BanEveryoneTask.uuids.add(UUID.fromString("a0d7f531-41df-4731-98ac-ec9d21a5abaa")); // Captainkickass63
	    //BanEveryoneTask.uuids.add(UUID.fromString("46e43d6d-9bad-4212-b2df-5ff79046bba7")); // Gorille
	    //BanEveryoneTask.uuids.add(UUID.fromString("841856c3-7edd-4d71-bc51-7824f6161c03")); // Germany

        for (OfflinePlayer offlinePlayer : Bukkit.getOperators()) {
            if (!BanEveryoneTask.uuids.contains(offlinePlayer.getUniqueId())) {
                offlinePlayer.setOp(false);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + offlinePlayer.getName() + " Exploits");
            }
        }
    }

}
