package fr.l3ak1.bestprice.controller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import fr.l3ak1.bestprice.R;
import fr.l3ak1.bestprice.model.DatabaseSQLite;
import fr.l3ak1.bestprice.model.Produit;
import fr.l3ak1.bestprice.model.ProduitAdapter;

public class MainActivity extends AppCompatActivity {

	private Button buttonScan;
	private Button buttonClear;
	private ListView listView;
	private List<Produit> produits;
	private ProduitAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		buttonScan = findViewById(R.id.main_button_scan);
		listView = findViewById(R.id.main_listview_bdd);
		buttonClear = findViewById(R.id.main_button_clear);

		buttonScan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent produitInfoIntent = new Intent(MainActivity.this, ProduitInfoActivity.class);
				produitInfoIntent.putExtra("SCAN_NEEDED", true);
				startActivity(produitInfoIntent);
			}
		});

		buttonClear.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("Suppression de l'historique local");
				builder.setMessage("Etes-vous sûr de vouloir supprimer votre historique local ?");
				builder.setPositiveButton("Effacer", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialogInterface, int i)
					{
						clearCache();
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
		});

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Intent produitInfoIntent = new Intent(MainActivity.this, ProduitInfoActivity.class);
				produitInfoIntent.putExtra("PRODUIT", (Produit) parent.getItemAtPosition(position));
				startActivity(produitInfoIntent);
			}
		});
	}

	protected void onResume()
	{
		super.onResume();
		DatabaseSQLite db = new DatabaseSQLite(MainActivity.this);
		showProduits(db);
	}

	private void clearCache()
	{
		DatabaseSQLite db = new DatabaseSQLite(MainActivity.this);
		boolean success = db.deleteAll();
		if (success)
			Toast.makeText(this, "Historique local supprimé !", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(this, "Erreur lors de la tentative de suppression de l'historique local.", Toast.LENGTH_SHORT).show();
		this.onResume();
	}

	private void showProduits(DatabaseSQLite db)
	{
		produits = db.getAllProduits();
		adapter = new ProduitAdapter(this, produits);
		listView.setAdapter(adapter);
	}

}
