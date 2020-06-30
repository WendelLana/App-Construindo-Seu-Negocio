package com.example.redelogin;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyGridAdapter extends ArrayAdapter {
    private List<Date> dates;
    private Calendar currentDate;
    private List<Events> events;
    private LayoutInflater inflater;

    MyGridAdapter(@NonNull Context context, List<Date> dates, Calendar currentDate, List<Events> events) {
        super(context, R.layout.calendar_single_cell);

        this.dates = dates;
        this.currentDate = currentDate;
        this.events = events;
        inflater = LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Date monthDate = dates.get( position );
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime( monthDate );
        int DayNo = dateCalendar.get( Calendar.DAY_OF_MONTH );
        int displayMonth = dateCalendar.get( Calendar.MONTH ) + 1;
        int displayYear = dateCalendar.get( Calendar.YEAR );
        int currentMonth = currentDate.get( Calendar.MONTH ) + 1;
        int currentYear = currentDate.get( Calendar.YEAR );

        View view = convertView;
        if (view == null) { view = inflater.inflate(R.layout.calendar_single_cell, parent, false); }

        if (displayMonth == currentMonth && displayYear == currentYear) {
            view.setBackgroundColor( getContext().getResources().getColor( R.color.calendarRow ));
        } else {
            view.setBackgroundColor( Color.parseColor("#cccccc") );
        }

        TextView Day_Number = view.findViewById( R.id.calendar_day );
        TextView EventNumber  = view.findViewById( R.id.event_id );
        Day_Number.setText( String.valueOf( DayNo ) );
        Calendar eventCalendar = Calendar.getInstance();
        //ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < events.size(); i++){
            eventCalendar.setTime(ConvertStringToDate( events.get(i).getData()) );
            if (DayNo == eventCalendar.get( Calendar.DAY_OF_MONTH ) && displayMonth == eventCalendar.get( Calendar.MONTH ) + 1 && displayYear == eventCalendar.get( Calendar.YEAR ) ) {
                //arrayList.add(events.get(i).getNome());
                //String numberEvents = String.valueOf(arrayList.size());
                //EventNumber.setText( numberEvents );
                view.setBackgroundColor( Color.parseColor( "#E60F31") );
                //EventNumber.setBackgroundColor( Color.parseColor( "#E60F31") );
            }
        }

        return view;
    }

    private Date ConvertStringToDate(String eventDate){
        SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd", Locale.ENGLISH );
        Date date = null;
        try {
            date = format.parse( eventDate );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return dates.indexOf( item );
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return dates.get( position );
    }
}
