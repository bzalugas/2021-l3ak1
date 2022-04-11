package fr.l3ak1.bestprice.model;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Prix implements Serializable
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

	public Prix(String codeBarres, double prix, String date, long localisation_id, String localisation_nom)
	{
		this.codeBarres = codeBarres;
		this.prix = prix;
		this.date = date;
		this.localisation_id = localisation_id;
		this.localisation_nom = localisation_nom;
	}

	public Prix(long id, String codeBarres, double prix, String date, long localisation_id,
				String localisation_nom)
	{
		this.id = id;
		this.codeBarres = codeBarres;
		this.prix = prix;
		this.date = date;
		this.localisation_id = localisation_id;
		this.localisation_nom = localisation_nom;
	}

	public static CompletableFuture<ArrayList<Prix>> getAllPrix(String codeBarres) throws IOException
	{
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
					switch (response.code())
					{
						default:
//							throw new IOException("Error in request, error code : " + response.code());
							ArrayList<Prix> prix = new ArrayList<>();
							prix.add(new Prix());
							prix.get(1).setCodeBarres(codeBarres);
							f.complete(prix);
					}
				}
				Gson gson = new Gson();
				Type type = new TypeToken<ArrayList<Prix>>(){}.getType();
//				ArrayList<Prix> prix = new ArrayList<>();
				ArrayList<Prix> prix = gson.fromJson(response.body().string(), type);
				f.complete(prix);
			}
		});
		return f;
	}

	public CompletableFuture<Boolean> addPrix() throws IOException
	{
		CompletableFuture<Boolean> f = new CompletableFuture<>();
		Gson gson = new Gson();
		RequestBody body;
		Request postRequest;
//		gson.toJson(this);
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
		this.prix = prix;
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
