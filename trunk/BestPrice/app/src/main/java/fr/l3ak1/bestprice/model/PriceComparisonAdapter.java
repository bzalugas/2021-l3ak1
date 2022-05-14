package fr.l3ak1.bestprice.model;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import fr.l3ak1.bestprice.R;

public class PriceComparisonAdapter extends BaseAdapter
{
	private Activity activity;
	private List<Prix> prixList;
	private List<Localisation> localisationList;

	public PriceComparisonAdapter(Activity activity, List<Prix> prixList, List<Localisation> localisationList)
	{
		this.activity = activity;
		this.prixList = prixList;
		this.localisationList = localisationList;
	}

	@Override
	public int getCount()
	{
		return prixList.size();
	}

	@Override
	public Prix getItem(int i)
	{
		return prixList.get(i);
	}

	@Override
	public long getItemId(int i)
	{
		return 0;
	}

	@Override
	public View getView(int i, View view, ViewGroup parent)
	{
		View comparisonOneLine;
		LayoutInflater inflater =
				(LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		comparisonOneLine = inflater.inflate(R.layout.price_comparison_one_line, parent, false);

		TextView tvNom = comparisonOneLine.findViewById(R.id.comparison_line_tv_enseigne);
		TextView tvPrix = comparisonOneLine.findViewById(R.id.comparison_line_tv_prix);
		TextView tvDist = comparisonOneLine.findViewById(R.id.comparison_line_tv_distance);

		Prix prix = this.getItem(i);
		Localisation loc = findLocalisation(prix);

		if (loc.getNom() != null && !loc.getNom().isEmpty())
			tvNom.setText(loc.getNom());
		tvPrix.setText(Double.toString(prix.getPrix()));
		double distKm = loc.getDistance() / 1000.0;
		distKm = (double) (Math.round(distKm * 1000.0) / 1000.0);
		tvDist.setText(Double.toString(distKm));

		return comparisonOneLine;
	}

	private Localisation findLocalisation(Prix prix)
	{
		for (Localisation l : localisationList)
			if (l.getId() == prix.getLocalisation_id())
				return l;
		return null;
	}
}
