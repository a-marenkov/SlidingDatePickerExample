package amarenkov.android.slidingdatepickerexample;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import amarenkov.androidx.slidingdatepicker.SlidingDatePicker;
import androidx.appcompat.app.AppCompatActivity;

public class ExampleActivity extends AppCompatActivity {

    private SlidingDatePicker sdp1;
    private SlidingDatePicker sdp2;
    private boolean isCallbackOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        sdp1 = findViewById(R.id.sdp1);
        sdp2 = findViewById(R.id.sdp2);

        if (savedInstanceState == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(2018, 9, 7);
            sdp2.setCalendar(calendar.getTimeInMillis());
        } else {
            sdp1.restoreInstanceState(savedInstanceState);
            sdp2.restoreInstanceState(savedInstanceState);
        }

        Button btn = findViewById(R.id.button);
        final SimpleDateFormat sdf = new SimpleDateFormat("EEE dd.MM.yyyy", Locale.getDefault());
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),
                        sdf.format(sdp1.getCalendar().getTimeInMillis()),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        Switch switch1 = findViewById(R.id.switch1);
        switch1.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCallbackOn = isChecked;
            }
        });

        sdp2.setCallback(new SlidingDatePicker.Callback() {
            @Override
            public void onDatePicked(Calendar calendar) {
                if (isCallbackOn) {
                    Toast.makeText(getApplicationContext(),
                            sdf.format(calendar.getTimeInMillis()),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        sdp1.saveInstanceState(outState);
        sdp2.saveInstanceState(outState);
    }
}
