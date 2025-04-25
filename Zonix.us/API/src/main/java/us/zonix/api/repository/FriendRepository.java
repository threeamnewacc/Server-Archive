package us.zonix.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import us.zonix.api.model.Friend;

import java.util.List;

@Repository
public interface FriendRepository extends CrudRepository<Friend, Integer> {

    Friend findById(int id);

    Friend findFirstBySenderAndReceiver(String sender, String receiver);

    List<Friend> findAllBySenderOrReceiverAndAccepted(String sender, String receiver, boolean accepted);

}
