package com.saroty.ter.adapter.cellcat;

import com.saroty.ter.adapter.Adapter;
import com.saroty.ter.adapter.exception.AdapterException;
import com.saroty.ter.adapter.exception.AdapterParsingException;
import com.saroty.ter.schedule.Course;
import com.saroty.ter.schedule.CourseDay;
import com.saroty.ter.schedule.CourseWeek;
import com.saroty.ter.schedule.Schedule;
import com.saroty.ter.schedule.time.DayOfWeek;
import com.saroty.ter.schedule.time.LocalTime;
import com.saroty.ter.schedule.time.LocalTimeInterval;

import org.apache.http.HttpResponse;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;

/**
 * Created by Arthur on 09/03/2015.
 */
public class CelcatAdapter extends Adapter
{
    public CelcatAdapter(URL url, boolean trusted)
    {
        super(url, trusted);
    }


    @Override
    public Schedule adapt() throws AdapterException
    {
        HttpResponse response = loadUrl();
        System.out.println(response.getEntity().getContentType());
        if (!response.getEntity().getContentType().getValue().endsWith("/xml"))
            throw new AdapterParsingException(url);

        XmlPullParserFactory factory = null;
        try
        {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            String encoding = "UTF-8";
            if (response.getEntity().getContentEncoding() != null)
                encoding = response.getEntity().getContentEncoding().getValue();

            xpp.setInput(response.getEntity().getContent(), encoding);

            int eventType = xpp.getEventType();

            Schedule table = new Schedule();

            CourseWeek curWeek = null;

            Course curCourse = null;
            LocalTime curCourseStart = null;
            LocalTime curCourseEnd = null;
            DayOfWeek curCourseDay = null;

            String text = "";

            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                if (eventType == XmlPullParser.START_TAG)
                {
                    if (xpp.getName().equalsIgnoreCase("span"))//TODO: Switch ?
                    {
                        curWeek = new CourseWeek();
                        table.addWeek(Integer.parseInt(xpp.getAttributeValue(null, "rawix")), curWeek);
                    } else if (xpp.getName().equalsIgnoreCase("event"))
                    {
                        curCourse = new Course();
                    }
                    if (curWeek != null && xpp.getName().equalsIgnoreCase("day"))
                    {
                        curWeek.addDay(DayOfWeek.getById(Integer.parseInt(xpp.getAttributeValue(null, "id"))), new CourseDay());
                    }

                } else if (eventType == XmlPullParser.END_TAG)
                {
                    if (xpp.getName().equalsIgnoreCase("span"))
                        curWeek = null;
                    else if (xpp.getName().equalsIgnoreCase("event"))
                    {
                        curCourse = null;
                        curCourseStart = null;
                        curCourseEnd = null;
                        curCourseDay = null;
                    } else if (curCourse != null)
                    {
                        if (xpp.getName().equalsIgnoreCase("day"))
                            curCourseDay = DayOfWeek.getById(Integer.parseInt(text));
                        else if (xpp.getName().equalsIgnoreCase("module"))
                            curCourse.setTitle(text);
                        else if (xpp.getName().equalsIgnoreCase("starttime"))
                            curCourseStart = new LocalTime(text, ":");
                        else if (xpp.getName().equalsIgnoreCase("endtime"))
                            curCourseEnd = new LocalTime(text, ":");
                        else if (xpp.getName().equalsIgnoreCase("rawweeks") && curCourseDay != null && curCourseEnd != null && curCourseStart != null)
                        {
                            //TODO : Je suis pas sûr a 100%, mais je pense qu'il peut y avoir plusieurs "Y"
                            for (int index = text.indexOf("Y"); index >= 0; index = text.indexOf("Y", index + 1))
                            {
                                table.getWeekByWeekNumber(index + 1).getDay(curCourseDay).addCourse(new LocalTimeInterval(curCourseStart, curCourseEnd), curCourse);
                            }
                        }
                    }

                } else if (eventType == XmlPullParser.TEXT)
                {
                    text = xpp.getText();
                }
                eventType = xpp.next();
            }
            System.out.println(table.weekCount());
            return table;
        } catch (Exception e)
        {
            throw new AdapterParsingException(url);
        }
    }
}