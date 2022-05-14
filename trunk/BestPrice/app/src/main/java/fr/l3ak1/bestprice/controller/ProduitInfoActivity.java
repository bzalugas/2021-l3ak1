package fr.l3ak1.bestprice.controller;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.squareup.picasso.Picasso;

import java.util.concurrent.CompletableFuture;

import fr.l3ak1.bestprice.R;
import fr.l3ak1.bestprice.model.DatabaseSQLite;
import fr.l3ak1.bestprice.model.Prix;
import fr.l3ak1.bestprice.model.Produit;

public class ProduitInfoActivity extends AppCompatActivity
{
	private Produit produit;
	private Prix prix;
	private TextView textViewMarque;
	private TextView textViewNom;
	private TextView textViewContenu;
	private TextView tvPrix;
	private ImageView imageView;
	private Button btnComparisonPrice;
	private Button btnEvolutionPrice;

	@Override
	protected void onCreate(Bundle savedInstance)
	{
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_produit_info);

		textViewMarque = findViewById(R.id.scan_text_marque);
		textViewNom = findViewById(R.id.scan_text_nom);
		textViewContenu = findViewById(R.id.scan_text_quantite);
		imageView = findViewById(R.id.scan_imageview);
		btnComparisonPrice = findViewById(R.id.scan_button_comparaison_prix);
		tvPrix = findViewById(R.id.scan_text_prix);

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
		if (getIntent().getBooleanExtra("SCAN_NEEDED", false))
			launchScan();
		else
		{
			this.produit = (Produit) getIntent().getSerializableExtra("PRODUIT");
			try{
				CompletableFuture<Prix> fPrix = Prix.getCheapest(this.produit.getCodeBarres());
				this.prix = fPrix.get();
			} catch (Exception e){
				e.printStackTrace();
			}
			displayInfosProduit();
		}
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
						CompletableFuture<Produit> fProduit = Produit.getProduit(result.getContents());
						CompletableFuture<Prix> fPrix = Prix.getCheapest(result.getContents());
						this.produit = fProduit.get();
						this.prix = fPrix.get();
						saveLocalProduit();
						displayInfosProduit();
					} catch (Exception e){
						e.printStackTrace();
					}
				}
			});


	private void saveLocalProduit()
	{
		if (!this.produit.minComplete())
			return;
		DatabaseSQLite db = new DatabaseSQLite(ProduitInfoActivity.this);
		Produit prod = db.getProduitByCodeBarres(this.produit.getCodeBarres());
		if (prod.minComplete())
			return;
		boolean success = db.addProduit(this.produit);
		if (!success)
			Toast.makeText(this, "Error trying to add produit", Toast.LENGTH_SHORT).show();
	}

	private void launchScan()
	{
		ScanOptions options = new ScanOptions();
		options.setPrompt("For flash use volume up key");
		options.setBeepEnabled(false);
		options.setOrientationLocked(true);
		options.setDesiredBarcodeFormats(ScanOptions.PRODUCT_CODE_TYPES);
		options.setCaptureActivity(Capture.class);
		scanLauncher.launch(options);
	}

	private void displayInfosProduit()
	{
		if (!produit.minComplete())
			return;
		try{
			if (produit.getImagePath() != null && !produit.getImagePath().isEmpty())
				loadProduitImage();
			if (produit.getMarque() != null && !produit.getMarque().isEmpty())
				textViewMarque.setText(produit.getMarque());
			if (!produit.getNom().isEmpty())
				textViewNom.setText(produit.getNom());
			if (produit.getQuantite() != null && !produit.getQuantite().isEmpty())
				textViewContenu.setText(produit.getQuantite());
			if (prix != null)
				tvPrix.setText(Double.toString(prix.getPrix()));
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	private void loadProduitImage()
	{
		Picasso.get().load(produit.getImagePath()).into(imageView);
	}
}