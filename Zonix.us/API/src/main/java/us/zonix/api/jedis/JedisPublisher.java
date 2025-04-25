package us.zonix.api.jedis;

import redis.clients.jedis.Jedis;
import us.zonix.api.Application;

public class JedisPublisher {

    public void write(String message) {
        Jedis jedis = null;

        try {
            //jedis = Application.getController().getPool().getResource();
            //jedis.publish("core-bukkit", message);
            //System.out.println(message);
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


}
