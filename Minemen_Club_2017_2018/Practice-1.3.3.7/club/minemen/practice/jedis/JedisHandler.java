// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.jedis;

import com.google.gson.JsonObject;
import club.minemen.core.redis.subscription.JedisSubscriptionHandler;

public class JedisHandler implements JedisSubscriptionHandler<JsonObject>
{
    public void handleMessage(final JsonObject jsonObject) {
        final String action = jsonObject.get("action").getAsString();
        if (action.equalsIgnoreCase("reset")) {
            final String type = jsonObject.get("type").getAsString();
            if (!type.equalsIgnoreCase("premium_matches")) {
                if (type.equalsIgnoreCase("premium_elo")) {}
            }
        }
    }
}
