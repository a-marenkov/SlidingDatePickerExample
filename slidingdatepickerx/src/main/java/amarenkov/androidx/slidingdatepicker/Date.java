package amarenkov.androidx.slidingdatepicker;

class Date {
    private int date;
    private int weekday;

    Date(int date, int weekday) {
        this.date = date;
        this.weekday = weekday;
    }

    int getDate() {
        return date;
    }

    int getWeekday() {
        return weekday;
    }
}
