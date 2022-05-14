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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import fr.l3ak1.bestprice.R;

/**
 * Class to display the Product history in a listView
 */
public class ProduitPrixAdapter extends BaseAdapter
{
	private Activity activity;
	private List<Produit> produitList;
	private List<Prix> prices;

	public ProduitPrixAdapter(Activity activity, List<Produit> produits, List<Prix> prices)
	{
		this.activity = activity;
		this.produitList = produits;
		this.prices = prices;
		sortPricesByDate();
		sortProduitsByDate();
	}

	private void sortPricesByDate()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date d1;
		Date d2;
		Prix tmp;
		for (int i = 0; i < prices.size() - 1; i++)
			for (int j = i + 1; j < prices.size(); j++)
			{
				try {
					d1 = sdf.parse(prices.get(i).getDate());
					d2 = sdf.parse(prices.get(j).getDate());
					if (d1.before(d2))
					{
						tmp = prices.get(i);
						prices.set(i, prices.get(j));
						prices.set(j, tmp);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
	}

	private void sortProduitsByDate()
	{
		int indexProduit;
		Produit tmp;
		for (int i = 0; i < prices.size(); i++)
		{
			indexProduit = getProduit(prices.get(i));
			tmp = produitList.get(indexProduit);
			produitList.set(indexProduit, produitList.get(i));
			produitList.set(i, tmp);
		}
	}

	private int getProduit(Prix prix)
	{
		for (int i = 0; i < produitList.size(); i++)
			if (produitList.get(i).getCodeBarres().equals(prix.getCodeBarres()))
				return i;
		return -1;
	}

	@Override
	public int getCount()
	{
		return produitList.size();
	}

	@Override
	public Produit getItem(int i)
	{
		return produitList.get(i);
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
		TextView tvQuantite = oneProduitLine.findViewById(R.id.produit_text_quantite);
		TextView tvPrix = oneProduitLine.findViewById(R.id.produit_text_prix);

		Produit p = this.getItem(i);
		Prix prix = prices.get(i);
//		Produit p = findProduit(prix);

		if (p.getImagePath() != null && !p.getImagePath().isEmpty())
			Picasso.get().load(p.getImagePath()).into(img);
		if (p.getMarque() != null && !p.getMarque().isEmpty())
			tvMarque.setText(p.getMarque());
		tvNom.setText(p.getNom());
		if (p.getQuantite() != null && !p.getQuantite().isEmpty())
			tvQuantite.setText(p.getQuantite());
		if (prices != null && !prices.isEmpty())
		{
//			double prix = findPrice(p);
//			prix = (double) (Math.round(prix.getPrix() * 100.0) / 100.0);
			if (prix.getPrix() != 0)
				tvPrix.setText(Double.toString(prix.getPrix()));
		}

		return oneProduitLine;
	}

	private Produit findProduit(Prix prix)
	{
		for (Produit p : this.produitList)
			if (p.getCodeBarres().equals(prix.getCodeBarres()))
				return p;
		return null;
	}

	private double findPrice(Produit p)
	{
		for (Prix prix : this.prices)
			if (prix.getCodeBarres().equals(p.getCodeBarres()))
				return prix.getPrix();
		return 0;
	}
}
