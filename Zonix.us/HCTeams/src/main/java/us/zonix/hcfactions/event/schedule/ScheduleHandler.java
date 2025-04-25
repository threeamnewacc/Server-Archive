package us.zonix.hcfactions.event.schedule;

import org.apache.commons.lang.StringUtils;
import us.zonix.hcfactions.event.EventManager;
import org.bukkit.Bukkit;
import us.zonix.hcfactions.files.ConfigFile;
import us.zonix.hcfactions.event.EventManager;
import us.zonix.hcfactions.files.ConfigFile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class ScheduleHandler {

    public static List<Schedule> schedules = new ArrayList<Schedule>();

    public static void setSchedules(EventManager manager, ConfigFile config) {

        schedules.clear();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(config.getString("schedule-timezone")));
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

        long calendarMillis = calendar.getTimeInMillis();

        if(config.getConfiguration().contains("schedule-monday")) {
            readSchedule(manager, config, "monday", 0, calendarMillis);
            Bukkit.getLogger().info("[HCF] (KOTH): Loaded schedule for monday.");
        }

        if(config.getConfiguration().contains("schedule-tuesday")) {
            readSchedule(manager, config, "tuesday", 1, calendarMillis);
            Bukkit.getLogger().info("[HCF] (KOTH): Loaded schedule for tuesday.");
        }

        if(config.getConfiguration().contains("schedule-wednesday")) {
            readSchedule(manager, config, "wednesday", 2, calendarMillis);
            Bukkit.getLogger().info("[HCF] (KOTH): Loaded schedule for wednesday.");
        }

        if(config.getConfiguration().contains("schedule-thursday")) {
            readSchedule(manager, config, "thursday", 3, calendarMillis);
            Bukkit.getLogger().info("[HCF] (KOTH): Loaded schedule for thursday.");
        }

        if(config.getConfiguration().contains("schedule-friday")) {
            readSchedule(manager, config, "friday", 4, calendarMillis);
            Bukkit.getLogger().info("[HCF] (KOTH): Loaded schedule for friday.");
        }

        if(config.getConfiguration().contains("schedule-saturday")) {
            readSchedule(manager, config, "saturday", 5, calendarMillis);
            Bukkit.getLogger().info("[HCF] (KOTH): Loaded schedule for saturday.");
        }

        if(config.getConfiguration().contains("schedule-sunday")) {
            readSchedule(manager, config, "sunday", 6, calendarMillis);
            Bukkit.getLogger().info("[HCF] (KOTH): Loaded schedule for sunday.");
        }
    }

    public static void readSchedule(EventManager manager, ConfigFile config, String day, int dayInId, long calendarMillis) {


        try {

            String[] events = config.getString("schedule-" + day).split("#");

            for(int i = 0; i < events.length; i++) {
                String[] event = events[i].split("/");
                String time = event[0];
                String area = event[1];
                String runTime = event[2];

                int hours = 0, minute = 0;

                if (time.contains(":")) {

                    String[] build = time.split(":");

                    hours = Integer.parseInt(build[0]);
                    minute = Integer.parseInt(build[1].replaceAll("[a-zA-Z]", ""));

                } else {

                    hours = Integer.parseInt(time.replaceAll("[a-zA-Z]", ""));
                }

                if (time.endsWith("PM")) {
                    hours += 12;
                }

                long eventTime = calendarMillis + (dayInId * 24 * 60 * 60 * 1000) + (hours * 60 * 60 * 1000) + (minute * 60 * 1000);

                if (eventTime < System.currentTimeMillis()) {
                    eventTime += 7 * 24 * 60 * 60 * 1000;
                }

                schedules.add(new Schedule(eventTime, runTime, area, StringUtils.capitalize(day) + " " + time));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Schedule getNextEvent() {
        Schedule ret = null;
        for(Schedule sched : schedules) {
            if(ret == null) {
                ret = sched;
            } else if(sched.getEventTime() < ret.getEventTime()) {
                ret = sched;
            }
        }
        return ret;
    }


    public static Schedule getNextEvent(String arenaName) {

        Schedule schedule = null;

        for(Schedule sched : schedules) {

            if(schedule == null) {
                schedule = sched;
            }

            else if(sched.getEventTime() < schedule.getEventTime()) {
                schedule = sched;
            }

        }

        return schedule;
    }

    public static void runSchedule(){
        for(Schedule schedule : schedules){
            schedule.runEvent();
        }
    }
}
