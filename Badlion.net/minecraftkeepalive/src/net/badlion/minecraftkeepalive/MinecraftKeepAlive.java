package net.badlion.minecraftkeepalive;

import org.bukkit.util.NumberConversions;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

public class MinecraftKeepAlive {

    public static ArrayList<MinecraftServer> minecraftServers = new ArrayList<MinecraftServer>();
    public static ArrayList<String> emails = new ArrayList<String>();

	public static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public static class Location {

        private double x;
        private double y;
        private double z;

        public Location(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getZ() {
            return z;
        }

        public void setZ(double z) {
            this.z = z;
        }

    }

    public static double getXFromRadians(double d1, double d2) {
        return Math.round(d1 * Math.sin(d2)) + 0.5D;
    }

    public static double getZFromRadians(double d1, double d2) {
        return Math.round(d1 * Math.cos(d2)) + 0.5D;
    }

    private static boolean isLocationValid(Location location, ArrayList<Location> locations, Double d) {
        for (Location loc : locations) {
            if (Math.sqrt(NumberConversions.square(loc.getX() - location.getX()) + NumberConversions.square(loc.getZ() - location.getZ())) < d) {
                return false;
            }
        }
        return true;
    }

    public static ArrayList<Location> randomCircularScatter(int rad, int pCount) {
        Random randy = new Random();

        ArrayList<Location> locations = new ArrayList<>();

        int radius = rad - 25;
        //double minDistance = (radius * 2 - 100D) / pCount;
        double minDistance = 20D; // Solves issues with small player counts
        //World world = Bukkit.getWorld("uhcworld");

        for (int i = 0; i < pCount; i++) {
            Location scatterPoint = new Location(0.0D, 0.0D, 0.0D);
            int j = 0;
            for (int k = 0; k < 100; k++) {
                double d1 = randy.nextDouble() * 6.283185307179586D;
                double d2 = radius * Math.sqrt(randy.nextDouble());
                double d3 = getXFromRadians(d2, d1);
                double d4 = getZFromRadians(d2, d1);
                d3 = Math.round(d3) + 0.5D;
                d4 = Math.round(d4) + 0.5D;
                scatterPoint.setX(d3);
                scatterPoint.setZ(d4);
                //scatterPoint.setY(world.getHighestBlockYAt(scatterPoint) + 5);
                if (MinecraftKeepAlive.isLocationValid(scatterPoint, locations, minDistance)) { //&& isLocationBlockValid(scatterPoint)) {
                    j = 1;
                    break;
                }
            }
            if (j == 0) {
                //System.out.println("MaxAttemptsReachedException"); // Didn't feel like making an exception
            }
            locations.add(scatterPoint);
        }
        return locations;
    }

    public static ArrayList<Location> randomSquareScatter(int rad, int pCount) {
        //System.out.println("pcount " + pCount);
        Random randy = new Random();

        ArrayList<Location> locations = new ArrayList<>();

        int radius = rad - 25;
        //double minDistance = (radius * 2 - 100D) / pCount;
        double minDistance = 20D; // Solves issues with small player counts
        //World world = Bukkit.getWorld("uhcworld");

        for (int i = 0; i < pCount; i++) {
            Location scatterPoint = new Location(0.0D, 0.0D, 0.0D);
            int j = 0;
            for (int k = 0; k < 100; k++) {
                double d1 = randy.nextDouble() * radius * 2.0D - radius;
                double d2 = randy.nextDouble() * radius * 2.0D - radius;
                d1 = Math.round(d1) + 0.5D;
                d2 = Math.round(d2) + 0.5D;
                scatterPoint.setX(d1);
                scatterPoint.setZ(d2);
                //scatterPoint.setY(world.getHighestBlockYAt(scatterPoint) + 5);
                if (isLocationValid(scatterPoint, locations, minDistance)) { //&& isLocationBlockValid(scatterPoint)) {
                    j = 1;
                    break;
                }
            }
            if (j == 0) {
                //System.out.println("MaxAttemptsReachedException"); // Didn't feel like making an exception
            }
            locations.add(scatterPoint);
        }
        return locations;
    }

	public static void main(String[] args) throws Exception {
        Random rand = new Random();

        int counter = 0;

        while (true) {
            counter++;

            long s = System.currentTimeMillis();
            MinecraftKeepAlive.randomSquareScatter(rand.nextInt(2900) + 101, rand.nextInt(290) + 11);

            long diff = System.currentTimeMillis() - s;

            if (diff > 9) {
                System.out.println(counter + ": " + diff + "ms");
            }
        }


        // Check if config file exists
        /*File file = new File("mckeepalive.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error creating yml");
                System.exit(0);
            }
        }

        // Load config file
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<String> servers = config.getStringList("servers");
        List<String> emails = config.getStringList("emails");

        // Terminate program if config file is empty
        if (servers.isEmpty() || emails.isEmpty()) {
            System.out.println("Configuration file is empty, exiting...");
            System.exit(0);
        } else {
            for (String server : servers) {
                String[] serverInfo = server.split(":");
                MinecraftKeepAlive.minecraftServers.add(new MinecraftServer(serverInfo[0], serverInfo[1], Integer.valueOf(serverInfo[2])));
            }

            for (String email : emails) {
                MinecraftKeepAlive.emails.add(email);
            }
        }

        while (true) {
            for (MinecraftServer ms : MinecraftKeepAlive.minecraftServers) {
                if (!ms.fetchData()) {
                    if (ms.getContinuousPingsFailed() == 5) {
                        ms.setContinuousPingsFailed(0);
                        System.out.println("[" + MinecraftKeepAlive.dateFormat.format(Calendar.getInstance().getTime()) + "] Did not receive successful data for " + ms.getServerName() + " 5 times, sending email.");
                        for (String email : MinecraftKeepAlive.emails) {
                            MinecraftKeepAlive.sendEmail(email, ms);
                        }
                    } else {
                        System.out.println("[" + MinecraftKeepAlive.dateFormat.format(Calendar.getInstance().getTime()) + "] Did not receive successful data for " + ms.getServerName());
                        ms.setContinuousPingsFailed(ms.getContinuousPingsFailed() + 1);
                    }
                } else {
                    System.out.println("[" + MinecraftKeepAlive.dateFormat.format(Calendar.getInstance().getTime()) + "] Received successful data for " + ms.getServerName());
                    ms.setContinuousPingsFailed(0);
                }
            }

            // Rare case an exception is thrown...
            try {
                Thread.sleep(60000);
            } catch (Exception e) {
                e.printStackTrace();

                for (String email : MinecraftKeepAlive.emails) {
                    MinecraftKeepAlive.sendEmail(email, "KeepAliveScript Thread Sleep Exception");
                }

                System.exit(0);
            }
        }*/
    }

    public static void sendEmail(String email, MinecraftServer ms) {
        String msgBody = ms.getServerName() + " is offline!";
        MinecraftKeepAlive.sendEmail(email, msgBody);
    }

	public static void sendEmail(String email, String msgBody) {
        Properties properties = new Properties();
        Session session = Session.getDefaultInstance(properties);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("no-reply@badlion.net", "Badlion"));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(email, "Badlion Keep Alive"));
            msg.setSubject("Badlion Keep Alive - " + msgBody);
            msg.setText(msgBody);
            Transport.send(msg);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
