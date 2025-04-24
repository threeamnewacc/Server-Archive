// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.util;

import java.beans.ConstructorProperties;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.BufferedInputStream;
import java.net.URL;
import club.mineman.core.api.request.RequestCallback;
import club.mineman.core.api.APIMessage;
import org.json.simple.JSONObject;
import club.mineman.core.api.request.AbstractCallback;
import club.mineman.core.api.impl.VersionCheckRequest;
import club.mineman.core.util.finalutil.FileUtil;
import java.io.File;
import club.mineman.core.util.finalutil.CC;
import club.mineman.core.CorePlugin;

public class PluginUpdater
{
    private final CorePlugin plugin;
    
    public void update(final String pluginName, final String pluginVersion) {
        this.plugin.getServer().getConsoleSender().sendMessage(CC.GREEN + "[Updater] Checking for updates in " + pluginName + "...");
        this.plugin.getServer().getConsoleSender().sendMessage(CC.GREEN + "[Updater] The current version of " + pluginName + " is " + pluginVersion + ".");
        final File pluginFile = new File("plugins/" + pluginName + ".jar");
        try {
            final String checksum = FileUtil.getMD5Checksum(pluginFile.getAbsolutePath());
            this.plugin.getRequestManager().sendRequest(new VersionCheckRequest(pluginName), new AbstractCallback("Error checking version for " + pluginName) {
                @Override
                public void callback(final JSONObject data) {
                    final String response = (String)data.get((Object)"response");
                    if (response.equals("success")) {
                        final String latestChecksum = (String)data.get((Object)"checksum");
                        if (!checksum.equals(latestChecksum)) {
                            final String updateVersion = (String)data.get((Object)"latest-version");
                            final String downloadLink = (String)data.get((Object)"download-link");
                            PluginUpdater.this.downloadUpdate(pluginName, downloadLink, updateVersion);
                        }
                    }
                }
            });
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private void downloadUpdate(final String pluginName, final String downloadLink, final String updateVersion) {
        try {
            this.plugin.getServer().getConsoleSender().sendMessage(CC.GREEN + "[Updater] The latest version of " + pluginName + " is " + updateVersion + ".");
            this.plugin.getServer().getConsoleSender().sendMessage(CC.GREEN + "[Updater] Updating " + pluginName + " to v" + updateVersion + ".");
            final URL link = new URL(downloadLink);
            final InputStream in = new BufferedInputStream(link.openStream());
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = in.read(buf))) {
                out.write(buf, 0, n);
            }
            out.close();
            in.close();
            final byte[] response = out.toByteArray();
            final File pluginFile = new File("plugins/" + pluginName + ".jar");
            final FileOutputStream fos = new FileOutputStream(pluginFile);
            fos.write(response);
            fos.close();
            this.plugin.getServer().getConsoleSender().sendMessage(CC.GREEN + "[Updater] " + pluginName + " has been successfully updated to v" + updateVersion + ".");
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    @ConstructorProperties({ "plugin" })
    public PluginUpdater(final CorePlugin plugin) {
        this.plugin = plugin;
    }
}
