/*
 * Copyright (C) 2015 Dipu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package UtilityClass;

/**
 *
 * @author Dipu
 */
public final class TimeAndDate {

    public static String formatTimeSpan(long time)
    {
        //day, hour | min | sec
        long sec = time / 1000;
        long min = sec / 60;
        long hour = min / 60;
        long day = hour / 24;
        sec -= min * 60;
        min -= hour * 60;
        hour -= day * 24;

        String out = "";
        if (day > 0)
        {
            out += String.format("%d day", day);
            if (day > 1)
            {
                out += "s";
            }
        }
        if (hour > 0)
        {
            if (out.length() > 0)
            {
                out += " ";
            }
            out += String.format("%d hour", hour);
            if (hour > 1)
            {
                out += "s";
            }
        }
        if (day == 0)
        {
            if (min > 0)
            {
                if (out.length() > 0)
                {
                    out += " ";
                }
                out += String.format("%d minute", min);
                if (min > 1)
                {
                    out += "s";
                }
            }
            if (hour == 0 && sec > 0)
            {
                if (out.length() > 0)
                {
                    out += " ";
                }
                out += String.format("%d second", sec);
                if (sec > 1)
                {
                    out += "s";
                }
            }
        }

        return out;
    }
}
