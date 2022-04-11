package fr.l3ak1.bestprice.controller;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import fr.l3ak1.bestprice.R;
import fr.l3ak1.bestprice.model.Prix;
import fr.l3ak1.bestprice.model.Produit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProduitInfoActivity extends AppCompatActivity
{
	private Produit produit;
	private TextView editTextCodeBarres;
	private TextView textViewMarque;
	private TextView textViewNom;
	private TextView textViewContenu;
//	private EditText editTextimagePath;
	private ImageView imageView;
	private Button btnComparisonPrice;
	private Button btnEvolutionPrice;

	@Override
	protected void onCreate(Bundle savedInstance)
	{
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_produit_info);


//		editTextCodeBarres = findViewById(R.id.scan_edittext_codebarres);
		textViewMarque = findViewById(R.id.scan_edittext_marque);
		textViewNom = findViewById(R.id.scan_edittext_nom);
		textViewContenu = findViewById(R.id.scan_edittext_contenu);
//		editTextimagePath = findViewById(R.id.scan_edittext_image_path);
		imageView = findViewById(R.id.scan_imageview);
//		addButton = findViewById(R.id.scan_button_ajouter);
//		getButton = findViewById(R.id.scan_button_voir);
		btnComparisonPrice = findViewById(R.id.scan_button_comparaison_prix);

		btnComparisonPrice.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent comparisonIntent = new Intent(ProduitInfoActivity.this,
						PriceComparisonActivity.class);
				comparisonIntent.putExtra("produit", produit);
				startActivity(comparisonIntent);
			}
		});

		btnEvolutionPrice = findViewById(R.id.scan_button_evolution_prix);
		btnEvolutionPrice.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent evolutionIntent = new Intent(ProduitInfoActivity.this,
						PriceEvolutionActivity.class);
				evolutionIntent.putExtra("produit", produit);
				startActivity(evolutionIntent);
			}
		});
		launchScan();
	}

	private final ActivityResultLauncher<ScanOptions> scanLauncher =
			registerForActivityResult(new ScanContract(), result -> {
				if (result.getContents() == null)
				{
					Toast.makeText(ProduitInfoActivity.this, "Scan cancelled", Toast.LENGTH_LONG).show();
					finish();
				}
				else
				{
					try {
						CompletableFuture<Produit> f = Produit.getProduit(result.getContents());
						this.produit = f.get();
						displayInfosProduit();
					} catch (Exception e){
						e.printStackTrace();
					}
				}
			});

	private void launchScan()
	{
		ScanOptions options = new ScanOptions();
		options.setPrompt("For flash use volume up key");
		options.setBeepEnabled(true);
		options.setOrientationLocked(true);
		options.setCaptureActivity(Capture.class);
		scanLauncher.launch(options);
	}

	private void displayInfosProduit()
	{
		try{
			if (produit.getImagePath() != null && !produit.getImagePath().isEmpty())
				loadProduitImage();
//			editTextCodeBarres.setText(produit.getCodeBarres());
//			editTextCodeBarres.setEnabled(false);
			if (!produit.getMarque().isEmpty())
			{
				textViewMarque.setText(produit.getMarque());
				textViewMarque.setEnabled(false);
			}
			if (!produit.getNom().isEmpty())
			{
				textViewNom.setText(produit.getNom());
				textViewNom.setEnabled(false);
			}
			if (produit.getContenu() != null && !produit.getContenu().isEmpty())
			{
				textViewContenu.setText(produit.getContenu());
				textViewContenu.setEnabled(false);
			}
//			if (!produit.getImagePath().isEmpty())
//			{
//				editTextimagePath.setText(produit.getImagePath());
//				editTextimagePath.setEnabled(false);
//			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	private void loadProduitImage()
	{
		Picasso.get().load(produit.getImagePath()).into(imageView);
	}
}