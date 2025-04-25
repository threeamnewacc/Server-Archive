package us.zonix.core.misc.command;

import com.google.gson.JsonObject;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.core.CorePlugin;
import us.zonix.core.api.request.PlayerRequest;
import us.zonix.core.profile.Profile;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class RegisterCommand extends BaseCommand {

    @Command(name = "register", rank = Rank.DEFAULT, description = "Register your account with our website.", requiresPlayer = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        if (command.getArgs().length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /register <email>");
            return;
        }

        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile == null) {
            player.sendMessage(ChatColor.RED + "Your profile could not be found.");
            return;
        }

        if (profile.isRegistered()) {
            player.sendMessage(ChatColor.RED + "You are already registered.");
            return;
        }

        if (!(this.isValidEmailAddress(command.getArgs()[0]))) {
            player.sendMessage(ChatColor.RED + "Please specify a valid email address.");
            return;
        }

        long current = System.currentTimeMillis();
        long last = profile.getLastRegister();

        if (last != 0 && current < last + 120000) {
            long timeLeft = (last - current);

            player.sendMessage(ChatColor.RED + "You must wait " + RegisterCommand.readableTime(timeLeft) + " before attempting to register again.");
        }
        else {
            player.sendMessage(ChatColor.GRAY + "(Attempting to register...)");

            String confirmationId = RandomStringUtils.randomAlphanumeric(16);

            profile.setEmailAddress(command.getArgs()[0]);
            profile.setConfirmationId(confirmationId);
            profile.setLastRegister(System.currentTimeMillis());

            PlayerRequest.UpdateRegisterRequest request = new PlayerRequest.UpdateRegisterRequest(player.getUniqueId(), profile.getEmailAddress(), profile.getConfirmationId(), profile.isRegistered());

            CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(request, data -> {
                JsonObject object = data.getAsJsonObject();

                if (object.has("response")) {
                    String response = object.get("response").getAsString();

                    if (response.equalsIgnoreCase("success")) {
                        RegisterCommand.this.sendEmail(profile.getEmailAddress(), confirmationId);
                        player.sendMessage(ChatColor.YELLOW + "Please check your email at " + ChatColor.GOLD + profile.getEmailAddress() + ChatColor.YELLOW + " to complete your registration.");
                    }
                    else if (response.equalsIgnoreCase("already-registered")) {
                        player.sendMessage(ChatColor.RED + "You have already registered. If you need assistance completing your registration, join our TeamSpeak.");
                    }
                    else if (response.equalsIgnoreCase("player-not-found")) {
                        player.sendMessage(ChatColor.RED + "Could not process your registration. Try re-logging.");
                    }
                    else {
                        player.sendMessage(ChatColor.RED + "Could not process your registration. Try again in a little bit.");
                    }
                }
            });
        }
    }

    private static String readableTime(long time) {
        short second = 1000;
        int minute = 60 * second;
        int hour = 60 * minute;
        int day = 24 * hour;
        long ms = time;

        StringBuilder text = new StringBuilder("");

        if (time > (long) day) {
            text.append(time / (long) day).append(" days ");
            ms = time % (long) day;
        }

        if (ms > (long) hour) {
            text.append(ms / (long) hour).append(" hours ");
            ms %= (long) hour;
        }

        if (ms > (long) minute) {
            text.append(ms / (long) minute).append(" minutes ");
            ms %= (long) minute;
        }

        if (ms > (long) second) {
            text.append(ms / (long) second).append(" seconds ");
        }

        return text.toString();
    }

    private boolean isValidEmailAddress(String email) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    private void sendEmail(String email, String confirmationId) {
        new BukkitRunnable() {
            @Override
            public void run() {
                // TODO: add config options for properties
                Properties props = new Properties();
                props.put("mail.smtp.host", "mail.privateemail.com");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.socketFactory.port", "465");
                props.put("mail.smtp.socketFactory.class",
                        "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.port", "465");

                Session session = Session.getDefaultInstance(props,
                        new Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication("site@zonix.us", "@EQaFEnApr2b");
                            }
                        }
                );

                try {
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress("site@zonix.us", "Zonix Network"));
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
                    message.setSubject("Zonix - Complete Your Registration");
                    message.setText(
                            "Thanks for registering your account on the Zonix Network.\n"
                                    + "To complete your account registration, please click the following link:\n"
                                    + "http://www.zonix.us/confirm/" + confirmationId + "\n\n"
                                    + "Thanks,\n"
                                    + "Zonix Network");

                    Transport.send(message);

                } catch (MessagingException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(CorePlugin.getInstance());
    }

}