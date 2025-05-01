package net.badlion.bungeelobby;

import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class FakeProxiedPlayer implements ProxiedPlayer {

    private UUID uuid;
    private String username;

    public FakeProxiedPlayer(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    @Override
    public String getDisplayName() {
        return this.username;
    }

    @Override
    public void setDisplayName(String s) {
        this.username = s;
    }

    @Override
    public void sendMessage(ChatMessageType chatMessageType, BaseComponent... baseComponents) {

    }

    @Override
    public void sendMessage(ChatMessageType chatMessageType, BaseComponent baseComponent) {

    }

    @Override
    public void connect(ServerInfo serverInfo) {

    }

    @Override
    public void connect(ServerInfo serverInfo, Callback<Boolean> callback) {

    }

    @Override
    public Server getServer() {
        return null;
    }

    @Override
    public int getPing() {
        return 0;
    }

    @Override
    public void sendData(String s, byte[] bytes) {

    }

    @Override
    public PendingConnection getPendingConnection() {
        return null;
    }

    @Override
    public void chat(String s) {

    }

    @Override
    public ServerInfo getReconnectServer() {
        return null;
    }

    @Override
    public void setReconnectServer(ServerInfo serverInfo) {

    }

    @Override
    public String getUUID() {
        return this.uuid.toString();
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public Locale getLocale() {
        return Locale.ENGLISH;
    }

    @Override
    public void setTabHeader(BaseComponent baseComponent, BaseComponent baseComponent1) {

    }

    @Override
    public void setTabHeader(BaseComponent[] baseComponents, BaseComponent[] baseComponents1) {

    }

    @Override
    public void resetTabHeader() {

    }

    @Override
    public void sendTitle(Title title) {

    }

    @Override
    public boolean isForgeUser() {
        return false;
    }

    @Override
    public Map<String, String> getModList() {
        return null;
    }

    @Override
    public String getName() {
        return this.username;
    }

    @Override
    public void sendMessage(String s) {

    }

    @Override
    public void sendMessages(String... strings) {

    }

    @Override
    public void sendMessage(BaseComponent... baseComponents) {

    }

    @Override
    public void sendMessage(BaseComponent baseComponent) {

    }

    @Override
    public Collection<String> getGroups() {
        return null;
    }

    @Override
    public void addGroups(String... strings) {

    }

    @Override
    public void removeGroups(String... strings) {

    }

    @Override
    public boolean hasPermission(String s) {
        return false;
    }

    @Override
    public void setPermission(String s, boolean b) {

    }

    @Override
    public Collection<String> getPermissions() {
        return null;
    }

    @Override
    public InetSocketAddress getAddress() {
        return null;
    }

    @Override
    public void disconnect(String s) {

    }

    @Override
    public void disconnect(BaseComponent... baseComponents) {

    }

    @Override
    public void disconnect(BaseComponent baseComponent) {

    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public Unsafe unsafe() {
        return null;
    }
}
