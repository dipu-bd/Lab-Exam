/*
 * Copyright (C) 2015 charlie.hubbard@gmail.com
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
package ServerApplication;

import org.jdesktop.swingx.calendar.SingleDaySelectionModel;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.DateFormatter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;
import java.awt.*;

/**
 * This is a DateTimePicker class downloaded from this thread-
 * http://stackoverflow.com/a/10820090/1583052
 */
public class DateTimePicker extends JXDatePicker
{

    private JSpinner timeSpinner;
    private JPanel timePanel;
    private DateFormat timeFormat;

    public DateTimePicker()
    {
        super();
        getMonthView().setSelectionModel(new SingleDaySelectionModel());        
    }

    public DateTimePicker(Date d)
    {
        this();
        setDate(d);
    }

    @Override
    public void commitEdit() throws ParseException
    {
        commitTime();
        super.commitEdit();
    }

    @Override
    public void cancelEdit()
    {
        super.cancelEdit();
        setTimeSpinners();
    }

    @Override
    public JPanel getLinkPanel()
    {
        super.getLinkPanel();
        if (timePanel == null) {
            timePanel = createTimePanel();
        }
        setTimeSpinners();
        return timePanel;
    }

    private JPanel createTimePanel()
    {
        JPanel newPanel = new JPanel();
        newPanel.setLayout(new FlowLayout());
        //newPanel.add(panelOriginal);

        SpinnerDateModel dateModel = new SpinnerDateModel();
        timeSpinner = new JSpinner(dateModel);
        if (timeFormat == null)
            timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        updateTextFieldFormat();
        newPanel.add(new JLabel("Time:"));
        newPanel.add(timeSpinner);
        newPanel.setBackground(Color.WHITE);
        return newPanel;
    }

    private void updateTextFieldFormat()
    {
        if (timeSpinner == null) return;
        JFormattedTextField tf = ((JSpinner.DefaultEditor) timeSpinner.getEditor()).getTextField();
        DefaultFormatterFactory factory = (DefaultFormatterFactory) tf.getFormatterFactory();
        DateFormatter formatter = (DateFormatter) factory.getDefaultFormatter();
        // Change the date format to only show the hours
        formatter.setFormat(timeFormat);
    }

    private void commitTime()
    {
        Date date = getDate();
        if (date != null) {
            Date time = (Date) timeSpinner.getValue();
            GregorianCalendar timeCalendar = new GregorianCalendar();
            timeCalendar.setTime(time);

            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Date newDate = calendar.getTime();
            setDate(newDate);
        }

    }

    private void setTimeSpinners()
    {
        Date date = getDate();
        if (date != null) {
            timeSpinner.setValue(date);
        }
    }

    public DateFormat getTimeFormat()
    {
        return timeFormat;
    }

    public void setTimeFormat(DateFormat timeFormat)
    {
        this.timeFormat = timeFormat;
        updateTextFieldFormat();
    } 
}
