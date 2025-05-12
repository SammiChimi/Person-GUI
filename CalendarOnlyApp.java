import com.github.lgooddatepicker.components.CalendarPanel;

import javax.swing.*;
import java.time.LocalDate;

public class CalendarOnlyApp {
    public static void main(String[] args) {
        // Create the main window
        JFrame frame = new JFrame("Standalone Calendar Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);

        // Create the calendar panel
        CalendarPanel calendarPanel = new CalendarPanel();

        // Optionally select today's date
        calendarPanel.setSelectedDate(LocalDate.now());

        // Add a listener to capture date changes
        // calendarPanel.addCalendarListener(event -> {
            // LocalDate selectedDate = event.getNewDate();
            // System.out.println("You selected: " + selectedDate);
            // });

            // Add calendar panel to the frame
            frame.add(calendarPanel);
            frame.setVisible(true);
        }
    }
