package us.zonix.api.jedis;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import redis.clients.jedis.JedisPubSub;
import us.zonix.api.Application;
import us.zonix.api.model.Player;
import us.zonix.api.model.Punishment;
import us.zonix.api.repository.PlayerRepository;
import us.zonix.api.repository.PunishmentRepository;

import java.sql.Timestamp;

public class JedisSubscriber {

    @Getter private JedisPubSub pubSub;

    private PlayerRepository playerRepository = Application.getInstance().getPlayerRepository();
    private PunishmentRepository punishmentRepository = Application.getInstance().getPunishmentRepository();

    public JedisSubscriber() {
        this.pubSub = get();

        new Thread(() -> {
            //JedisPool pool = Application.getController().getPool();

            //if (pool != null) {
                //pool.getResource().subscribe(pubSub, "core-api");
            //}
        }).start();
    }

    private JedisPubSub get() {
        return new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                if (channel.equalsIgnoreCase("core-api")) {
                    String[] args = message.split(";");
                    JsonObject data = (JsonObject) new JsonParser().parse(args[1]);

                    if (args[0].equalsIgnoreCase("SAVE_PLAYER")) {
                        String uuid = data.get("uuid").getAsString();

                        Player player = playerRepository.findFirstByUuid(uuid);

                        if (player == null) {
                            player = new Player();
                            player.setUuid(uuid);
                            player.setRank("DEFAULT");
                            player.setFirstLogin(new Timestamp(System.currentTimeMillis()));
                        }

                        // only update name if not JsonNull
                        if (!data.get("name").isJsonNull()) {
                            player.setName(data.get("name").getAsString());
                        }

                        player.setLastLogin(new Timestamp(data.get("last_login").getAsLong()));
                        player.setLastServer(data.get("last_server").getAsString());
                        player.setIp(data.get("ip_address").getAsString());

                        playerRepository.save(player);
                    }
                    else if (args[0].equalsIgnoreCase("ADD_PUNISHMENT")) {
                        String type = data.get("type").getAsString();
                        String uuid = data.get("uuid").getAsString();
                        String reason = data.get("reason").getAsString();
                        Long duration = data.get("duration").getAsLong();

                        Punishment punishment = new Punishment();
                        punishment.setType(type);
                        punishment.setUuid(uuid);
                        punishment.setReason(reason);
                        punishment.setDuration(duration);
                        punishment.setAddedAt(new Timestamp(System.currentTimeMillis()));

                        if (data.has("sender_uuid")) {
                            punishment.setAddedBy(data.get("sender_uuid").getAsString());
                        }

                        punishmentRepository.save(punishment);

                        JsonObject object = punishment.toJson();
                        object.addProperty("silent", data.get("silent").getAsBoolean());
                        object.addProperty("name", data.get("name").getAsString());
                        object.addProperty("sender_name", data.get("sender_name").getAsString());

                        //Application.getBukkitPublisher().write("ADD_PUNISHMENT;" + object.toString());
                    }
                    else if (args[0].equalsIgnoreCase("REMOVE_PUNISHMENT")) {

                        System.out.println(data);

                        Integer id = data.get("punishment_id").getAsInt();

                        Punishment punishment = punishmentRepository.findFirstById(id);
                        punishment.setRemovedReason(data.get("remove_reason").getAsString());
                        punishment.setRemovedAt(new Timestamp(System.currentTimeMillis()));

                        if (data.has("sender_uuid")) {
                            punishment.setRemovedBy(data.get("sender_uuid").getAsString());
                        }

                        JsonObject object = punishment.toJson();
                        object.addProperty("silent", data.get("silent").getAsBoolean());
                        object.addProperty("name", data.get("name").getAsString());
                        object.addProperty("sender_name", data.get("sender_name").getAsString());

                        //Application.getBukkitPublisher().write("REMOVE_PUNISHMENT;" + object.toString());

                        punishmentRepository.save(punishment);
                    }
                    else if (args[0].equalsIgnoreCase("UPDATE_RANK")) {
                        String uuid = data.get("uuid").getAsString();
                        String rank = data.get("rank").getAsString();

                        Player player = playerRepository.findFirstByUuid(uuid);
                        player.setRank(rank);

                        playerRepository.save(player);

                        //Application.getBukkitPublisher().write("UPDATE_RANK;" + data.toString());
                    }
                    else if (args[0].equalsIgnoreCase("UPDATE_REGISTER")) {
                        String uuid = data.get("uuid").getAsString();
                        String emailAddress = data.get("email_address").getAsString();
                        String confirmationId = data.get("confirmation_id").getAsString();
                        boolean registered = data.get("registered").getAsBoolean();

                        Player player = playerRepository.findFirstByUuid(uuid);
                        player.setEmailAddress(emailAddress);
                        player.setConfirmationId(confirmationId);
                        player.setRegistered(registered);

                        playerRepository.save(player);

                        //Application.getBukkitPublisher().write("UPDATE_REGISTER;" + data.toString());
                    }
                }
            }
        };
    }
}
