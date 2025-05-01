package net.badlion.bungeelobby.tasks;

import net.badlion.bungeelobby.BungeeLobby;
import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.md_5.bungee.BungeeCord;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.simple.JSONObject;

public class RebootTimeTask implements Runnable {

    BungeeLobby plugin;
    boolean threeHour;
    boolean twoHour;
    boolean oneHour;
    boolean thirtyMin;
    boolean tenMin;
    boolean fiveMin;
    boolean oneMin;
    boolean tenSec;
    boolean needsDelete;

    public RebootTimeTask(BungeeLobby plugin) {
        this.plugin = plugin;
        this.threeHour = false;
        this.twoHour = false;
        this.oneHour = false;
        this.thirtyMin = false;
        this.tenMin = false;
        this.fiveMin = false;
        this.oneMin = false;
        this.tenSec = false;
        this.needsDelete = false;
    }

    public void run() {
        if (needsDelete) {
            try {
                String rec_id = plugin.getRecID(BungeeLobby.cloudflareIP);

                if (rec_id.equals("-1")) {
                    needsDelete = false;
                } else if (!rec_id.equals("-2")){
                    String urlString = "https://www.cloudflare.com/api_json.html?a=rec_delete&z=badlion.net"
                            + "&tkn=" + BungeeLobby.cloudflareKey
                            + "&email=" + BungeeLobby.cloudflareEmail
                            + "&id=" + rec_id;
                    JSONObject json = new JSONObject();

                    JSONObject response = HTTPCommon.executePOSTRequest(urlString, json, 60000);
                    if (response != null) {
                        if (response.containsKey("result")) {
                            if (((String) response.get("result")).equals("success")) {
                                needsDelete = false;
                            }
                        }
                    }
                }
            } catch (HTTPRequestFailException e) {

            }

        }

        DateTime currentTime = new DateTime(DateTimeZone.UTC);
        if (!threeHour && currentTime.isAfter(BungeeLobby.restartTime.minusHours(3))) {
            threeHour = true;
            if (BungeeLobby.delete_record == 1) {
                needsDelete = true;
            }
            BungeeCord.getInstance().getPluginManager().dispatchCommand(BungeeCord.getInstance().getConsole(),
                    "alert This bungeelobby proxy is going down for a restart in 3 hours!");
        } else if (!twoHour && currentTime.isAfter(BungeeLobby.restartTime.minusHours(2))) {
            twoHour = true;
            BungeeCord.getInstance().getPluginManager().dispatchCommand(BungeeCord.getInstance().getConsole(),
                    "alert This bungeelobby proxy is going down for a restart in 2 hours!");
        } else if (!oneHour && currentTime.isAfter(BungeeLobby.restartTime.minusHours(1))) {
            oneHour = true;
            BungeeCord.getInstance().getPluginManager().dispatchCommand(BungeeCord.getInstance().getConsole(),
                    "alert This bungeelobby proxy is going down for a restart in 1 hour!");
        } else if (!thirtyMin && currentTime.isAfter(BungeeLobby.restartTime.minusMinutes(30))) {
            thirtyMin = true;
            BungeeCord.getInstance().getPluginManager().dispatchCommand(BungeeCord.getInstance().getConsole(),
                    "alert This bungeelobby proxy is going down for a restart in 30 minutes!");
        } else if (!tenMin && currentTime.isAfter(BungeeLobby.restartTime.minusMinutes(10))) {
            tenMin = true;
            BungeeCord.getInstance().getPluginManager().dispatchCommand(BungeeCord.getInstance().getConsole(),
                    "alert This bungeelobby proxy is going down for a restart in 10 minutes!");
        } else if (!fiveMin && currentTime.isAfter(BungeeLobby.restartTime.minusMinutes(5))) {
            fiveMin = true;
            BungeeCord.getInstance().getPluginManager().dispatchCommand(BungeeCord.getInstance().getConsole(),
                    "alert This bungeelobby proxy is going down for a restart in 5 minutes!");
        } else if (!oneMin && currentTime.isAfter(BungeeLobby.restartTime.minusMinutes(1))) {
            oneMin = true;
            BungeeCord.getInstance().getPluginManager().dispatchCommand(BungeeCord.getInstance().getConsole(),
                    "alert This bungeelobby proxy is going down for a restart in 1 minute!");
        } else if (!tenSec && currentTime.isAfter(BungeeLobby.restartTime.minusSeconds(10))) {
            tenSec = true;
            BungeeCord.getInstance().getPluginManager().dispatchCommand(BungeeCord.getInstance().getConsole(),
                    "alert This bungeelobby proxy is going down for a restart in 10 seconds!");
        } else if (currentTime.isAfter(BungeeLobby.restartTime)) {
            BungeeCord.getInstance().stop();
        }
    }
}