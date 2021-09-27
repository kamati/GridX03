package com.example.gridx03.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gridx03.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentStatHourly extends Fragment {
    TextView powerConsumption, remainComsumption;
    private BarChart barChart;
    ArrayList<Entry> liveEntries;
    private LineChart liveChart;
    public ArrayList<String> xLabel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily, container, false);
        barChart = (BarChart) view.findViewById(R.id.daily_Bar_chart);
        liveEntries = new ArrayList<>();
        xLabel = new ArrayList<>();

        barChart.setDrawBarShadow(true);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(10);
        barChart.setPinchZoom(false);

        plotDailyChart(1, 2);
        return view;
    }

    public void plotDailyChart(float content, int count) {

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, 40f));
        barEntries.add(new BarEntry(1, 40f));
        barEntries.add(new BarEntry(2, 44f));
        barEntries.add(new BarEntry(3, 30f));
        barEntries.add(new BarEntry(4, 36f));
        barEntries.add(new BarEntry(5, 36f));
        barEntries.add(new BarEntry(6, 36f));


        BarDataSet barDataSet = new BarDataSet(barEntries, "Data Set1");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData data = new BarData(barDataSet);
        data.setBarWidth(0.1f);
        barChart.setData(data);

        String[] months = new String[]{"Mon", "Tues", "Wed", "Thus", "Fri", "Sat", "Sun"};
        XAxis axis = barChart.getXAxis();
        axis.setValueFormatter(new MyXAxisValueFormatter(months));
        axis.setPosition(XAxis.XAxisPosition.BOTTOM);

    }

    private class MyXAxisValueFormatter implements IAxisValueFormatter {
        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;

        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int) value];
        }
    }


}
