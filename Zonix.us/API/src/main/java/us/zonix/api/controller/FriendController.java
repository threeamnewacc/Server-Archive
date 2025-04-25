package us.zonix.api.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import us.zonix.api.model.Friend;
import us.zonix.api.repository.FriendRepository;
import us.zonix.api.util.Constants;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;

@RestController
@RequestMapping("/api/{key}/friend")
@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
public class FriendController {

    @Autowired
    private FriendRepository friendRepository;

    @RequestMapping("/fetch_accepted/{uuid}")
    public ResponseEntity<String> fetchAccepted(@PathVariable("key") String key, @PathVariable("uuid") String uuid) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        JsonArray friends = new JsonArray();

        for (Friend friend : this.friendRepository.findAllBySenderOrReceiverAndAccepted(uuid, uuid, true)) {
            friends.add(friend.toJson());
        }

        return new ResponseEntity<>(friends.toString(), HttpStatus.OK);
    }

    @RequestMapping("/fetch_pending/{uuid}")
    public ResponseEntity<String> fetchPending(@PathVariable("key") String key, @PathVariable("uuid") String uuid) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        JsonArray friends = new JsonArray();

        for (Friend friend : this.friendRepository.findAllBySenderOrReceiverAndAccepted(uuid, uuid, false)) {
            friends.add(friend.toJson());
        }

        return new ResponseEntity<>(friends.toString(), HttpStatus.OK);
    }

    @RequestMapping("/remove_friend")
    public ResponseEntity<String> removeFriend(@PathVariable("key") String key, HttpServletRequest request) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        String param = request.getParameter("data");

        if (param == null) {
            return null;
        }

        JsonObject data = new JsonParser().parse(param).getAsJsonObject();

        if (data.has("id") && data.has("uuid")) {
            Integer id = data.get("id").getAsInt();
            String uuid = data.get("uuid").getAsString();

            Friend friend = this.friendRepository.findById(id);

            if (friend == null) {
                return null;
            }
            else {
                if (friend.getSender().equalsIgnoreCase(uuid) || friend.getReceiver().equalsIgnoreCase(uuid)) {
                    this.friendRepository.delete(friend);

                    return new ResponseEntity<>(Constants.SUCCESS, HttpStatus.OK);
                }
                else {
                    return null;
                }
            }
        }
        else {
            return null;
        }
    }

    @RequestMapping("/request_or_accept")
    public ResponseEntity<String> requestOrAccept(@PathVariable("key") String key, HttpServletRequest request) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        String param = request.getParameter("data");

        if (param == null) {
            return null;
        }

        JsonObject data = new JsonParser().parse(param).getAsJsonObject();

        if (data.has("sender") && data.has("receiver")) {
            String sender = data.get("sender").getAsString();
            String receiver = data.get("receiver").getAsString();

            Friend friend = this.friendRepository.findFirstBySenderAndReceiver(sender, receiver);

            if (friend != null) {
                return new ResponseEntity<>("already-sent", HttpStatus.OK);
            }

            friend = this.friendRepository.findFirstBySenderAndReceiver(receiver, sender);

            if (friend != null) {
                friend.setAccepted(true);
                friend.setAcceptedTimestamp(new Timestamp(System.currentTimeMillis()));

                this.friendRepository.save(friend);

                return new ResponseEntity<>(Constants.SUCCESS, HttpStatus.OK);
            }

            friend = new Friend();
            friend.setSender(sender);
            friend.setReceiver(receiver);
            friend.setSentTimestamp(new Timestamp(System.currentTimeMillis()));
            friend.setAccepted(false);

            this.friendRepository.save(friend);

            return new ResponseEntity<>(Constants.SUCCESS, HttpStatus.OK);
        }
        else {
            return null;
        }
    }

}