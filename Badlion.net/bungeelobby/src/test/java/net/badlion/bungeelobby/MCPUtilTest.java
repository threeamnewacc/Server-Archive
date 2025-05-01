package net.badlion.bungeelobby;

import junit.framework.Assert;
import net.badlion.bungeelobby.util.MCPUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;

public class MCPUtilTest {

    private static final String jsonChatData = "[{\"text\": \"" + DatatypeConverter.printBase64Binary("test".getBytes()) + "\"}, {\"text\": \"" + DatatypeConverter.printBase64Binary("test2".getBytes()) + "\", \"color\": \"6\", \"bold\": \"TRUE\", \"underline\": \"TRUE\", \"italic\": \"TRUE\", \"strikethrough\": \"TRUE\", \"click\": {\"action\": \"RUN_COMMAND\", \"text\": \"/test\"}, \"hover\": {\"action\": \"SHOW_TEXT\", \"parts\": [{\"text\": \"" + DatatypeConverter.printBase64Binary("test".getBytes()) + "\"}]}}]";
    private static final String jsonUrlChatData = "[{\"text\": \"" + DatatypeConverter.printBase64Binary("test".getBytes()) + "\"}, {\"text\": \"" + DatatypeConverter.printBase64Binary("This is a link www.google.com".getBytes()) + "\", \"bold\": \"TRUE\", \"underline\": \"TRUE\"}]";


    @Test
    public void testBuildComponent() {
        JSONParser parser = new JSONParser();
        JSONArray data;
        try {
            data = (JSONArray) parser.parse(MCPUtilTest.jsonChatData);
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse");
        }

        BaseComponent[] builderComponents = MCPUtil.buildComponent(data);

        ComponentBuilder actual = new ComponentBuilder("test")
                                          .append("test2")
                                          .color(ChatColor.GOLD)
                                          .bold(true)
                                          .underlined(true)
                                          .italic(true)
                                          .strikethrough(true)
                                          .event(new ClickEvent(
                                                                       ClickEvent.Action.RUN_COMMAND, "/test")
                                                )
                                          .event(new HoverEvent(
                                                                       HoverEvent.Action.SHOW_TEXT,
                                                                       new ComponentBuilder("test").create()
                                                                )
                                                );

        BaseComponent[] actualComponents = actual.create();

        StringBuilder builderBuilder = new StringBuilder();
        StringBuilder actualBuilder = new StringBuilder();

        for (BaseComponent component : builderComponents) {
            builderBuilder.append(component.toLegacyText());
        }

        for (BaseComponent component : actualComponents) {
            actualBuilder.append(component.toLegacyText());
        }

        Assert.assertEquals(builderBuilder.toString(), actualBuilder.toString());

        // Test nothing edge case
        JSONArray emptyArray = new JSONArray();
        BaseComponent[] emptyComponent = MCPUtil.buildComponent(emptyArray);
        BaseComponent[] actualEmptyComponent = new BaseComponent[1];

        Assert.assertEquals(emptyComponent[0], actualEmptyComponent[0]);

        // Test link creation
        try {
            data = (JSONArray) parser.parse(MCPUtilTest.jsonUrlChatData);
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse");
        }

        builderComponents = MCPUtil.buildComponent(data);
        actual = new ComponentBuilder("test").append("This is a link ").bold(true).underlined(true).append("www.google.com").bold(true).underlined(true).event(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://www.google.com")).append("").bold(true).underlined(true);
        actualComponents = actual.create();

        builderBuilder = new StringBuilder();
        actualBuilder = new StringBuilder();

        for (BaseComponent component : builderComponents) {
            builderBuilder.append(component.toLegacyText());
        }

        for (BaseComponent component : actualComponents) {
            actualBuilder.append(component.toLegacyText());
        }

        Assert.assertEquals(builderBuilder.toString(), actualBuilder.toString());
    }

}
