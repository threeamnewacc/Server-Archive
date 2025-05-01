package net.badlion.gberry.utils;

import net.badlion.gberry.managers.MCPManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

/**
 * Wrapper Util for MCP to properly parse responses from our Protocol system
 */
public class MCPUtil {

    public static void handleResponse(MCPManager.MCP_MESSAGE msg, JSONObject data, JSONObject response) {
        if (response == null) {
            return;
        }
    }

}
