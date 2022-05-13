package fr.l3ak1.bestprice.controller;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import fr.l3ak1.bestprice.R;
import fr.l3ak1.bestprice.model.DatabaseSQLite;
import fr.l3ak1.bestprice.model.Localisation;
import fr.l3ak1.bestprice.model.Prix;
import fr.l3ak1.bestprice.model.Produit;

public class PriceComparisonActivity extends AppCompatActivity {

    private ArrayList<Prix> prixList;
    private Produit produit;
    private Localisation user_localisation;
    private ArrayList<Localisation> localisationsPrix;
    private ListView listViewComparison;
    private double newPrice;
    private Button btnChangePrice;

    ActivityResultLauncher<Intent> startForResult =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>()
                    {
                        @Override
                        public void onActivityResult(ActivityResult result)
                        {
                            if (result.getData() == null)
                                Toast.makeText(PriceComparisonActivity.this, "error trying to get" +
                                        " location", Toast.LENGTH_LONG).show();
                            else
                                user_localisation =
                                    (Localisation) result.getData().getSerializableExtra(
                                            "LOCALISATION");
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_comparison);

        listViewComparison = findViewById(R.id.comparison_listview_enseigne);
        btnChangePrice = findViewById(R.id.comparison_button_change_price);

        produit = (Produit)getIntent().getSerializableExtra("produit");

        getLocation();
        getPrices();

        btnChangePrice.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (user_localisation.getId() == 0)
                    getStore();
                askNewPrice();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        displayPrices();
    }

    private void getLocation()
    {
        try {
            Intent localisationIntent = new Intent(this, LocalisationActivity.class);
            localisationIntent.putExtra("getStore", false);
            startForResult.launch(localisationIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getStore()
    {
        try {
            Intent localisationIntent = new Intent(this, LocalisationActivity.class);
            localisationIntent.putExtra("getStore", true);
            startForResult.launch(localisationIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createPrix()
    {
        boolean success;
        DatabaseSQLite db = new DatabaseSQLite(PriceComparisonActivity.this);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Prix tmpPrix = new Prix(produit.getCodeBarres(), newPrice, format.format(new Date()),
                user_localisation.getId(), user_localisation.getNom());
        try {
            CompletableFuture<Boolean> f = tmpPrix.addPrix();
            success = f.get();
            if (!success)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(PriceComparisonActivity.this);
                        builder.setTitle("Error");
                        builder.setMessage("An error has occured while trying to add price to this " +
                                "product.");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                dialogInterface.cancel();
                            }
                        });
                    }
                });
            }
            db.addPrix(tmpPrix);
            this.prixList.add(tmpPrix);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void askNewPrice()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Veuillez entrer le prix du produit en euros");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);
        builder.setPositiveButton("Entrer", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                newPrice = Double.parseDouble(input.getText().toString());
                createPrix();
                onResume();
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    private void displayPrices()
    {
        ArrayAdapter<Prix> prixAdapter;
        prixAdapter = new ArrayAdapter<Prix>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, prixList);
        listViewComparison.setAdapter(prixAdapter);
    }

    private void getPrices()
    {
        /*Modifier pour ne prendre que les derniers prix des 5 magasins les plus proches*/
        try {
            CompletableFuture<ArrayList<Prix>> f = Prix.getAllPrix(produit.getCodeBarres());
            this.prixList = f.get();
            if (!this.prixList.isEmpty())
                for (Prix p : prixList)
                    p.setLocalisation_nom(getLocalisationById(p.getLocalisation_id()));
        } catch (Exception e){
            e.printStackTrace();
        }
        if (this.prixList.isEmpty())
            askNewPrice();
    }

    private String getLocalisationById(long id)
    {
        String nom = "";
        if (id != 0)
        {
            try{
                CompletableFuture<Localisation> f = Localisation.getLocalisationById(id);
                Localisation tmpLoc = f.get();
                if (tmpLoc != null)
                {
                    nom = tmpLoc.getNom();
//                    this.localisationsPrix.add(tmpLoc);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return nom;
        }
        return null;
    }
}