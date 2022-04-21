package fr.l3ak1.bestprice.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import fr.l3ak1.bestprice.databinding.ActivityLocalisationBinding;

import fr.l3ak1.bestprice.R;
import fr.l3ak1.bestprice.model.Localisation;
import fr.l3ak1.bestprice.model.LocalisationAdapter;

public class LocalisationActivity extends AppCompatActivity implements LocationListener
{
	private double latitude, longitude;
	private FusedLocationProviderClient fusedLocationProviderClient;
	private LocationManager locationManager;
	private final static String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.ACCESS_FINE_LOCATION};
	private final static int PERMISSIONS_ALL = 1;
	private TextView tvMagasins;
	private ListView lvMagasins;
	private Button btnAdd;
	private Localisation user_location;
	private List<Localisation> localisationList;
	private LocalisationAdapter adapter;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_localisation);

		tvMagasins = findViewById(R.id.loc_text_magasin);
		lvMagasins = findViewById(R.id.loc_list_view);
		btnAdd = findViewById(R.id.loc_button_add);

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if(Build.VERSION.SDK_INT>=23){
			requestPermissions(PERMISSIONS, PERMISSIONS_ALL);
		}else{
			requestLocation();
		}

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
	}

	private void createLocation(String nom)
	{
		this.user_location.setNom(nom);
		try{
			CompletableFuture<Boolean> f = this.user_location.insert();
			f.get();
		} catch(Exception e ){
			e.printStackTrace();
		}
	}

	private void sendLocationAndFinish()
	{
		Intent intent = new Intent();
		intent.putExtra("LOCALISATION", this.user_location);
		setResult(RESULT_OK, intent);
		finish();
	}

	private void showStores()
	{
		try {
			CompletableFuture<List<Localisation>> f = user_location.getNearbyLocations(100);
			this.localisationList = f.get();
			this.adapter = new LocalisationAdapter(this, this.localisationList);
			lvMagasins.setAdapter(adapter);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onLocationChanged(@NonNull Location location)
	{
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
		this.user_location = new Localisation(this.latitude, this.longitude);
//		Intent intent = new Intent();
//		intent.putExtra("LAT", this.latitude);
//		intent.putExtra("LONG", this.longitude);
//		setResult(RESULT_OK, intent);
		showStores();
//		finish();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
			requestLocation();
		}
	}

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