package com.example.demoiot;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    // For display sensor values
    Double mHumidity, mPressure, mTemp;
    DatabaseReference mRef;

    private LineChart lineChart, lineChartPres, lineChartTemp;
    private ArrayList<Entry> values1, values2, values3;
    private Random random = new Random();
    int changeCount = -1, countLimit = 9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // For display list of sensor values
        TextView hmiHumid = findViewById(R.id.humidValue);
        TextView hmiPres  = findViewById(R.id.presValue);
        TextView hmiTemp = findViewById(R.id.tempValue);
        // Get instance of Firebase
        mRef = FirebaseDatabase.getInstance().getReference();
        // For display graph
        lineChart     = findViewById(R.id.line_chart);
        lineChartPres = findViewById(R.id.line_chart_pres);
        lineChartTemp = findViewById(R.id.line_chart_temp);

        values1 = new ArrayList<>();
        values2 = new ArrayList<>();
        values3 = new ArrayList<>();

        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                initChart(lineChart, "Count");
                initChart(lineChartPres, "Count");
                initChart(lineChartTemp, "Count");
                HashMap<String, Double> hashMap = (HashMap<String, Double>) snapshot.getValue();
                System.out.println("[mson] onChildAdded: " + hashMap);
                mHumidity = hashMap.get("hum");
                mPressure = hashMap.get("pres");
                mTemp = hashMap.get("temp");
                hmiHumid.setText(String.valueOf(mHumidity));
                hmiPres.setText(String.valueOf(mPressure));
                hmiTemp.setText(String.valueOf(mTemp));
                updateDataToGraph();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                HashMap<String, Double> hashMap = (HashMap<String, Double>) snapshot.getValue();
                System.out.println("[mson] onChildChanged: " + hashMap);
                mHumidity = hashMap.get("hum");
                mPressure =hashMap.get("pres");
                mTemp = hashMap.get("temp");
                hmiHumid.setText(String.valueOf(mHumidity));
                hmiPres.setText(String.valueOf(mPressure));
                hmiTemp.setText(String.valueOf(mTemp));
                updateDataToGraph();
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
            public void updateDataToGraph() {
                runOnUiThread(() -> {
                    values1.add(new Entry(changeCount, mHumidity != null ? mHumidity.floatValue() : 0f));
                    values2.add(new Entry(changeCount, mPressure != null ? mPressure.floatValue() : 0f));
                    values3.add(new Entry(changeCount, mTemp != null ? mTemp.floatValue() : 0f));
                    updateChart(lineChart, Color.BLUE, "Humidity", values1);
                    updateChart(lineChartPres, Color.RED, "Pressure", values2);
                    updateChart(lineChartTemp, Color.GREEN, "Temperature", values3);
                    ++changeCount;
                });
            }
        });
    }

    private void initChart(LineChart lineChart_, String description) {
        // Set up X-axis description
        XAxis xAxis = lineChart_.getXAxis();
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
        lineChart.getDescription().setText(description);
    }

    private void updateChart(LineChart lineChart_, int color, String label, ArrayList<Entry> values_) {
        // clear to draw again
        if (10 <= changeCount) {
            values1.clear(); // Clear data
            values2.clear();
            values3.clear();
//            lineChart.clear();
//            lineChartPres.clear();
//            lineChartTemp.clear();
            changeCount = -1;
            return;
        }

        // Set the description for X-axis
        lineChart_.getDescription().setText("Count");
        // Dummy
        LineDataSet dataSet = new LineDataSet(values_, label);

        // Set colors for each dataset
        dataSet.setColor(color);

        // Set other configurations as needed, like line thickness, etc.
        dataSet.setLineWidth(2f);
        // Add data to chart
        LineData lineData = new LineData(dataSet);
        lineChart_.setData(lineData);
        lineChart_.invalidate();
    }
}
