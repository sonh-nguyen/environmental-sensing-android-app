package com.example.demoiot;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    // For display sensor values
    ListView listView;
    ArrayList<String> sensorList = new ArrayList<>();
    Double mHumidity, mPressure, mTemp;
    DatabaseReference mRef;

    private LineChart lineChart;
    private ArrayList<Entry> values1, values2, values3;
    private Random random = new Random();

    // Dummy
    int changeCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // For display list of sensor values
        final ArrayAdapter<String> sensorArray = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, sensorList);
        listView = (ListView) findViewById(R.id.listviewsensor);
        listView.setAdapter(sensorArray);
        // Get instance of Firebase
        mRef = FirebaseDatabase.getInstance().getReference();
        // For display graph
        lineChart = findViewById(R.id.line_chart);

        values1 = new ArrayList<>();
        values2 = new ArrayList<>();
        values3 = new ArrayList<>();

        // Dummy TimerTask to simulate real-time data updates (Replace this with actual data listener)
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    ++changeCount;
                    mHumidity = getRandomValue();
                    mPressure = getRandomValue();
                    mTemp     = getRandomValue();
                    values1.add(new Entry(changeCount, mHumidity != null ? mHumidity.floatValue() : 0f));
                    values2.add(new Entry(changeCount, mPressure != null ? mPressure.floatValue() : 0f));
                    values3.add(new Entry(changeCount, mTemp != null ? mTemp.floatValue() : 0f));

                    updateChart();
                });
            }
        }, 0, 1000); // Update every second (1000 milliseconds)

        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                HashMap<String, Double> hashMap = (HashMap<String, Double>) snapshot.getValue();
                System.out.println("[mson] onChildAdded: " + hashMap);
                mHumidity = hashMap.get("hum");
                mPressure = hashMap.get("pres");
                mTemp = hashMap.get("temp");
                sensorList.add("Humidity: " + String.valueOf(mHumidity));
                sensorList.add("Pressure: " + String.valueOf(mPressure));
                sensorList.add("Temperature: " + String.valueOf(mTemp));
                sensorArray.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                HashMap<String, Double> hashMap = (HashMap<String, Double>) snapshot.getValue();
                System.out.println("[mson] onChildAdded: " + hashMap);
                mHumidity = hashMap.get("hum");
                mPressure =hashMap.get("pres");
                mTemp = hashMap.get("temp");
                sensorList.add("Humidity: " + String.valueOf(mHumidity));
                sensorList.add("Pressure: " + String.valueOf(mPressure));
                sensorList.add("Temperature: " + String.valueOf(mTemp));
//                updateDataToGraph();
                sensorArray.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
//            public void updateDataToGraph() {
//                runOnUiThread(() -> {
//                    values1.add(new Entry(System.currentTimeMillis(), mHumidity != null ? mHumidity.floatValue() : 0f));
//                    values2.add(new Entry(System.currentTimeMillis(), mPressure != null ? mPressure.floatValue() : 0f));
//                    values3.add(new Entry(System.currentTimeMillis(), mTemp != null ? mTemp.floatValue() : 0f));
//
//                    updateChart();
//                });
//            }
        });
    }
    private double getRandomValue() {
        return random.nextDouble() * 100; // Change this logic based on your actual data
    }

    private void updateChart() {
        // clear to draw again
        if (10 <= changeCount) {
            values1.clear(); // Clear data for value 1
            values2.clear(); // Clear data for value 2
            values3.clear(); // Clear data for value 3
            changeCount = 0;
        }
        // Set up X-axis description
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Set position of X-axis labels
        xAxis.setGranularity(1f); // Set granularity to 1 to show integer values
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value); // Format the X-axis label as integer
            }
        });
        xAxis.setLabelCount(1); // Set the number of labels to display on the X-axis
        xAxis.setAxisMinimum(0); // Set the minimum value for the X-axis
        xAxis.setAxisMaximum(10); // Set the maximum value for the X-axis

        // Set the description for X-axis
        lineChart.getDescription().setText("Count");
        // Dummy
        LineDataSet dataSet1 = new LineDataSet(values1, "Humidity");
        LineDataSet dataSet2 = new LineDataSet(values2, "Pressure");
        LineDataSet dataSet3 = new LineDataSet(values3, "Temperature");

        // Define colors for the lines
        int colorHumidity = Color.BLUE;  // Change color according to your preference
        int colorPressure = Color.RED;   // Change color according to your preference
        int colorTemperature = Color.GREEN; // Change color according to your preference

        // Set colors for each dataset
        dataSet1.setColor(colorHumidity);
        dataSet2.setColor(colorPressure);
        dataSet3.setColor(colorTemperature);

        // Set other configurations as needed, like line thickness, etc.
        dataSet1.setLineWidth(2f);
        dataSet2.setLineWidth(2f);
        dataSet3.setLineWidth(2f);
        // Add data to chart
        LineData lineData = new LineData(dataSet1, dataSet2, dataSet3);
        lineChart.setData(lineData);
        lineChart.invalidate();

        System.out.println("[mson] drawingOnGraph: "
                + " " + mHumidity + " " +  mPressure + " " + mTemp);
    }
}
