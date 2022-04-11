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

public class MainActivity extends AppCompatActivity {

	private Button buttonScan;
	private ListView listView;
	private ArrayAdapter produitsAdapter;

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
		DatabaseSQLite db = new DatabaseSQLite(MainActivity.this);
		showProduits(db);
//		buttonAdd.setOnClickListener(new View.OnClickListener()
//		{
//			@Override
//			public void onClick(View view)
//			{
//				Produit produit = new Produit(editCodebarres.getText().toString(),
//						editMarque.getText().toString(),
//						editNom.getText().toString(), editContenu.getText().toString(),
//						editImage.getText().toString());
//				DatabaseSQLite dbSql = new DatabaseSQLite(MainActivity.this);
//				boolean success = dbSql.addProduit(produit);
//				if (success)
//					showProduits(dbSql);
//			}
//		});

//		buttonShow.setOnClickListener(new View.OnClickListener()
//		{
//			@Override
//			public void onClick(View view)
//			{
//				DatabaseSQLite dbSql = new DatabaseSQLite(MainActivity.this);
//				showProduits(dbSql);
//			}
//		});

	}

	private void showProduits(DatabaseSQLite db)
	{
		produitsAdapter = new ArrayAdapter<Produit>(MainActivity.this,
				android.R.layout.simple_list_item_1, db.getAllProduits());
		listView.setAdapter(produitsAdapter);
	}

}
