package fr.l3ak1.bestprice.model;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Class to represent a Location
 */
public class Localisation implements Serializable
{
	private long id;
	private double latitude;
	private double longitude;
	private String nom;
	private transient double distance;
	private static final transient OkHttpClient client = new OkHttpClient();

	public Localisation()
	{
		id = 0;
		latitude = 0;
		longitude = 0;
		nom = "";
		distance = 0;
	}

	public Localisation(double latitude, double longitude)
	{
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Localisation(long id, double latitude, double longitude, String nom, double distance)
	{
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.nom = nom;
		this.distance = distance;
	}

	public Localisation(long id, double latitude, double longitude, String nom)
	{
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.nom = nom;
	}

	public Localisation(double latitude, double longitude, String nom)
	{
		this.latitude = latitude;
		this.longitude = longitude;
		this.nom = nom;
	}

	/**
	 * Get a location by ID from the online API
	 * @param id ID of the localisation searched
	 * @return Location object contained in completableFuture
	 * @throws IOException
	 */
	public static CompletableFuture<Localisation> getLocalisationById(long id) throws IOException
	{
		CompletableFuture<Localisation> f = new CompletableFuture<>();
		HttpUrl.Builder queryBuilder;
		Request request;

		queryBuilder = HttpUrl.get(Database.URL_API + "/api/localisation/get.php").newBuilder();
		queryBuilder.addQueryParameter("id", Long.toString(id));

		request = new Request.Builder().url(queryBuilder.build()).build();

		client.newCall(request).enqueue(new Callback()
		{
			@Override
			public void onFailure(@NonNull Call call, @NonNull IOException e)
			{
				e.printStackTrace();
			}

			@Override
			public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException
			{
				if (!response.isSuccessful())
					f.complete(new Localisation());
				else
				{
					Gson gson = new Gson();
					f.complete(gson.fromJson(response.body().string(), Localisation.class));
				}
			}
		});
		return f;
	}

	public CompletableFuture<Boolean> insert()
	{
		CompletableFuture<Boolean> f = new CompletableFuture<>();
		Gson gson = new Gson();
		String json = gson.toJson(this);
		RequestBody requestBody = RequestBody.create(
				json,
				MediaType.parse("application/json")
		);
		Request postRequest = new Request.Builder()
				.url(Database.URL_API + "/api/localisation/add.php")
				.post(requestBody)
				.build();
		client.newCall(postRequest).enqueue(new Callback()
		{
			@Override
			public void onFailure(@NonNull Call call, @NonNull IOException e)
			{
				e.printStackTrace();
			}

			@Override
			public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException
			{
				if (!response.isSuccessful())
					f.complete(false);
				else
				{
					Gson gson = new Gson();
					f.complete(true);
				}
			}
		});
		return f;
	}

	public CompletableFuture<List<Localisation>> getNearbyLocations(int radius) throws IOException
	{
		CompletableFuture<List<Localisation>> f = new CompletableFuture<>();
		HttpUrl.Builder queryBuilder;
		Request request;
		queryBuilder =
				HttpUrl.get(Database.URL_API + "/api/localisation/getRadius.php").newBuilder();
		queryBuilder.addQueryParameter("latitude", Double.toString(this.latitude));
		queryBuilder.addQueryParameter("longitude", Double.toString(this.longitude));
		queryBuilder.addQueryParameter("radius", Double.toString(radius));

		request = new Request.Builder().url(queryBuilder.build()).build();

		client.newCall(request).enqueue(new Callback()
		{
			@Override
			public void onFailure(@NonNull Call call, @NonNull IOException e)
			{
				e.printStackTrace();
			}

			@Override
			public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException
			{
				if (!response.isSuccessful())
				{
					List<Localisation> loc = new ArrayList<>();
					loc.add(new Localisation());
					f.complete(loc);
				}
				else
				{
					Gson gson = new Gson();
					Type type = new TypeToken<ArrayList<Localisation>>(){}.getType();
					ArrayList<Localisation> loc = gson.fromJson(response.body().string(), type);
					f.complete(loc);
				}
			}
		});
		return f;
	}

	public static CompletableFuture<Localisation> getLocalisationByName(String name) throws IOException
	{
		CompletableFuture<Localisation> f = new CompletableFuture<>();
		HttpUrl.Builder queryBuilder;
		Request request;

		queryBuilder = HttpUrl.get(Database.URL_API + "/api/localisation/get.php").newBuilder();
		queryBuilder.addQueryParameter("name", name);

		request = new Request.Builder().url(queryBuilder.build()).build();

		client.newCall(request).enqueue(new Callback()
		{
			@Override
			public void onFailure(@NonNull Call call, @NonNull IOException e)
			{
				e.printStackTrace();
			}

			@Override
			public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException
			{
				if (!response.isSuccessful())
					f.complete(new Localisation());
				Gson gson = new Gson();
				f.complete(gson.fromJson(response.body().string(), Localisation.class));
			}
		});
		return f;
	}

	/**
	 * get ID
	 * @return ID
	 */
	public long getId()
	{
		return id;
	}

	/**
	 * set ID
	 * @param id ID to set
	 */
	public void setId(long id)
	{
		this.id = id;
	}

	/**
	 * get latitude
	 * @return latitude
	 */
	public double getLatitude()
	{
		return latitude;
	}

	/**
	 * set latitude
	 * @param latitude latitude to set
	 */
	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

	/**
	 * get longitude
	 * @return longitude
	 */
	public double getLongitude()
	{
		return longitude;
	}

	/**
	 * set longitude
	 * @param longitude to set
	 */
	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}

	/**
	 * get name
	 * @return name
	 */
	public String getNom()
	{
		return nom;
	}

	/**
	 * set name
	 * @param nom name to set
	 */
	public void setNom(String nom)
	{
		this.nom = nom;
	}

	public double getDistance()
	{
		return distance;
	}

	public void setDistance(double distance)
	{
		this.distance = distance;
	}
}
