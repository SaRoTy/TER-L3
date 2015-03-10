package com.saroty.ter.schedule.time;

/**
 * Created by Arthur on 09/03/2015.
 */
public enum DayOfWeek
{
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

    public static DayOfWeek getById(int id)
    {
        if (id < DayOfWeek.values().length)
            return DayOfWeek.values()[id];
        return MONDAY;
    }
}

