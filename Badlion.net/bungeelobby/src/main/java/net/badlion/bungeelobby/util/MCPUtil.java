package net.badlion.bungeelobby.util;

import net.badlion.bungeelobby.BungeeLobby;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Wrapper Util for MCP to properly parse responses from our Protocol system
 */
public class MCPUtil {

    private static final Pattern url = Pattern.compile("((?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?)");

    public static void handleResponse(JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }

        if (!jsonObject.containsKey("actions")) {
            return;
        }

        JSONArray actions = (JSONArray) jsonObject.get("actions");
        for (JSONObject actionObject : (List<JSONObject>) actions) {
            switch ((String) actionObject.get("action")) {
                case "MESSAGE_PLAYER":
                    MCPUtil.handlePlayerMessage(actionObject);
                    break;
                case "DC_PLAYER":
                    MCPUtil.handleDCPlayer(actionObject);
                    break;
                case "CONNECT":
                    MCPUtil.handleConnectPlayer(actionObject);
                    break;
                case "SEND_TO_LOBBY":
                    MCPUtil.handleSendToLobby(actionObject);
                    break;
            }
        }
    }

    public static void handleSendToLobby(JSONObject action) {
        ProxiedPlayer receiver = BungeeLobby.getInstance().getProxy().getPlayer(UUID.fromString((String) action.get("target")));
        if (receiver != null) {
            String lobbyInfo = BungeeLobby.getInstance().getProxy().getConfig().getListeners().iterator().next().getDefaultServer();
            receiver.connect(BungeeLobby.getInstance().getProxy().getServerInfo(lobbyInfo));
        }
    }

    public static void handleConnectPlayer(JSONObject action) {
        ServerInfo server = ProxyServer.getInstance().getServerInfo((String) action.get("server"));

        // dynamic instance fail-safe, mcp will send an IP and we will add it if we didn't already have it
        if (server == null && action.containsKey("address")) {
            String[] ipAndPort = ((String) action.get("address")).split(":");
            server = ProxyServer.getInstance().constructServerInfo((String) action.get("server"), new InetSocketAddress(ipAndPort[0], Integer.parseInt(ipAndPort[1])), "", false);
        }

        ProxiedPlayer receiver = ProxyServer.getInstance().getPlayer(UUID.fromString((String) action.get("target")));
        if (receiver != null && server != null) {
            receiver.connect(server);
        }
    }

    public static void handleDCPlayer(JSONObject action) {
        ProxiedPlayer receiver = BungeeLobby.getInstance().getProxy().getPlayer(UUID.fromString((String) action.get("target")));
        if (receiver != null) {
            JSONArray jsonArray = (JSONArray) action.get("message");
            receiver.disconnect(MCPUtil.buildComponent(jsonArray));
        }
    }

    public static void handlePlayerMessage(JSONObject action) {
        ProxiedPlayer receiver = BungeeLobby.getInstance().getProxy().getPlayer(UUID.fromString((String) action.get("target")));
        if (receiver != null) {
            JSONArray jsonArray = (JSONArray) action.get("message");
            receiver.sendMessage(MCPUtil.buildComponent(jsonArray));
        }
    }

    private static BaseComponent[] createLinks(BaseComponent baseComponent) {
        /*def create_links(part):
            # If it has a click event bail out
            if 'click' in part.to_dict():
                return [part]

            match = web_pattern.search(part.text, 0)
            if not match:
                return [part]

            print 'trying to build link for ' + part.text

            # Clone it (link part)
            cloned = part.clone()
            cloned.text = part.text[match.start():match.end()]
            cloned.click(ClickEvent(ClickEvent.OPEN_URL, cloned.text))

            # Clone it (after link part)
            a_cloned = part.clone()
            a_cloned.text = part.text[match.end():]

            # Chop off after link text from the original
            part.text = part.text[:match.start()]

            # Recursively go through after (might have it again)
            other_parts = create_links(a_cloned)

            return_data = [part, cloned]
            for o in other_parts:
                return_data.append(o)

            return return_data*/
        if (!(baseComponent instanceof TextComponent)) {
            return new BaseComponent[] {baseComponent};
        }

        TextComponent textComponent = (TextComponent) baseComponent;

        if (textComponent.getClickEvent() != null) {
            return new BaseComponent[] {textComponent};
        }

        String text = textComponent.toPlainText();
        Matcher m = url.matcher(text);
        if (!m.find()) {
            return new BaseComponent[] {textComponent};
        }

        // Clone the link part
        TextComponent cloned = (TextComponent) textComponent.duplicate();
        cloned.setText(text.substring(m.start(), m.end()));
        cloned.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, cloned.getText().startsWith("http") ? cloned.getText() : "http://" + cloned.getText()));

        // Clone the after link part
        TextComponent after = (TextComponent) textComponent.duplicate();
        after.setText(text.substring(m.end()));

        // Chop off up until the link from the original part
        textComponent.setText(text.substring(0, m.start()));

        BaseComponent[] otherParts = MCPUtil.createLinks(after);

        List<BaseComponent> returnParts = new ArrayList<>();
        returnParts.add(textComponent);
        returnParts.add(cloned);
        for (BaseComponent b : otherParts) {
            returnParts.add(b);
        }

        return returnParts.toArray(new BaseComponent[returnParts.size()]);
    }

    public static BaseComponent[] buildComponent(JSONArray parts) {
        ComponentBuilder builder = null;
        for (JSONObject part : (List<JSONObject>) parts) {
            String decodedText = new String(DatatypeConverter.parseBase64Binary((String) part.get("text")));

            if (builder == null) {
                builder = new ComponentBuilder(decodedText); // Decode
            } else {
                builder.append(decodedText); // Decode
            }

            MCPUtil.addColorAndEvents(builder, part);
        }

        if (builder == null) {
            return new BaseComponent[1];
        }

        // Go through and re-create it now with the URL's created into click events
        BaseComponent[] baseComponents = builder.create();
        List<BaseComponent> finalComponents = new ArrayList<>();
        for (BaseComponent baseComponent : baseComponents) {
            // Take results and add them
            for (BaseComponent bc : MCPUtil.createLinks(baseComponent.duplicate())) {
                finalComponents.add(bc);
            }
        }

        return finalComponents.toArray(new BaseComponent[finalComponents.size()]);
    }

    public static void addColorAndEvents(ComponentBuilder builder, JSONObject part) {
        if (part.containsKey("color")) {
            builder.color(ChatColor.getByChar(((String) part.get("color")).charAt(0)));
        }

        if (part.containsKey("hover")) {
            JSONObject hoverEvent = (JSONObject) part.get("hover");
            JSONArray parts = (JSONArray) hoverEvent.get("parts");
            builder.event(new HoverEvent(HoverEvent.Action.valueOf((String) hoverEvent.get("action")),
                                         MCPUtil.buildComponent(parts)));
        }

        if (part.containsKey("click")) {
            JSONObject clickEvent = (JSONObject) part.get("click");
            builder.event(new ClickEvent(ClickEvent.Action.valueOf((String) clickEvent.get("action")), (String) clickEvent.get("text")));
        }

        if (part.containsKey("bold")) {
            builder.bold(true);
        } else {
            builder.bold(false);
        }

        if (part.containsKey("underline")) {
            builder.underlined(true);
        } else {
            builder.underlined(false);
        }

        if (part.containsKey("italic")) {
            builder.italic(true);
        } else {
            builder.italic(false);
        }

        if (part.containsKey("strikethrough")) {
            builder.strikethrough(true);
        } else {
            builder.strikethrough(false);
        }
    }

    public static String convertLegacyText(String text) {
        return text.replaceAll("&([0-9a-fA-Fk-rK-R])", "§$1");
    }

}
