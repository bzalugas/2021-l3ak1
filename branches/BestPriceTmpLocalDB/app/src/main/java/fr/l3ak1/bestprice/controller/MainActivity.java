package fr.l3ak1.bestprice.controller;

import androidx.appcompat.app.AppCompatActivity;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import fr.l3ak1.bestprice.R;
import fr.l3ak1.bestprice.model.DatabaseSQLite;
import fr.l3ak1.bestprice.model.Produit;
import fr.l3ak1.bestprice.model.ProduitAdapter;

public class MainActivity extends AppCompatActivity {

	private Button buttonScan;
	private ListView listView;
	private List<Produit> produits;
	private ProduitAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		buttonScan = findViewById(R.id.main_button_scan);
		listView = findViewById(R.id.main_listview_bdd);
		buttonScan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent scanIntent = new Intent(MainActivity.this, ProduitInfoActivity.class);
				startActivity(scanIntent);
			}
		});
	}

	protected void onResume()
	{
		super.onResume();
		DatabaseSQLite db = new DatabaseSQLite(MainActivity.this);
		showProduits(db);
	}

	private void showProduits(DatabaseSQLite db)
	{
		produits = db.getAllProduits();
		adapter = new ProduitAdapter(this, produits);
		listView.setAdapter(adapter);
	}

}
