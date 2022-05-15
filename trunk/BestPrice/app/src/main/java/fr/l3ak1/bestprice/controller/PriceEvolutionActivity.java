package fr.l3ak1.bestprice.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import fr.l3ak1.bestprice.R;
import fr.l3ak1.bestprice.model.Prix;
import fr.l3ak1.bestprice.model.Produit;

public class PriceEvolutionActivity extends AppCompatActivity {

    private Produit produit;
    private ArrayList<Prix> prix;
    private LineChart lineChart;
    private Button retour;
    private String[] dates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_evolution);

        produit = (Produit) getIntent().getSerializableExtra("produit");
        getPrices();
        /*retour.findViewById(R.id.button2);
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent retourIntent = new Intent(PriceEvolutionActivity.this, MainActivity.class);
                startActivity(retourIntent);
            }
        });*/

        lineChart = (LineChart) findViewById(R.id.line_chart);

        // entrées du graph
        ArrayList<Entry> Prices = new ArrayList<>();
        int i = 0;
        for (Prix p : prix) {
            Prices.add(new Entry(i, (float) p.getPrix()));
            i++;
        }


        // valeurs des axes
        XAxis xAxis = lineChart.getXAxis();
        YAxis yAxisLeft = lineChart.getAxisLeft();
        YAxis yAxisRight = lineChart.getAxisRight();

        xAxis.setValueFormatter(formatter2);
        xAxis.setLabelCount(Prices.size(), true);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawLabels(true);
        xAxis.setSpaceMax(1f);


        yAxisRight.setEnabled(false);
        yAxisLeft.setEnabled(false);


        // description ( en bas à droite )

        Description description = lineChart.getDescription();
        description.setEnabled(false);


        // couleurs et formes des lignes / textes dans le graph

        LineDataSet lineDataSet = new LineDataSet(Prices, "Prix");
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueFormatter(formatter);

        lineDataSet.setLineWidth(5f);
        lineDataSet.setColor(Color.RED);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setCircleColor(Color.RED);
        lineDataSet.setCircleRadius(7f);
        lineDataSet.setCircleHoleRadius(2f);
        lineDataSet.setValueTextSize(16f);


        lineChart.setData(new LineData(lineDataSet));
        //lineChart.setBackgroundColor(Color.BLUE);

        // bordures du graph
        lineChart.setDrawBorders(false);
        lineChart.setBorderColor(Color.RED);
        lineChart.setBorderWidth(2);


        lineChart.setDrawMarkers(false);
        lineChart.setDrawGridBackground(false);
        lineChart.animateX(3000, Easing.Linear);
        lineChart.invalidate();

    }

    private void getPrices() {
        try {
            CompletableFuture<ArrayList<Prix>> f = Prix.getAllPrix(produit.getCodeBarres());
            this.prix = f.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    ValueFormatter formatter = new ValueFormatter() {

        @Override
        public String getFormattedValue(float value) {
            return value + "€";
        }

    };

    ValueFormatter formatter2 = new ValueFormatter() {
        @Override
        public String getFormattedValue(float value){

            return "";
        }
    };

}
