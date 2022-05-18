package fr.l3ak1.bestprice.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to handle local database operations
 */
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
	private static final String COL_QUANTITE = "quantite";
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
			+ COL_MARQUE + " VARCHAR(255) DEFAULT NULL, "
			+ COL_PRODUIT_NOM + " VARCHAR(255) NOT NULL, "
			+ COL_QUANTITE + " VARCHAR(255) DEFAULT NULL, "
			+ COL_IMAGE_PATH + " TEXT);";
	private static final String CREATE_PRIX =
			"CREATE TABLE " + TABLE_PRIX + "( "
			+ COL_PRIX_ID + " SERIAL PRIMARY KEY, "
			+ COL_PRIX_CODE_BARRES + "  VARCHAR(15) NOT NULL REFERENCES " +  TABLE_PRODUIT +
					"(codeBarres) ON DELETE CASCADE ON UPDATE CASCADE, "
			+ COL_PRIX + " NUMERIC(6, 2) NOT NULL, "
			+ COL_DATE + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
			+ COL_PRIX_LOCALISATION_ID + " SERIAL NOT NULL REFERENCES " + TABLE_LOCALISATION + "(id) ON DELETE RESTRICT ON UPDATE CASCADE, "
			+ "CONSTRAINT cb_prix_datePrix_loc_unique UNIQUE(" + COL_PRIX_CODE_BARRES +
			", " + COL_PRIX + ", " + COL_DATE + ", " + COL_PRIX_LOCALISATION_ID + "));";

	public DatabaseSQLite(Context context)
	{
		super (context, DB_NAME, null, DB_VERSION);
	}

	/*Special historic methods*/

	public boolean deleteAll()
	{
		this.deleteAllPrix();
		this.deleteAllLocalisation();
		this.deleteAllProduit();
		return true;
	}

	/*Localisation related methods*/

	/**
	 * Add a Location to local database
	 * @param loc Location object to add
	 * @return true if the insert is a success, false if not
	 */
	public boolean addLocalisation(Localisation loc)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();

		cv.put(COL_LATITUDE, loc.getLatitude());
		cv.put(COL_LONGITUDE, loc.getLongitude());
		cv.put(COL_LOCALISATION_NOM, loc.getNom());

		long insert = db.insert(TABLE_LOCALISATION, null, cv);
		db.close();
		return insert != -1;
	}

	/**
	 * Get a location by id in local database
	 * @param id id of the location to search for
	 * @return Location object if found. Empty location object if not found.
	 */
	public Localisation getLocalisationById(long id)
	{
		Localisation loc = new Localisation();
		String query = "SELECT * FROM " + TABLE_LOCALISATION + " WHERE " + COL_LOCALISATION_ID +
				" = ?;";
		SQLiteDatabase db = this.getReadableDatabase();
		String[] params = {Long.toString(id)};
		Cursor cursor = db.rawQuery(query, params);
		if (cursor.moveToFirst())
		{
			loc.setId(id);
			loc.setLatitude(cursor.getDouble(1));
			loc.setLongitude(cursor.getDouble(2));
			loc.setNom(cursor.getString(3));
		}
		cursor.close();
		db.close();
		return loc;
	}

	/**
	 * Delete all instances of location
	 * @return true if success, false otherwise.
	 */
	public boolean deleteAllLocalisation()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "DELETE FROM " + TABLE_LOCALISATION;
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst())
		{
			cursor.close();
			db.close();
			return true;
		}
		cursor.close();
		db.close();
		return false;
	}

	/*Prix related methods*/

	/**
	 * Add a Price to local database
	 * @param prix Price object to add
	 * @return true if the insert successes, false otherwise
	 */
	public boolean addPrix(Prix prix)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();

		cv.put(COL_PRIX_CODE_BARRES, prix.getCodeBarres());
		cv.put(COL_PRIX, prix.getPrix());
		cv.put(COL_DATE, prix.getDate());
		cv.put(COL_PRIX_LOCALISATION_ID, prix.getLocalisation_id());

		long insert = db.insert(TABLE_PRIX, null, cv);
		db.close();
		return insert != -1;
	}

	/**
	 * Get a Price by ID in local database
	 * @param id ID of the Price to search for
	 * @return Price object if found. Empty Price object otherwise.
	 */
	public Prix getPrixById(long id)
	{
		Prix prix = new Prix();
		String query = "SELECT * FROM " + TABLE_PRIX + " WHERE " + COL_PRIX_ID + " = ?;";
		SQLiteDatabase db = this.getReadableDatabase();
		String[] params = {Long.toString(id)};
		Cursor cursor = db.rawQuery(query, params);
		if (cursor.moveToFirst())
		{
			prix.setId(id);
			prix.setCodeBarres(cursor.getString(1));
			prix.setPrix(cursor.getDouble(2));
			prix.setDate(cursor.getString(3));
			prix.setLocalisation_id(cursor.getLong(4));
		}
		cursor.close();
		db.close();
		return prix;
	}

	/**
	 * Delete all prices in local database
	 * @return the success of the operation (boolean)
	 */
	public boolean deleteAllPrix()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "DELETE FROM " + TABLE_PRIX;
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst())
		{
			cursor.close();
			db.close();
			return true;
		}
		cursor.close();
		db.close();
		return false;
	}

	/**
	 * Get last entered price for each product
	 * @param produits
	 * @return
	 */
	public List<Prix> getLastPrices(List<Produit> produits)
	{
		List<Prix> prices = new ArrayList<>();
		String query =
				"SELECT * FROM " + TABLE_PRIX +
						" WHERE " + COL_PRIX_CODE_BARRES + " = ?" +
						" ORDER BY " + COL_DATE + " DESC" +
						" LIMIT 1";
		String[] params = new String[1];
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor;
		for (Produit p : produits)
		{
			params[0] = p.getCodeBarres();
			cursor = db.rawQuery(query, params);
			if (cursor.moveToFirst())
			{
				long id = cursor.getLong(0);
				double prix = cursor.getDouble(2);
				String date = cursor.getString(3);
				long locId = cursor.getLong(4);
				prices.add(new Prix(id, p.getCodeBarres(), prix,date,locId));
			}
			cursor.close();
		}
		db.close();
		return prices;
	}

	/*Produit related methods*/

	/**
	 * Save a product in local database
	 * @param produit the product to save
	 * @return a boolean representing success or not of the operation
	 */
	public boolean addProduit(Produit produit)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();

		cv.put(COL_PRODUIT_CODE_BARRES, produit.getCodeBarres());
		cv.put(COL_MARQUE, produit.getMarque());
		cv.put(COL_PRODUIT_NOM, produit.getNom());
		cv.put(COL_QUANTITE, produit.getQuantite());
		cv.put(COL_IMAGE_PATH, produit.getImagePath());

		long insert = db.insert(TABLE_PRODUIT, null, cv);
		db.close();
		if (insert == -1)
			return false;
		return true;
	}

	/**
	 * Get all products in local database
	 * @return a List containing all products in local database
	 */
	public List<Produit> getAllProduits()
	{
		List<Produit> produits = new ArrayList<>();
		String query =
				"SELECT * FROM " + TABLE_PRODUIT;
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

				produits.add(0, new Produit(codeBarres, marque, nom, contenu, imagePath));
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return produits;
	}

	/**
	 *  Get a product by its barcode
	 * @param codeBarres the barcode of the product to find
	 * @return the product if found
	 */
	public Produit getProduitByCodeBarres(String codeBarres)
	{
		Produit produit = new Produit();
		String query = "SELECT * FROM " + TABLE_PRODUIT + " WHERE " + COL_PRODUIT_CODE_BARRES +
				" = ?;";
		SQLiteDatabase db = this.getReadableDatabase();
		String[] params = {codeBarres};
		Cursor cursor = db.rawQuery(query, params);
		if (cursor.moveToFirst())
		{
			produit.setCodeBarres(codeBarres);
			produit.setMarque(cursor.getString(1));
			produit.setNom(cursor.getString(2));
			produit.setQuantite(cursor.getString(3));
			produit.setImagePath(cursor.getString(4));
		}
		cursor.close();
		db.close();
		return produit;
	}

	/**
	 * Delete all products in local database
	 * @return success or not of the operation
	 */
	public boolean deleteAllProduit()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "DELETE FROM " + TABLE_PRODUIT;
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst())
		{
			cursor.close();
			db.close();
			return true;
		}
		cursor.close();
		db.close();
		return false;
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
