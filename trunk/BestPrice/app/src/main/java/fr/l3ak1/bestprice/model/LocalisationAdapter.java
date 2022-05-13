package fr.l3ak1.bestprice.model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

import fr.l3ak1.bestprice.R;

/**
 * Class to display nearby stores in a ListView
 */
public class LocalisationAdapter extends BaseAdapter
{
	private Activity activity;
	private List<Localisation> localisationList;

	public LocalisationAdapter(Activity activity, List<Localisation> localisationList)
	{
		this.activity = activity;
		this.localisationList = localisationList;
	}

	@Override
	public int getCount()
	{
		return localisationList.size();
	}

	@Override
	public Localisation getItem(int i)
	{
		return localisationList.get(i);
	}

	@Override
	public long getItemId(int i)
	{
		return 0;
	}

	@Override
	public View getView(int i, View view, ViewGroup parent)
	{
		View oneLocalisationLine;
		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		oneLocalisationLine = inflater.inflate(R.layout.localisation_one_line, parent, false);
		TextView tvNom = oneLocalisationLine.findViewById(R.id.loc_line_text_nom);
		TextView tvDist = oneLocalisationLine.findViewById(R.id.loc_line_text_distance);

		Localisation l = this.getItem(i);

		if (l.getNom() != null && !l.getNom().isEmpty())
			tvNom.setText(l.getNom());
//		if (l.getDistance() != 0)
		double distKm = l.getDistance() / 1000.0;
		distKm = (double) (Math.round(distKm*1000.0)/1000.0);
		tvDist.setText(Double.toString(distKm));

		return oneLocalisationLine;
	}
}
