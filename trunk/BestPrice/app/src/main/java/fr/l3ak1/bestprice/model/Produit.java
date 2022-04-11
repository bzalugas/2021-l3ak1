package fr.l3ak1.bestprice.model;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import fr.l3ak1.bestprice.controller.ProduitInfoActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Produit implements Serializable
{
	@SerializedName("codebarres") private String codeBarres;
	private String marque;
	private String nom;
	private String contenu;
	@SerializedName("imagepath") private String imagePath;
	private static final OkHttpClient client = new OkHttpClient();

	public Produit()
	{
		this.codeBarres = "";
		this.marque = "";
		this.nom = "";
		this.contenu = "";
		this.imagePath = "";
	}

	public Produit(String codeBarres, String marque, String nom, String contenu, String imagePath)
	{
		this.codeBarres = codeBarres;
		this.marque = marque;
		this.nom = nom;
		this.contenu = contenu;
		this.imagePath = imagePath;
	}

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
					switch (response.code())
					{
						default:
//							throw new IOException("Error in request, error code : " + response.code());
							Produit prod = new Produit();
							prod.setCodeBarres(codeBarres);
							f.complete(prod);
					}
				}
					Gson gson = new Gson();
					f.complete(gson.fromJson(response.body().string(), Produit.class));
			}
		});
		return f;
	}

	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder("code-barres : ");
		s.append(codeBarres).append("\nmarque : ").append(marque).append("\nnom : ")
				.append(nom).append("\ncontenu : ").append(contenu).append("\nimage : ").append(imagePath);
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

	public String getContenu()
	{
		return contenu;
	}

	public void setContenu(String contenu)
	{
		this.contenu = contenu;
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
