package us.zonix.core.redis.queue;

import java.util.Map;
import java.util.concurrent.*;

import com.google.gson.Gson;
import org.bukkit.event.*;
import org.bukkit.entity.*;
import us.zonix.core.CorePlugin;
import us.zonix.core.redis.queue.Queue;
import us.zonix.core.shared.redis.JedisPublisher;
import us.zonix.core.shared.redis.JedisSubscriber;
import us.zonix.core.shared.redis.subscription.JedisSubscriptionHandler;

public class QueueManager implements Listener {

    private final Map<String, Queue> queues;
    private final Gson gson;

    private CorePlugin plugin;
    private final boolean serverOnline;

    private final JedisSubscriber<String> managerSubscriber;
    private final JedisPublisher<String> managerPublisher;

    private String[] availableQueues = new String[]{
            "practice-us",
            "practice-eu",
            "practice-as",
            "practice-sa",
            "kitmap-us",
            "hcf-us",
            "sg-01",
            "sg-02",
            "sg-03",
            "sg-04"
    };

    public QueueManager(CorePlugin plugin) {
        this.plugin = plugin;
        this.gson = new Gson();

        this.queues = new ConcurrentHashMap<>(10, 0.5f, 4);
        this.serverOnline = true;

        this.managerSubscriber = new JedisSubscriber<>(this.plugin.getJedisSettings(), "queuemanager", String.class, new QueueManagerSubscriptionHandler());
        this.managerPublisher = new JedisPublisher<>(this.plugin.getJedisSettings(), "queuemanager");

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            for (String queue : this.availableQueues) {
                Queue actualQueue = new Queue(plugin, queue);

                this.queues.put(queue.toLowerCase(), actualQueue);

                this.managerPublisher.writeDirectly("addServer`" + queue.replace("-", "_").toLowerCase());

                System.out.println("[Queue] Added " + queue.toLowerCase() + " server.");

                if (queue.toLowerCase().equalsIgnoreCase(this.plugin.getServerId())) {
                    actualQueue.runServerStatus();

                    System.out.println("[Queue] Server status detected & now running on " + queue.toLowerCase() + ".");
                }
            }
        });
    }

    public Queue getQueue(final String name) {
        return this.queues.get(name);
    }

    public boolean isServerOnline() {
        return this.serverOnline;
    }

    public Queue getQueue(final Player player) {
        for (final Queue queue : this.queues.values()) {
            if (queue.contains(player)) {
                return queue;
            }
        }

        return null;
    }

    private class QueueManagerSubscriptionHandler implements JedisSubscriptionHandler<String> {
        @Override
        public void handleMessage(String message) {
            if (message.equals("hello")) {
                for (final String server : queues.keySet()) {
                    managerPublisher.writeDirectly("addServer`" + server.toLowerCase().replace("-", "_"));
                }
            } else if (message.equals("goodbye")) {
                for (final Queue queue : queues.values()) {
                    queue.clear();
                }
            }
        }
    }

}
