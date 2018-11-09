package amarenkov.android.slidingdatepicker;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import amarenkov.android.snappyrecyclerview.SnappyAdapter;


class Adapter extends SnappyAdapter<Adapter.ViewHolder> {
    private ArrayList<Date> mDates = new ArrayList<>();
    private int mDefaultDay = -1;
    private Drawable mBgDateSelected;
    private Drawable mBgDateDefault;
    private int mColorDateSelected;
    private int mColorDateNotSelected;
    private int mDateItemSpacing;
    private String[] mWeekdaysArray;

    Adapter(Drawable bgDateSelected, Drawable bgDateDefault, int colorDateSelected,
            int colorDateNotSelected, int dateItemSpacing, String[] weekdaysArray) {
        mBgDateSelected = bgDateSelected;
        mBgDateDefault = bgDateDefault;
        mColorDateSelected = colorDateSelected;
        mColorDateNotSelected = colorDateNotSelected;
        mDateItemSpacing = dateItemSpacing;
        mWeekdaysArray = weekdaysArray;
    }

    void dispatchList(ArrayList<Date> dates, int defaultDay) {
        if(snapper.getSnappedPosition() > dates.size() -1) snapper.snapToPosition(dates.size() -1);
        mDates.clear();
        mDates.addAll(dates);
        mDefaultDay = defaultDay;
        notifyDataSetChanged();
    }

    int getCurrentDate() {
        return snapper.getSnappedPosition();
    }

    @Override
    public int getItemCount() {
        return mDates.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.sdp_item, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int position, boolean isAtTheCenter) {
        if(isAtTheCenter) viewHolder.select(mDates.get(position));
        else viewHolder.deselect(mDates.get(position));
    }

    @Override
    protected void onSnapedFromCenter(@NonNull ViewHolder viewHolder) {
        viewHolder.deselect();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate;
        private TextView tvWeekday;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.date);
            tvWeekday = itemView.findViewById(R.id.weekday);

            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            lp.leftMargin = mDateItemSpacing;
            lp.rightMargin = mDateItemSpacing;
            itemView.setLayoutParams(lp);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() == snapper.getSnappedPosition()) return;
                    snapper.smoothSnapToPosition(getAdapterPosition());
                }
            });
        }

        void select(Date date) {
            tvDate.setText(String.valueOf(date.getDate()));
            tvWeekday.setText(mWeekdaysArray[date.getWeekday() - 1]);
            tvDate.setBackground(mBgDateSelected);
            tvDate.setTextColor(mColorDateSelected);
        }

        void deselect(Date date) {
            tvDate.setText(String.valueOf(date.getDate()));
            tvWeekday.setText(mWeekdaysArray[date.getWeekday() - 1]);
            if(getAdapterPosition() == mDefaultDay) tvDate.setBackground(mBgDateDefault);
            else tvDate.setBackground(null);
            tvDate.setTextColor(mColorDateNotSelected);
        }

        void deselect() {
            if(getAdapterPosition() == mDefaultDay) tvDate.setBackground(mBgDateDefault);
            else tvDate.setBackground(null);
            tvDate.setTextColor(mColorDateNotSelected);
        }
    }
}
