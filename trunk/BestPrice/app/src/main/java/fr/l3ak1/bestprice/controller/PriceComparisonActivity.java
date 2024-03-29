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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import fr.l3ak1.bestprice.R;
import fr.l3ak1.bestprice.model.DatabaseSQLite;
import fr.l3ak1.bestprice.model.Localisation;
import fr.l3ak1.bestprice.model.PriceComparisonAdapter;
import fr.l3ak1.bestprice.model.Prix;
import fr.l3ak1.bestprice.model.Produit;

/**
 * Activity to compare prices
 */
public class PriceComparisonActivity extends AppCompatActivity {

    private List<Prix> prixList;
    private Produit produit;
    private Localisation user_localisation;
    private List<Localisation> localisationsPrix;
    private ListView lvComparison;
    private double newPrice;
    private Button btnChangePrice;
    private Button btnSortPrice;
    private Button btnSortDistance;
    private PriceComparisonAdapter prixAdapter;

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

        lvComparison = findViewById(R.id.comparison_listview_enseigne);
        btnChangePrice = findViewById(R.id.comparison_button_change_price);
        btnSortDistance = findViewById(R.id.comparison_btn_tri_dist);
        btnSortPrice = findViewById(R.id.comparison_btn_tri_price);

        produit = (Produit)getIntent().getSerializableExtra("produit");

        getLocation();
        if (user_localisation != null)
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

        btnSortPrice.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sortByPrice();
                if (prixAdapter != null)
                    prixAdapter.notifyDataSetChanged();
            }
        });

        btnSortDistance.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sortByDistance();
                if (prixAdapter != null)
                    prixAdapter.notifyDataSetChanged();
            }
        });

        lvComparison.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent evolutionIntent = new Intent(PriceComparisonActivity.this,
                        PriceEvolutionActivity.class);
                evolutionIntent.putExtra("produit", produit);
                Localisation store = null;
                Prix prix = (Prix)parent.getItemAtPosition(position);
                try
                {
                    CompletableFuture<Localisation> f =
                            Localisation.getLocalisationById(prix.getLocalisation_id());
                    store = f.get();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                evolutionIntent.putExtra("location", store);
                startActivity(evolutionIntent);
            }
        });

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (this.user_localisation != null)
        {
            if (this.user_localisation.getNom() != null)
                btnChangePrice.setText("Le prix a changé");
            getPrices();
            displayPrices();
        }
    }

    /**
     * Launch the LocalisationActivity to get latitude & longitude of the user
     */
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

    /**
     * Launch LocalisationActivity to get the store the user is in
     */
    private void getStore()
    {
        try {
            Intent localisationIntent = new Intent(this, LocalisationActivity.class);
            localisationIntent.putExtra("localisation", user_localisation);
            localisationIntent.putExtra("radius", 500);
            localisationIntent.putExtra("getStore", true);
            startForResult.launch(localisationIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Add new price to online DB and local DB
     */
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
            if (this.prixList == null)
                this.prixList = new ArrayList<>();
            this.prixList.add(tmpPrix);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Ask the user for the new price to add and create it
     */
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

    /**
     * Display prices to the ListView
     */
    private void displayPrices()
    {
        if (this.prixList != null && this.localisationsPrix != null)
        {
            prixAdapter = new PriceComparisonAdapter(this, this.prixList,
                    this.localisationsPrix);
            lvComparison.setAdapter(prixAdapter);
        }
        else
            Toast.makeText(this, "Chargement en cours", Toast.LENGTH_SHORT).show();
    }

    /**
     * Get in DB all prices around the location
     */
    private void getPrices()
    {
        try{
            CompletableFuture<List<Prix>> fPrices =
                    Prix.getNearbyPrices(this.produit.getCodeBarres(),
                    user_localisation.getLatitude(), user_localisation.getLongitude(), 5000);
            CompletableFuture<List<Localisation>> fLoc =
                    user_localisation.getNearbyLocations(5000);
            this.prixList = fPrices.get();
            this.localisationsPrix = fLoc.get();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Sort prices by ascending distance
     */
    private void sortByDistance()
    {
        Collections.sort(this.localisationsPrix);
        int indexPrix;
        Prix tmp;
        for (int i = 0; i < localisationsPrix.size(); i++)
        {
            indexPrix = getPrixIndex(localisationsPrix.get(i));
            if (indexPrix != -1)
            {
                tmp = prixList.get(indexPrix);
                if (i < prixList.size())
                {
                    prixList.set(indexPrix, prixList.get(i));
                    prixList.set(i, tmp);
                }
            }
        }
    }

    /**
     * Sort prices by price
     */
    private void sortByPrice()
    {
        Collections.sort(this.prixList);
        int indexLoc;
        Localisation tmp;
        for (int i = 0; i < prixList.size(); i++)
        {
            indexLoc = getLocalisationIndex(prixList.get(i));
            if (indexLoc != -1)
            {
                tmp = localisationsPrix.get(indexLoc);
                if (i < localisationsPrix.size())
                {
                    localisationsPrix.set(indexLoc, localisationsPrix.get(i));
                    localisationsPrix.set(i, tmp);
                }
            }
        }
    }

    /**
     * Get index of the price corresponding to a location in the ArrayList
     * @param loc the corresponding location
     * @return the index of the price in the ArrayList or -1 if price is not found
     */
    private int getPrixIndex(Localisation loc)
    {
        for (int i = 0; i < prixList.size(); i++)
            if (prixList.get(i).getLocalisation_id() == loc.getId())
                return i;
        return -1;
    }

    /**
     * Get index of the location corresponding to a price in the ArrayList
     * @param p the corresponding price
     * @return the index of the location in the ArrayList or -1 if location is not found
     */
    private int getLocalisationIndex(Prix p)
    {
        for (int i = 0; i < localisationsPrix.size(); i++)
            if (localisationsPrix.get(i).getId() == p.getLocalisation_id())
                return i;
        return -1;
    }
}