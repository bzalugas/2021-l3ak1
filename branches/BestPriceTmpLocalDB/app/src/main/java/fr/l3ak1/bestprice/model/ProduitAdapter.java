package fr.l3ak1.bestprice.model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import fr.l3ak1.bestprice.R;

public class ProduitAdapter extends BaseAdapter
{
	private Activity activity;
	private List<Produit> produits;

	public ProduitAdapter(Activity activity, List<Produit> produits)
	{
		this.activity = activity;
		this.produits = produits;
	}

	@Override
	public int getCount()
	{
		return produits.size();
	}

	@Override
	public Produit getItem(int i)
	{
		return produits.get(i);
	}

	@Override
	public long getItemId(int i)
	{
		return 0;
	}

	@Override
	public View getView(int i, View view, ViewGroup parent)
	{
		View oneProduitLine;
		LayoutInflater inflater =
				(LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		oneProduitLine = inflater.inflate(R.layout.produit_one_line, parent, false);
		ImageView img = oneProduitLine.findViewById(R.id.produit_image);
		TextView tvMarque = oneProduitLine.findViewById(R.id.produit_text_marque);
		TextView tvNom = oneProduitLine.findViewById(R.id.produit_text_nom);
		TextView tvContenu = oneProduitLine.findViewById(R.id.produit_text_contenu);
		Produit p = this.getItem(i);

		Picasso.get().load(p.getImagePath()).into(img);
		tvMarque.setText(p.getMarque());
		tvNom.setText(p.getNom());
		tvContenu.setText(p.getContenu());
		return oneProduitLine;
	}
}
