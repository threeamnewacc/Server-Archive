// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.manager;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import club.mineman.core.api.request.RequestCallback;
import club.mineman.core.api.APIMessage;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.LinkedList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import club.mineman.core.api.request.AbstractCallback;
import club.mineman.antigamingchair.request.banwave.AGCGetBanWaveRequest;
import club.mineman.core.CorePlugin;
import java.util.Map;
import java.util.Deque;
import club.mineman.antigamingchair.AntiGamingChair;

public class BanWaveManager
{
    private final AntiGamingChair plugin;
    private final Deque<Long> cheaters;
    private final Map<Long, String> cheaterNames;
    private boolean banWaveStarted;
    
    public void loadCheaters() {
        CorePlugin.getInstance().getRequestManager().sendRequest((APIMessage)new AGCGetBanWaveRequest(), (RequestCallback)new AbstractCallback("Error fetching the ban wave list.") {
            public void callback(final JSONObject data) {
                final JSONArray array = (JSONArray)data.get((Object)"data");
                final List<Long> cheaters = new LinkedList<Long>();
                for (final Object object : array) {
                    final JSONObject jsonObject = (JSONObject)object;
                    final long id = (long)jsonObject.get((Object)"id");
                    final String name = (String)jsonObject.get((Object)"name");
                    cheaters.add(id);
                    BanWaveManager.this.cheaterNames.put(id, name);
                }
                cheaters.sort((integer, t1) -> {
                    final String name2 = BanWaveManager.this.cheaterNames.get(integer);
                    final String otherName = BanWaveManager.this.cheaterNames.get(t1);
                    return name2.compareToIgnoreCase(otherName);
                });
                BanWaveManager.this.cheaters.addAll(cheaters);
            }
        });
    }
    
    public void clearCheaters() {
        this.cheaters.clear();
        this.cheaterNames.clear();
    }
    
    public long queueCheater() {
        return this.cheaters.poll();
    }
    
    public String getCheaterName(final long id) {
        return this.cheaterNames.get(id);
    }
    
    public AntiGamingChair getPlugin() {
        return this.plugin;
    }
    
    public Deque<Long> getCheaters() {
        return this.cheaters;
    }
    
    public Map<Long, String> getCheaterNames() {
        return this.cheaterNames;
    }
    
    public boolean isBanWaveStarted() {
        return this.banWaveStarted;
    }
    
    @ConstructorProperties({ "plugin" })
    public BanWaveManager(final AntiGamingChair plugin) {
        this.cheaters = new LinkedList<Long>();
        this.cheaterNames = new HashMap<Long, String>();
        this.plugin = plugin;
    }
    
    public void setBanWaveStarted(final boolean banWaveStarted) {
        this.banWaveStarted = banWaveStarted;
    }
}
