package fr.l3ak1.bestprice.controller;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import fr.l3ak1.bestprice.R;
import fr.l3ak1.bestprice.model.Localisation;
import fr.l3ak1.bestprice.model.Prix;
import fr.l3ak1.bestprice.model.Produit;

/**
 * Activity to see price evolution in a store
 */
public class PriceEvolutionActivity extends AppCompatActivity {

    private Produit produit;
    private ArrayList<Prix> prixList;
    private LineChart lineChart;
    private Localisation userStore;
    private ArrayList<Entry> pricesEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_evolution);

        produit = (Produit) getIntent().getSerializableExtra("produit");
        userStore = (Localisation) getIntent().getSerializableExtra("location");
        if (userStore == null)
            getLocation();
        if (userStore != null)
        {
            if (userStore.getNom() == null)
                getStore();
            getPrices();
        }
        lineChart = (LineChart) findViewById(R.id.line_chart);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (this.userStore != null)
        {
            if (this.userStore.getNom() == null)
                getStore();
            getPrices();
            if (prixList != null)
                setChartParams();
        }
    }

    /**
     * Configure the chart for display
     */
    private void setChartParams()
    {
        // valeurs des axes
        XAxis xAxis = lineChart.getXAxis();
        YAxis yAxisLeft = lineChart.getAxisLeft();
        YAxis yAxisRight = lineChart.getAxisRight();

        xAxis.setValueFormatter(formatter2);
        xAxis.setLabelCount(pricesEntries.size(), true);
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

        LineDataSet lineDataSet = new LineDataSet(pricesEntries, "Prix");
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

    ActivityResultLauncher<Intent> startForLocationResult =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>()
                    {
                        @Override
                        public void onActivityResult(ActivityResult result)
                        {
                            if (result.getData() == null)
                                Toast.makeText(PriceEvolutionActivity.this, "error trying to get" +
                                        " location", Toast.LENGTH_LONG).show();
                            else
                                userStore =
                                        (Localisation) result.getData().getSerializableExtra(
                                                "LOCALISATION");
                        }
                    });

    ActivityResultLauncher<Intent> startForStoreResult =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>()
                    {
                        @Override
                        public void onActivityResult(ActivityResult result)
                        {
                            if (result.getData() == null)
                                Toast.makeText(PriceEvolutionActivity.this, "error trying to get" +
                                        " location", Toast.LENGTH_LONG).show();
                            else
                                userStore =
                                        (Localisation) result.getData().getSerializableExtra(
                                                "LOCALISATION");
                        }
                    });

    /**
     * Get the location of the user
     */
    private void getLocation()
    {
        try {
            Intent localisationIntent = new Intent(this, LocalisationActivity.class);
            localisationIntent.putExtra("getStore", false);
            startForLocationResult.launch(localisationIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the store of the user
     */
    private void getStore()
    {
        try {
            Intent localisationIntent = new Intent(this, LocalisationActivity.class);
            localisationIntent.putExtra("localisation", userStore);
            localisationIntent.putExtra("radius", 5000);
            localisationIntent.putExtra("getStore", true);
            startForStoreResult.launch(localisationIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all prices of the product in the store
     */
    private void getPrices() {
        try {
            CompletableFuture<List<Prix>> f = Prix.getAllPrixLoc(produit.getCodeBarres(),
                    userStore.getId());
            this.prixList = (ArrayList<Prix>) f.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.reverse(this.prixList);
        if (prixList == null)
            return;
        pricesEntries = new ArrayList<>();
        int i = 0;
        for (Prix p : prixList) {
            pricesEntries.add(new Entry(i, (float) p.getPrix()));
            i++;
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
