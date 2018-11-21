# SlidingDatePickerExample

## 1. How to add to your project

add to your dependencies
```
implementation 'amarenkov.android:slidingdatepicker:1.0.0-alpha1'
```
or
```
implementation 'amarenkov.androidx:slidingdatepicker:1.0.0-alpha1'
```

## 2. How to use

- Add SlidingDatePicker to your layout.

```
<amarenkov.androidx.slidingdatepicker.SlidingDatePicker
android:id="@+id/sdp1"
android:layout_width="match_parent"
android:layout_height="wrap_content" />
```
  
```android:layout_width``` should always match screen width since SlidingDatePicker designed to work from edge to edge.

```android:layout_height``` should always be "wrap_content", the actual height of the SlidingDatePicker will depend on set styles.

- Find view in your Activity or Fragment
	
```java
SlidingDatePicker sdp1 = findViewById(R.id.sdp1);
```

- By deafault SlidingDatePicker sets today's date, if you want to set other date pass the time in millis to SlidingDatePicker's setCalendar(Long time):

```java
Calendar calendar = Calendar.getInstance();
calendar.set(2018, 9, 7);
sdp1.setCalendar(calendar.getTimeInMillis());
```

- If you want to get currently selected date use SlidingDatePicker's getCalendar():
	
```java
Calendar calendar = sdp1.getCalendar();
```

- If you want to listen to selected dates changes use SlidingDatePicker's setCallback(SlidingDatePicker.Callback callback):

```java
sdp1.setCallback(new SlidingDatePicker.Callback() {
    	@Override
    	public void onDatePicked(Calendar calendar) {
        String date = String.valueOf(calendar.get(Calendar.YEAR))
               + "."
               + String.valueOf(calendar.get(Calendar.MONTH) + 1)
               + "."
               + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        Toast.makeText(getApplicationContext(), date, Toast.LENGTH_SHORT).show();
    	}
});
```

- If you want SlidingDatePicker to "survive" screen orientation changes use SlidingDatePicker's saveInstanceState(Bundle outState) and restoreInstanceState(Bundle savedInstanceState):

```java
sdp1.saveInstanceState(outState);
sdp1.restoreInstanceState(savedInstanceState);
 ```
 - If you want SlidingDatePicker to return to default date, no need to call setCalendar(Long timeInMillis), you can use one of the following methods backToDefaultDate() (goes back smoothly) or backToDefaultDateForced() (goes back instantly)
 ```java
 sdp1.backToDefaultDate()
 ```
 
## 3. Styling&Localization

- You can style SlidingDatePicker using following LayoutParams:

	```app:sdp_date_item_spacing``` - set margin that will be applied to dates as leftMargin and rightMargin (by default is 0dp);
	
  ```app:sdp_date_color_selected``` - set textColor that will be applied to selected date (by default color is white);
	
  ```app:sdp_enable_highlight``` - set to false if you want to hide highlight that is shown while dates dragging (by default set as true);
	
  ```app:sdp_enable_default_date``` - set to false if you don't want default date to be outlined (by default set as true);
	
  ```app:sdp_enable_chevrons``` - set to false if you want to hide navigation chevrons (by default set as true);
  
  ```android:paddingStart``` & ```android:paddingLeft``` - set if you want to give extra margin to left chevron (by default chevron has margin of 8dp);
  
  ```android:paddingEnd``` & ```android:paddingRight``` - set if you want to give extra margin to right chevron (by default chevron has margin of 8dp).
  
`Note:` if you added left and right padding to SlidingDatePicker, you also need to add following LayoutParams to it:

```
android:clipToPadding="false"
android:clipChildren="false"
```

`Note:` default date is the date that is set with method setCalendar(Long timeInMillis) or today's date if the method is never called.


- You can change SlidingDatePicker's text appearances overriding following styles (note that SDPMonth's textColor will be used as chevrons color filter):

```
    <style name="SDPMonth">
        <item name="fontFamily">sans-serif-medium</item>
        <item name="android:fontFamily">sans-serif-medium</item>
        <item name="android:textStyle">bold</item>
        <item name="android:textAllCaps">false</item>
        <item name="android:textSize">18sp</item>
        <item name="android:padding">4dp</item>
        <item name="android:alpha">0.8</item>
        <item name="android:gravity">center</item>
        <item name="android:textColor">@color/colorAccent</item>
        <item name="android:letterSpacing">0.0125</item>
    </style>
    
    <style name="SDPWeekday">
        <item name="fontFamily">sans-serif-medium</item>
        <item name="android:fontFamily">sans-serif-medium</item>
        <item name="android:textStyle">normal</item>
        <item name="android:textSize">10sp</item>
        <item name="android:textAllCaps">true</item>
        <item name="android:ems">3</item>
        <item name="android:alpha">0.6</item>
        <item name="android:textColor">@color/colorSDPText</item>
        <item name="android:gravity">center</item>
        <item name="android:letterSpacing">0.0125</item>
    </style>

    <style name="SDPDate">
        <item name="fontFamily">sans-serif-medium</item>
        <item name="android:fontFamily">sans-serif-medium</item>
        <item name="android:textStyle">bold</item>
        <item name="android:textSize">14sp</item>
        <item name="android:ems">3</item>
        <item name="android:gravity">center</item>
        <item name="android:paddingTop">4dp</item>
        <item name="android:alpha">0.8</item>
        <item name="android:textColor">@color/colorSDPText</item>
        <item name="android:paddingBottom">4dp</item>
        <item name="android:letterSpacing">0.0125</item>
    </style>
```

`Note:` if you just need to change color of date and weekday, you can override `@color/colorSDPText`, instead of overriding styles.

- You can change SlidingDatePicker's chevrons by overriding following res files `@drawable/sdp_previous` and `@drawable/sdp_next`

- You can change SlidingDatePicker's centered date background by overriding following res file `@drawable/sdp_bg_date_selected`

- You can change SlidingDatePicker's default date background by overriding following res file `@drawable/sdp_bg_date_default`

- You can change wording and add support for different locales (by default only English is supported) by overriding/adding following resources:

```
    <string-array name="SDPMonthsArray">
        <item>January</item>
        <item>February</item>
        <item>March</item>
        <item>April</item>
        <item>May</item>
        <item>June</item>
        <item>July</item>
        <item>August</item>
        <item>September</item>
        <item>October</item>
        <item>November</item>
        <item>December</item>
    </string-array>
    
    <string-array name="SDPWeekdaysArray">
        <item>SUN</item>
        <item>MON</item>
        <item>TUE</item>
        <item>WED</item>
        <item>THU</item>
        <item>FRI</item>
        <item>SAT</item>
    </string-array>
```
