package fr.l3ak1.bestprice.controller;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import fr.l3ak1.bestprice.R;
import fr.l3ak1.bestprice.model.Localisation;
import fr.l3ak1.bestprice.model.LocalisationAdapter;

/**
 * Handle the location and store of the user
 */
public class LocalisationActivity extends AppCompatActivity implements LocationListener
{
	private double latitude, longitude;
	private FusedLocationProviderClient fusedLocationProviderClient;
	private LocationManager locationManager;
	private final static String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.ACCESS_FINE_LOCATION};
	private final static int PERMISSIONS_ALL = 1;
	private ListView lvMagasins;
	private Button btnAdd;
	private Localisation user_location;
	private List<Localisation> localisationList;
	private LocalisationAdapter adapter;
	private boolean getStore;
	private int radius;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getStore = getIntent().getBooleanExtra("getStore", false);
		this.radius = getIntent().getIntExtra("radius", 500);
		if (!getStore)
			setContentView(R.layout.activity_localisation_loading);
		else
		{
			setContentView(R.layout.activity_localisation);

			lvMagasins = findViewById(R.id.loc_list_view);
			btnAdd = findViewById(R.id.loc_button_add);

			this.user_location = (Localisation) getIntent().getSerializableExtra("localisation");

			lvMagasins.setOnItemClickListener(new AdapterView.OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{
					user_location = (Localisation) parent.getItemAtPosition(position);
					sendLocationAndFinish();
				}
			});

			btnAdd.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					askStore();
				}
			});
		}

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if(Build.VERSION.SDK_INT>=23){
			requestPermissions(PERMISSIONS, PERMISSIONS_ALL);
		}else{
			requestLocation();
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	/**
	 * Ask the user for the store name
	 */
	private void askStore()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(LocalisationActivity.this);
		builder.setTitle("Veuillez entrer le nom du magasin (ex : Super U Montreuil)");

		final EditText input = new EditText(LocalisationActivity.this);
		builder.setView(input);
		builder.setPositiveButton("Entrer", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialogInterface, int i)
			{
				createLocation(input.getText().toString());
				dialogInterface.cancel();
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
	 * Add the store name to the location
	 * @param nom name of the store
	 */
	private void createLocation(String nom)
	{
		this.user_location.setNom(nom);
		try{
			CompletableFuture<Boolean> f = this.user_location.insert();
			boolean success = f.get();
			if (success)
				showStores();
			else
				Toast.makeText(this, "Error trying to save the current store", Toast.LENGTH_LONG).show();
		} catch(Exception e ){
			e.printStackTrace();
		}
	}

	/**
	 * Add the location to the Intent and stop the activity
	 */
	private void sendLocationAndFinish()
	{
		Intent intent = new Intent();
		intent.putExtra("LOCALISATION", this.user_location);
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * Get the nearby stores and display it to the ListView
	 */
	private void showStores()
	{
		try {
			CompletableFuture<List<Localisation>> f = user_location.getNearbyLocations(this.radius);
			this.localisationList = f.get();
			if (this.localisationList != null && !this.localisationList.isEmpty())
			{
				this.adapter = new LocalisationAdapter(this, this.localisationList);
				lvMagasins.setAdapter(adapter);
			}
			else
				askStore();

		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onLocationChanged(@NonNull Location location)
	{
		if (user_location == null)
		{
			this.latitude = location.getLatitude();
			this.longitude = location.getLongitude();
			this.user_location = new Localisation(this.latitude, this.longitude);
		}
		if (getStore)
			showStores();
		else
			sendLocationAndFinish();
		locationManager.removeUpdates(this);
	}

	@Override
	public void onStatusChanged(String s, int i, Bundle b)
	{

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
			requestLocation();
		}
	}

	/**
	 * Ask the location permission
	 */
	public void requestLocation(){
		if(locationManager == null){
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		}
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1000, this);
			}
		}
	}
}