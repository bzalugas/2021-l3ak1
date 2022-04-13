package fr.l3ak1.bestprice.model;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Class to represent a Product
 */
public class Produit implements Serializable
{
	@SerializedName("codebarres") private String codeBarres;
	private String marque;
	private String nom;
	private String quantite;
	@SerializedName("imagepath") private String imagePath;
	private static final OkHttpClient client = new OkHttpClient();

	public Produit()
	{
		this.codeBarres = null;
		this.marque = null;
		this.nom = null;
		this.quantite = null;
		this.imagePath = null;
	}

	public Produit(String codeBarres, String marque, String nom, String quantite, String imagePath)
	{
		this.codeBarres = codeBarres;
		this.marque = marque;
		this.nom = nom;
		this.quantite = quantite;
		this.imagePath = imagePath;
	}

	/**
	 * get a Product by barcode from the online API
	 * @param codeBarres the barcode to search for
	 * @return the CompletableFuture containing the Product
	 * @throws IOException
	 */
	public static CompletableFuture<Produit> getProduit(String codeBarres) throws IOException
	{
		CompletableFuture<Produit> f = new CompletableFuture<>();
		HttpUrl.Builder queryBuilder;
		Request request;

		queryBuilder =
				HttpUrl.get(Database.URL_API + "/api/produit/get.php").newBuilder();
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
					Produit prod = new Produit();
					prod.setCodeBarres(codeBarres);
					f.complete(prod);
				}
				else
				{
					Gson gson = new Gson();
					f.complete(gson.fromJson(response.body().string(), Produit.class));
				}
			}
		});
		return f;
	}

	/**
	 * Check if the minimum fields required for DB are set
	 * @return true if the minimum fields are set and false otherwise
	 */
	public boolean minComplete()
	{
		return (this.codeBarres != null && this.nom != null);
	}

	/**
	 * Ceck if all the fields are set
	 * @return true if all fields are set and false otherwise
	 */
	public boolean isComplete()
	{
		return (this.codeBarres != null && this.marque != null && this.nom != null
				&& this.quantite != null && this.imagePath != null);
	}

	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder("code-barres : ");
		s.append(codeBarres).append("\nmarque : ").append(marque).append("\nnom : ")
				.append(nom).append("\ncontenu : ").append(quantite).append("\nimage : ").append(imagePath);
		return s.toString();
	}

	public String getCodeBarres(){return codeBarres;}

	public void setCodeBarres(String codeBarres)
	{
		this.codeBarres = codeBarres;
	}

	public String getMarque()
	{
		return marque;
	}

	public void setMarque(String marque)
	{
		this.marque = marque;
	}

	public String getNom()
	{
		return nom;
	}

	public void setNom(String nom)
	{
		this.nom = nom;
	}

	public String getQuantite()
	{
		return quantite;
	}

	public void setQuantite(String quantite)
	{
		this.quantite = quantite;
	}

	public String getImagePath()
	{
		return imagePath;
	}

	public void setImagePath(String imagePath)
	{
		this.imagePath = imagePath;
	}
}
