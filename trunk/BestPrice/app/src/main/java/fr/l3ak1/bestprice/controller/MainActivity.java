package fr.l3ak1.bestprice.controller;

import androidx.appcompat.app.AppCompatActivity;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;

import fr.l3ak1.bestprice.R;

public class MainActivity extends AppCompatActivity {

	private Button buttonHist;
	private Button buttonScan;
	private Button buttonFiltre;
	private Button test;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		buttonScan = findViewById(R.id.main_button_scan);
		buttonScan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent scanIntent = new Intent(MainActivity.this, ProduitInfoActivity.class);
				startActivity(scanIntent);
			}
		});

		//buttonHist = findViewById(R.id.main_button_hist);
		//buttonHist.setEnabled(false);
		//buttonHist.setOnClickListener(new View.OnClickListener()
		{
		//	@Override
		//	public void onClick(View view)
			{
//				Intent infosIntent = new Intent(MainActivity.this, HistoricActivity.class);
//				startActivity(infosIntent);

			}
		}

//		buttonFiltre = findViewById(R.id.main_button_filtre);
//		buttonFiltre.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				Toast.makeText(MainActivity.this, "Tri par distance", Toast.LENGTH_LONG).show();
//			}
//		});
	}



}
