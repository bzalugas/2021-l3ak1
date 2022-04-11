package fr.l3ak1.bestprice.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import java.util.ArrayList;
import java.util.List;

public class DatabaseSQLite extends SQLiteOpenHelper
{
	private static final String DB_NAME = "BestPriceDB.db";
	private static final int DB_VERSION = 1;
	/*attributes table localisation*/
	private static final String TABLE_LOCALISATION = "Localisation";
	private static final String COL_LOCALISATION_ID = "id";
	private static final String COL_LATITUDE = "latitude";
	private static final String COL_LONGITUDE = "longitude";
	private static final String COL_LOCALISATION_NOM = "nom";
	/*attributes table produit*/
	private static final String TABLE_PRODUIT = "Produit";
	private static final String COL_PRODUIT_CODE_BARRES = "codeBarres";
	private static final String COL_MARQUE = "marque";
	private static final String COL_PRODUIT_NOM = "nom";
	private static final String COL_CONTENU = "contenu";
	private static final String COL_IMAGE_PATH = "imagePath";
	/*attributes table prix*/
	private static final String TABLE_PRIX = "Prix";
	private static final String COL_PRIX_ID = "id";
	private static final String COL_PRIX_CODE_BARRES = "produit_codeBarres";
	private static final String COL_PRIX = "prix";
	private static final String COL_DATE = "datePrix";
	private static final String COL_PRIX_LOCALISATION_ID = "localisation_id";
	/*DB creation instructions*/
	private static final String CREATE_LOCALISATION =
			"CREATE TABLE " + TABLE_LOCALISATION + " ("
			+ COL_LOCALISATION_ID + " SERIAL PRIMARY KEY, "
			+ COL_LATITUDE + " DECIMAL(8,6) NOT NULL, "
			+ COL_LONGITUDE + " DECIMAL(7,6) NOT NULL, "
			+ COL_LOCALISATION_NOM + " varchar(100) NOT NULL, "
			+ "CONSTRAINT lat_long_unique UNIQUE(" + COL_LATITUDE + ", " + COL_LONGITUDE + ")); ";
	private static final String CREATE_PRODUIT =
			"CREATE TABLE " + TABLE_PRODUIT + " ("
			+ COL_PRODUIT_CODE_BARRES + " VARCHAR(15) PRIMARY KEY, "
			+ COL_MARQUE + " VARCHAR(255) NOT NULL, "
			+ COL_PRODUIT_NOM + " VARCHAR(255) NOT NULL, "
			+ COL_CONTENU + " VARCHAR(255) DEFAULT NULL, "
			+ COL_IMAGE_PATH + " TEXT);";
	private static final String CREATE_PRIX =
			"CREATE TABLE " + TABLE_PRIX + "( "
			+ COL_PRIX_ID + " SERIAL PRIMARY KEY, "
			+ COL_PRIX_CODE_BARRES + "  VARCHAR(15) NOT NULL REFERENCES " +  TABLE_PRODUIT +
					"(codeBarres) ON DELETE CASCADE ON UPDATE CASCADE, "
			+ COL_PRIX + " NUMERIC(6, 2) NOT NULL, "
			+ COL_DATE + " DATE NOT NULL DEFAULT CURRENT_DATE, "
			+ COL_PRIX_LOCALISATION_ID + " SERIAL NOT NULL REFERENCES " + TABLE_LOCALISATION + "(id) ON DELETE RESTRICT ON UPDATE CASCADE, "
			+ "CONSTRAINT cb_prix_datePrix_loc_unique UNIQUE(" + COL_PRIX_CODE_BARRES +
			", " + COL_PRIX + ", " + COL_DATE + ", " + COL_PRIX_LOCALISATION_ID + "));";

	public DatabaseSQLite(Context context)
	{
		super (context, DB_NAME, null, DB_VERSION);
	}

	public boolean addProduit(Produit produit)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();

		cv.put(COL_PRODUIT_CODE_BARRES, produit.getCodeBarres());
		cv.put(COL_MARQUE, produit.getMarque());
		cv.put(COL_PRODUIT_NOM, produit.getNom());
		cv.put(COL_CONTENU, produit.getContenu());
		cv.put(COL_IMAGE_PATH, produit.getImagePath());

		long insert = db.insert(TABLE_PRODUIT, null, cv);
		if (insert == -1)
			return false;
		return true;
	}

	public List<Produit> getAllProduits()
	{
		List<Produit> produits = new ArrayList<>();
		String query = "SELECT * FROM " + TABLE_PRODUIT;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst())
		{
			do
			{
				String codeBarres = cursor.getString(0);
				String marque = cursor.getString(1);
				String nom = cursor.getString(2);
				String contenu = cursor.getString(3);
				String imagePath = cursor.getString(4);

				produits.add(new Produit(codeBarres, marque, nom, contenu, imagePath));
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return produits;
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(CREATE_LOCALISATION);
		db.execSQL(CREATE_PRODUIT);
		db.execSQL(CREATE_PRIX);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE " + TABLE_PRIX + ";");
		db.execSQL("DROP TABLE " + TABLE_PRODUIT + ";");
		db.execSQL("DROP TABLE " + TABLE_LOCALISATION + ";");
		onCreate(db);
	}
}
