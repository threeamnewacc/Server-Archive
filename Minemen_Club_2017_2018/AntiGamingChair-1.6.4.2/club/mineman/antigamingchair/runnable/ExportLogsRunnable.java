// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.runnable;

import java.beans.ConstructorProperties;
import club.mineman.core.util.finalutil.CC;
import java.util.Iterator;
import java.util.Set;
import club.mineman.core.api.APIMessage;
import club.mineman.core.api.request.RequestCallback;
import club.mineman.antigamingchair.request.AGCLogRequest;
import club.mineman.core.CorePlugin;
import java.sql.Timestamp;
import org.json.simple.JSONObject;
import club.mineman.antigamingchair.log.Log;
import org.json.simple.JSONArray;
import java.util.HashSet;
import club.mineman.antigamingchair.AntiGamingChair;

public class ExportLogsRunnable implements Runnable
{
    private final AntiGamingChair plugin;
    
    @Override
    public void run() {
        if (this.plugin.getLogManager().getLogQueue().isEmpty()) {
            return;
        }
        final Set<JSONArray> data = new HashSet<JSONArray>();
        JSONArray current = new JSONArray();
        for (final Log log : this.plugin.getLogManager().getLogQueue()) {
            final JSONObject object = new JSONObject();
            object.put((Object)"timestamp", (Object)new Timestamp(log.getTimestamp()).toString());
            object.put((Object)"player-id", (Object)log.getMinemanId());
            object.put((Object)"log", (Object)log.getLog());
            current.add((Object)object.toJSONString());
            if (current.toJSONString().length() >= 1000) {
                data.add(current);
                current = new JSONArray();
            }
        }
        if (current.size() > 0) {
            data.add(current);
        }
        for (final JSONArray array : data) {
            CorePlugin.getInstance().getRequestManager().sendRequest((APIMessage)new AGCLogRequest(array), (RequestCallback)new RequestCallback() {
                public void callback(final JSONObject data) {
                    final String response = (String)data.get((Object)"response");
                    if (!response.equals("success")) {
                        ExportLogsRunnable.this.onError(data.toJSONString());
                    }
                }
                
                public void error(final String message) {
                    ExportLogsRunnable.this.onError(message);
                }
            });
        }
        this.plugin.getLogManager().clearLogQueue();
    }
    
    private void onError(final String message) {
        this.plugin.getAlertsManager().forceAlert(CC.D_RED + "ERROR SAVING LOGS! Check console for details.");
        this.plugin.getLogger().severe(message);
    }
    
    @ConstructorProperties({ "plugin" })
    public ExportLogsRunnable(final AntiGamingChair plugin) {
        this.plugin = plugin;
    }
}
