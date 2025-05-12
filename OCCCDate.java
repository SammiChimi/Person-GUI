import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class OCCCDate implements Serializable {
    private final int dayOfMonth;
    private final int monthOfYear;
    private final int year;
    private final GregorianCalendar gc;
    private boolean dateFormat;
    private boolean dateStyle;
    private boolean dateDayName;

    public static final boolean FORMAT_US = true;
    public static final boolean FORMAT_EURO = false;
    public static final boolean STYLE_NUMBERS = true;
    public static final boolean STYLE_NAMES = false;
    public static final boolean SHOW_DAY_NAME = true;
    public static final boolean HIDE_DAY_NAME = false;

    public OCCCDate() {
        this(new GregorianCalendar());
    }

    public OCCCDate(int dayOfMonth, int monthOfYear, int year) throws InvalidOCCCDateException {
        this(new GregorianCalendar(year, monthOfYear - 1, dayOfMonth));
        if (isInvalidDate(dayOfMonth, monthOfYear, year)) {
            throw(new InvalidOCCCDateException());
        }
    }

    public OCCCDate(GregorianCalendar gc) {
        this.gc = gc;
        this.dayOfMonth = gc.get(Calendar.DATE);
        this.monthOfYear = gc.get(Calendar.MONTH) + 1;
        this.year = gc.get(Calendar.YEAR);
        dateFormat = FORMAT_US;
        dateStyle = STYLE_NUMBERS;
        dateDayName = SHOW_DAY_NAME;
    }

    public OCCCDate(OCCCDate d) {
        this(d.gc);
        dateFormat = d.dateFormat;
        dateStyle = d.dateStyle;
        dateDayName = d.dateDayName;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public String getDayName() {
        return gc.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
    }

    public int getMonthOfYear() {
        return monthOfYear;
    }

    public String getMonthName() {
        return gc.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
    }

    public int getYear() {
        return year;
    }

    public void setDateFormat(boolean df) {
        this.dateFormat = df;
    }

    public void setStyleFormat(boolean sf) {
        this.dateStyle = sf;
    }

    public void setDayName(boolean nf) {
        this.dateDayName = nf;
    }

    public int getDifferenceInYears() {
        return new OCCCDate().getDifferenceInYears(this);
    }

    public int getDifferenceInYears(OCCCDate d) {
        return d.year - this.year;
    }

    public boolean equals(OCCCDate dob) {
        return this.dayOfMonth == dob.dayOfMonth &&
                this.monthOfYear == dob.monthOfYear &&
                this.year == dob.year;
    }

    private boolean isInvalidDate(int day, int month, int year) {
        return getDayOfMonth() != day || getMonthOfYear() != month || getYear() != year;
    }

    @Override
    public String toString() {
        String fDay = String.format("%02d", dayOfMonth);
        String fMonth = String.format("%02d", monthOfYear);

        String date = "";
        if (dateDayName == SHOW_DAY_NAME) {
            date += getDayName() + ", ";
        }
        if (dateFormat == FORMAT_US && dateStyle == STYLE_NUMBERS) {
            date += fMonth + "/" + fDay + "/" + year;
        } else if (dateStyle == STYLE_NUMBERS) {
            date += fDay + "/" + fMonth + "/" + year;
        } else if (dateFormat == FORMAT_US) {
            date += getMonthName() + " " + fDay + ", " + year;
        } else {
            date += fDay + " " + getMonthName() + ", " + year;
        }

        return date;
    }
}
