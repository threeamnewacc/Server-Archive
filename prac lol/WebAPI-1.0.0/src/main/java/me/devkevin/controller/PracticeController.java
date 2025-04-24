package me.devkevin.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.devkevin.model.Player;
import me.devkevin.model.PracticeProfile;
import me.devkevin.repo.PlayerRepository;
import me.devkevin.repo.PracticeRepository;
import me.devkevin.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by DevKevin
 * Project: WebAPI
 * Date: 23/02/2022 @ 3:32
 */
@RestController
@RequestMapping("/api/{key}/practice")
public class PracticeController {
    private final Map<String, Field> fields = new HashMap<>();

    static {
        // Run this first so whenever it caches reflection data
        PracticeProfile practiceData = new PracticeProfile();

        Field[] fields = practiceData.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);

            try {
                if (field.getType() == boolean.class) {
                    field.set(practiceData, Boolean.valueOf("false"));
                } else if (field.getType() == int.class) {
                    field.set(practiceData, Integer.valueOf("123"));
                } else if (field.getType() == double.class) {
                    field.set(practiceData, Double.valueOf("123"));
                } else if (field.getType() == String.class) {
                    field.set(practiceData, "xd");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Autowired private PracticeRepository practiceRepository;
    @Autowired private PlayerRepository playerRepository;
    @PersistenceContext private EntityManager entityManager;

    @RequestMapping("/{uuid}")
    public ResponseEntity<PracticeProfile> getData(@PathVariable(name = "key") String key,
                                                @PathVariable(name = "uuid") UUID uuid) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        Player player = PlayerController.getPlayer(this.playerRepository, uuid);
        PracticeProfile practiceData = this.practiceRepository.findFirstByPlayerId(player.getPlayerId());

        if (practiceData == null) {
            return null;
        }

        return new ResponseEntity<>(practiceData, HttpStatus.OK);
    }


    @RequestMapping("/leaderboards/{ladder}")
    public ResponseEntity<String> getLeaderboards(@PathVariable(name = "key") String key,
                                                  @PathVariable(name = "ladder") String ladder) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        String columnName = this.deserializeName(ladder + "Elo");

        List<PracticeProfile> list = entityManager
                .createNativeQuery("SELECT * FROM practice_season_1_data ORDER BY ? DESC LIMIT 10", PracticeProfile.class)
                .setParameter(1, columnName)
                .getResultList();
        list.sort((o1, o2) -> {
            try {
                Field field = fields.computeIfAbsent(ladder + "Elo", name -> {
                    try {
                        Field practiceField = PracticeProfile.class.getDeclaredField(name);
                        practiceField.setAccessible(true);
                        return practiceField;
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }

                    return null;
                });

                return Integer.compare(field.getInt(o2), field.getInt(o1));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return 0;
        });

        JsonArray array = new JsonArray();
        list.forEach(data -> {
            JsonObject object = new JsonObject();
            Field field = fields.get(ladder + "Elo");
            if (field == null) {
                return;
            }

            Player player = playerRepository.findFirstByPlayerId(data.getPlayerId());
            if (player == null) {
                System.out.println("wtf " + data.getPlayerId());
                return;
            }

            try {
                object.addProperty("name", player.getName());
                object.addProperty("elo", field.getInt(data));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return;
            }

            array.add(object);
        });

        return new ResponseEntity<>(array.toString(), HttpStatus.OK);
    }

    @RequestMapping("/{id}/update")
    public ResponseEntity<PracticeProfile> updateData(HttpServletRequest request,
                                                   @PathVariable(name = "key") String key,
                                                   @PathVariable(name = "id") int id) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        PracticeProfile practiceData = this.practiceRepository.findFirstByPlayerId(id);

        if (practiceData == null) {
            practiceData = new PracticeProfile();
            practiceData.setPlayerId(id);
        }

        Field[] fields = practiceData.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);

            String name = this.deserializeName(field.getName());

            String param = request.getParameter(name);
            if (param == null) {
                continue;
            }

            try {
                if (field.getType() == boolean.class) {
                    field.set(practiceData, Boolean.valueOf(param));
                } else if (field.getType() == int.class) {
                    field.set(practiceData, Integer.valueOf(param));
                } else if (field.getType() == double.class) {
                    field.set(practiceData, Double.valueOf(param));
                } else if (field.getType() == String.class) {
                    field.set(practiceData, param);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        this.practiceRepository.save(practiceData);

        return new ResponseEntity<>(practiceData, HttpStatus.OK);
    }

    private String deserializeName(String name) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < name.length(); i++) {
            char c = name.toCharArray()[i];

            if (Character.isUpperCase(c)) {
                sb.append("_");
            }

            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

}
