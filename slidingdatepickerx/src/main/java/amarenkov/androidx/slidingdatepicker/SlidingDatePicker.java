package amarenkov.androidx.slidingdatepicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import amarenkov.androidx.snappyrecyclerview.SnappyAdapter;
import amarenkov.androidx.snappyrecyclerview.SnappyRecyclerView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SlidingDatePicker extends FrameLayout {
    // consts
    private final static String SDP_DEFAULT = "SDP_DEFAULT_";
    private final static String SDP_LAST = "SDP_LAST_";
    private final static int FORWARD = 1;
    private final static int BACKWARD = -1;
    private final static int NONE = 0;
    // views
    private SnappyRecyclerView rv;
    private TextView tvMonth;
    private TextView highlight;
    // other
    private Adapter mAdapter;
    private boolean mShowDefaultDate;
    private Calendar mCalendar = Calendar.getInstance();
    private int mDefaultYear = mCalendar.get(Calendar.YEAR);
    private int mDefaultMonth = mCalendar.get(Calendar.MONTH);
    private int mDefaultDay = mCalendar.get(Calendar.DAY_OF_MONTH);
    private String[] mMonthsArray = getResources().getStringArray(R.array.SDPMonthsArray);
    private String[] mWeekdaysArray = getResources().getStringArray(R.array.SDPWeekdaysArray);
    private float mMaxAlpha = 1f;
    private int spacing;
    private int itemSize;

    public SlidingDatePicker(@NonNull Context context) {
        this(context, null);
    }

    public SlidingDatePicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingDatePicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        View view = inflate(getContext(), R.layout.sdp_layout, this);
        final TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.SlidingDatePicker, 0, 0);

        tvMonth = view.findViewById(R.id.month);
        mMaxAlpha = tvMonth.getAlpha();

        ImageView left = view.findViewById(R.id.chevron_left);
        ImageView right = view.findViewById(R.id.chevron_right);
        if (typedArray.getBoolean(R.styleable.SlidingDatePicker_sdp_enable_chevrons, true)) {
            left.setColorFilter(tvMonth.getCurrentTextColor(), PorterDuff.Mode.SRC_IN);
            right.setColorFilter(tvMonth.getCurrentTextColor(), PorterDuff.Mode.SRC_IN);
            left.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    previousMonth();
                }
            });
            right.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    nextMonth();
                }
            });
        } else {
            left.setVisibility(GONE);
            right.setVisibility(GONE);
        }

        mShowDefaultDate = typedArray.getBoolean(R.styleable.SlidingDatePicker_sdp_enable_default_date, true);

        highlight = view.findViewById(R.id.highlight);
        spacing = (int) typedArray.getDimension(R.styleable.SlidingDatePicker_sdp_date_item_spacing, 0);
        final Drawable bgDateSelected = ContextCompat.getDrawable(getContext(), R.drawable.sdp_bg_date_selected);
        final Drawable bgDateDefault = ContextCompat.getDrawable(getContext(), R.drawable.sdp_bg_date_default);
        int colorDateSelected = typedArray.getColor(R.styleable.SlidingDatePicker_sdp_date_color_selected, ContextCompat.getColor(getContext(), android.R.color.white));
        mAdapter = new Adapter(bgDateSelected, bgDateDefault, colorDateSelected, highlight.getCurrentTextColor(), spacing, mWeekdaysArray);
        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rv.enableSnapListener(SnappyRecyclerView.Behavior.NOTIFY_ON_IDLE_AND_NO_POSITION);
        rv.setAdapter(mAdapter);

        if (typedArray.getBoolean(R.styleable.SlidingDatePicker_sdp_enable_highlight, true))
            highlight.setBackground(bgDateSelected);
        else highlight.setVisibility(INVISIBLE);
        highlight.post(new Runnable() {
            @Override
            public void run() {
                itemSize = highlight.getWidth();
                rv.resetCenteringPadding(
                        itemSize,
                        spacing,
                        mDefaultDay - 1);
                setMonth(-1, NONE);
            }
        });
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (itemSize == 0) highlight.post(new Runnable() {
            @Override
            public void run() {
                itemSize = highlight.getWidth();
                rv.resetCenteringPadding(
                        itemSize,
                        spacing,
                        mDefaultDay - 1);
            }
        });
    }

    private void setMonth(int date, int direction) {
        mCalendar.set(Calendar.DATE, 1);
        int count = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
        ArrayList dates = new ArrayList<Date>();
        count++;
        for (int i = 1; i < count; i++) {
            dates.add(new Date(i, dayOfWeek));
            if (dayOfWeek < 7) dayOfWeek++;
            else dayOfWeek = 1;
        }
        String newMonth = mMonthsArray[mCalendar.get(Calendar.MONTH)] + " " + String.valueOf(mCalendar.get(Calendar.YEAR));
        switch (direction) {
            case FORWARD:
                animateMonthForward(newMonth);
                break;
            case BACKWARD:
                animateMonthBackward(newMonth);
                break;
            default:
                tvMonth.setText(newMonth);
                break;
        }

        if (mShowDefaultDate && mDefaultMonth == mCalendar.get(Calendar.MONTH) && mDefaultYear == mCalendar.get(Calendar.YEAR))
            mAdapter.dispatchList(dates, mDefaultDay - 1);
        else mAdapter.dispatchList(dates, -1);
        if (date != -1) rv.smoothSnapToPosition(date);
    }

    private void animateMonthBackward(final String newMonth) {
        final float translationDelta = tvMonth.getWidth() / 2f;
        tvMonth.animate().alpha(0f).translationX(translationDelta)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        tvMonth.setText(newMonth);
                        tvMonth.setTranslationX(-translationDelta);
                        tvMonth.animate().alpha(mMaxAlpha).translationX(0f).start();
                    }
                }).start();
    }

    private void animateMonthForward(final String newMonth) {
        final float translationDelta = tvMonth.getWidth() / 2f;
        tvMonth.animate().alpha(0f).translationX(-translationDelta)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        tvMonth.setText(newMonth);
                        tvMonth.setTranslationX(translationDelta);
                        tvMonth.animate().alpha(mMaxAlpha).translationX(0f).start();
                    }
                }).start();
    }

    /**
     * smoothly goes back to current default date
     */
    public void backToDefaultDate() {
        int direction;
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        mCalendar.set(Calendar.YEAR, mDefaultYear);
        mCalendar.set(Calendar.MONTH, mDefaultMonth);
        if (year > mDefaultYear) direction = BACKWARD;
        else if (year < mDefaultYear) direction = FORWARD;
        else {
            if (month > mDefaultMonth) direction = BACKWARD;
            else if (month < mDefaultMonth) direction = FORWARD;
            else direction = NONE;
        }
        setMonth(mDefaultDay - 1, direction);
    }

    /**
     * smoothly goes back to current default date
     */
    public void backToDefaultDateForced() {
        mCalendar.set(Calendar.YEAR, mDefaultYear);
        mCalendar.set(Calendar.MONTH, mDefaultMonth);
        tvMonth.setText(mMonthsArray[mDefaultMonth] + " " + String.valueOf(mDefaultYear));
        rv.snapToPosition(mDefaultDay - 1);
    }

    /**
     * increments date picker's month
     */
    public void nextMonth() {
        mCalendar.add(Calendar.MONTH, 1);
        setMonth(-1, FORWARD);
    }

    /**
     * decrements date picker's month
     */
    public void previousMonth() {
        mCalendar.add(Calendar.MONTH, -1);
        setMonth(-1, BACKWARD);
    }

    /**
     * @return Calendar instance with selected date
     */
    public Calendar getCalendar() {
        mCalendar.set(Calendar.DAY_OF_MONTH, mAdapter.getCurrentDate() + 1);
        return mCalendar;
    }

    /**
     * sets date picker's date
     *
     * @param time time in millis
     */
    public void setCalendar(Long time) {
        mCalendar.setTimeInMillis(time);
        mDefaultYear = mCalendar.get(Calendar.YEAR);
        mDefaultMonth = mCalendar.get(Calendar.MONTH);
        mDefaultDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        if (rv.isPaddingApplied()) setMonth(mDefaultDay - 1, NONE);
    }

    /**
     * saves date picker's state
     * has to be called on Acivity's onSaveInstanceState(Bundle outState)
     *
     * @param bundle Bundle outState
     */
    public void saveInstanceState(Bundle bundle) {
        if (bundle == null) return;
        mCalendar.set(Calendar.DAY_OF_MONTH, mAdapter.getCurrentDate() + 1);
        bundle.putLong(SDP_LAST + getId(), mCalendar.getTimeInMillis());
        mCalendar.set(mDefaultYear, mDefaultMonth, mDefaultDay);
        bundle.putLong(SDP_DEFAULT + getId(), mCalendar.getTimeInMillis());
    }

    /**
     * restores date picker's state
     *
     * @param bundle Bundle savedInstanceState
     */
    public void restoreInstanceState(Bundle bundle) {
        if (bundle == null) return;
        mCalendar.setTimeInMillis(bundle.getLong(SDP_DEFAULT + getId()));
        mDefaultYear = mCalendar.get(Calendar.YEAR);
        mDefaultMonth = mCalendar.get(Calendar.MONTH);
        mDefaultDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        mCalendar.setTimeInMillis(bundle.getLong(SDP_LAST + getId()));
    }

    public void setCallback(final Callback callback) {
        mAdapter.setCallback(new SnappyAdapter.Callback() {
            @Override
            public void onItemCentered(int position) {
                mCalendar.set(Calendar.DAY_OF_MONTH, (position + 1));
                callback.onDatePicked(mCalendar);
            }
        });
    }

    public interface Callback {
        /**
         * Called when new date have been centered
         *
         * @param calendar Calendar instance with selected date
         */
        void onDatePicked(Calendar calendar);
    }
}