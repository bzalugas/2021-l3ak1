package fr.l3ak1.bestprice.model;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Localisation implements Serializable
{
	private long id;
	private double latitude;
	private double longitude;
	private String nom;
	private static final transient OkHttpClient client = new OkHttpClient();

	public Localisation()
	{
		id = 0;
		latitude = 0;
		longitude = 0;
		nom = "";
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
				Gson gson = new Gson();
				f.complete(gson.fromJson(response.body().string(), Localisation.class));
			}
		});
		return f;
	}

	public long getId()
	{
//		return 1;
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public double getLatitude()
	{
		return latitude;
	}

	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

	public double getLongitude()
	{
		return longitude;
	}

	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}

	public String getNom()
	{
		return nom;
	}

	public void setNom(String nom)
	{
		this.nom = nom;
	}
}
