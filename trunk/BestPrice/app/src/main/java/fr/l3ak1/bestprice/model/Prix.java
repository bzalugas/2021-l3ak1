package fr.l3ak1.bestprice.model;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Class to represent a Price
 */
public class Prix implements Serializable, Comparable<Prix>
{
	private long id;
	@SerializedName("produit_codebarres") private String codeBarres;
	private double prix;
	@SerializedName("dateprix") private String date;
	private long localisation_id;
	private transient String localisation_nom;
	private static final transient OkHttpClient client = new OkHttpClient();

	public Prix()
	{
		id = 0;
		prix = 0;
		date = "";
		localisation_id = 0;
		localisation_nom = "";
	}

	public Prix(long id, String codeBarres, double prix, String date, long localisation_id)
	{
		this.id = id;
		this.codeBarres = codeBarres;
		this.prix = (double) (Math.round(prix * 100.0) / 100.0);
		this.date = date;
		this.localisation_id = localisation_id;
	}

	public Prix(String codeBarres, double prix, String date, long localisation_id)
	{
		this.codeBarres = codeBarres;
		this.prix = (double) (Math.round(prix * 100.0) / 100.0);
		this.date = date;
		this.localisation_id = localisation_id;
	}

	public Prix(String codeBarres, double prix, String date, long localisation_id, String localisation_nom)
	{
		this.codeBarres = codeBarres;
		this.prix = (double) (Math.round(prix * 100.0) / 100.0);
		this.date = date;
		this.localisation_id = localisation_id;
		this.localisation_nom = localisation_nom;
	}

	public Prix(long id, String codeBarres, double prix, String date, long localisation_id,
				String localisation_nom)
	{
		this.id = id;
		this.codeBarres = codeBarres;
		this.prix = (double) (Math.round(prix * 100.0) / 100.0);
		this.date = date;
		this.localisation_id = localisation_id;
		this.localisation_nom = localisation_nom;
	}

	/**
	 * Get a list of prices by barcode of product from the online API
	 * @param codeBarres the barcode to search for
	 * @return an ArrayList of prices contained in a CompletableFuture
	 * @throws IOException
	 */
	public static CompletableFuture<ArrayList<Prix>> getAllPrix(String codeBarres) throws IOException
	{
		Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
		CompletableFuture<ArrayList<Prix>> f = new CompletableFuture<>();
		HttpUrl.Builder queryBuilder;
		Request request;
		queryBuilder =
				HttpUrl.get(Database.URL_API + "/api/produit/getAllPrix.php").newBuilder();
		queryBuilder.addQueryParameter("codeBarres", codeBarres);

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
					ArrayList<Prix> prix = new ArrayList<>();
					prix.add(new Prix());
					prix.get(1).setCodeBarres(codeBarres);
					f.complete(prix);
				}
				else
				{
					Gson gson = new Gson();
					Type type = new TypeToken<ArrayList<Prix>>(){}.getType();
					ArrayList<Prix> prix = gson.fromJson(response.body().string(), type);
					f.complete(prix);
				}
				response.body().close();
			}
		});
		return f;
	}

	/**
	 * Get the list of all prices for one product in one store
	 * @param codeBarres the barcode of product
	 * @param localisation_id the location id of the store
	 * @return a completable future containing the list of prices
	 * @throws IOException
	 */
	public static CompletableFuture<List<Prix>> getAllPrixLoc(String codeBarres,
															  long localisation_id) throws IOException
	{
		CompletableFuture<List<Prix>> f = new CompletableFuture<>();
		HttpUrl.Builder queryBuilder;
		Request request;
		queryBuilder =
				HttpUrl.get(Database.URL_API + "/api/prix/getAllByLoc.php").newBuilder();
		queryBuilder.addQueryParameter("codeBarres", codeBarres);
		queryBuilder.addQueryParameter("localisation_id", Long.toString(localisation_id));

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
					f.complete(null);
				}
				else
				{
					Gson gson = new Gson();
					Type type = new TypeToken<ArrayList<Prix>>(){}.getType();
					ArrayList<Prix> prices = gson.fromJson(response.body().string(), type);
					f.complete(prices);
				}
				response.body().close();
			}
		});
		return f;
	}

	/**
	 * Add a price to online API
	 * @return a boolean representing the success or not of the request
	 * @throws IOException
	 */
	public CompletableFuture<Boolean> addPrix() throws IOException
	{
		Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
		CompletableFuture<Boolean> f = new CompletableFuture<>();
		Gson gson = new Gson();
		RequestBody body;
		Request postRequest;
		body = RequestBody.create(
				gson.toJson(this),
				MediaType.parse("application/json")
		);

		postRequest = new Request.Builder()
				.url(Database.URL_API + "/api/prix/add.php")
				.post(body)
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
				if (response.isSuccessful())
					f.complete(true);
				else
					f.complete(false);
				response.body().close();
			}
		});
		return f;
	}

	public static CompletableFuture<List<Prix>> getNearbyPrices(String codeBarres, double latitude,
																double longitude, int radius) throws IOException
	{
		Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
		CompletableFuture<List<Prix>> f = new CompletableFuture<>();
		HttpUrl.Builder queryBuilder;
		Request request;
		queryBuilder =
				HttpUrl.get(Database.URL_API + "/api/prix/getNearby.php").newBuilder();
		queryBuilder.addQueryParameter("codeBarres", codeBarres);
		queryBuilder.addQueryParameter("latitude", Double.toString(latitude));
		queryBuilder.addQueryParameter("longitude", Double.toString(longitude));
		queryBuilder.addQueryParameter("radius", Integer.toString(radius));

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
					List<Prix> prix = null;
					f.complete(prix);
				}
				else
				{
					Gson gson = new Gson();
					Type type = new TypeToken<List<Prix>>(){}.getType();
					List<Prix> prix = gson.fromJson(response.body().string(), type);
					f.complete(prix);
				}
				response.body().close();
			}
		});
		return f;
	}

	public static CompletableFuture<Prix> getCheapest(String codeBarres) throws IOException
	{
		Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
		CompletableFuture<Prix> f = new CompletableFuture<>();
		HttpUrl.Builder queryBuilder;
		Request request;
		queryBuilder =
				HttpUrl.get(Database.URL_API + "/api/prix/getCheapest.php").newBuilder();
		queryBuilder.addQueryParameter("codeBarres", codeBarres);

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
					Prix prix = null;
					f.complete(prix);
				}
				else
				{
					Gson gson = new Gson();
					Prix prix = gson.fromJson(response.body().string(), Prix.class);
					f.complete(prix);
				}
				response.body().close();
			}
		});
		return f;
	}

	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder(localisation_nom);
		s.append(" : ").append(prix).append("€");
		return s.toString();
	}

	@Override
	public int compareTo(Prix other)
	{
		return (int) (this.getPrix()*100.0 - other.getPrix()*100.0);
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getCodeBarres()
	{
		return codeBarres;
	}

	public void setCodeBarres(String codeBarres)
	{
		this.codeBarres = codeBarres;
	}

	public double getPrix()
	{
		return prix;
	}

	public String getLocalisationNom() { return localisation_nom; }

	public void setPrix(double prix)
	{
		this.prix = (double) (Math.round(prix * 100.0) / 100.0);
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public long getLocalisation_id()
	{
		return localisation_id;
	}

	public void setLocalisation_id(long localisation_id)
	{
		this.localisation_id = localisation_id;
	}

	public void setLocalisation_nom(String localisation_nom) { this.localisation_nom =
			localisation_nom; }


}
