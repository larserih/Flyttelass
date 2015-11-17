package com.example.flyttelass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DB 
{
	Context context;
	static final String TAG = "DbHelper";
	static final String DB_NAVN = "boks.db";
	static final String TABELL = "Boks";
	static final String ID = BaseColumns._ID;
	static final String NR = "nummer";
	static final String INNHOLD = "innhold";
	static final String STED = "sted";
	static final int DB_VERSJON = 3;
	
	String[] cols = {NR,INNHOLD,STED};
	
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	public static ArrayList<String> DBArray = new ArrayList<String>();
	
	public DB(Context ctx)
	{
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	
	public static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context)
		{
			super(context, DB_NAVN, null, DB_VERSJON);
		}

		@Override
		public void onCreate(SQLiteDatabase db) 
		{
			String sql="create table " + TABELL + " ("
					+ ID + " integer primary key autoincrement, "
					+ NR + " int, "
					+ INNHOLD + " text, "
					+ STED + " text);";
					Log.d(TAG,"oncreated sql" + sql);
					db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
		{
			db.execSQL("drop table if exists " + TABELL);
			Log.d(TAG,"updated");
			onCreate(db);
		}
	}//slutt DatabaseHelper
	
	public DB open() throws SQLException
	{
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	public void close()
	{
		
	}
	//Metode for å legge inn i Boks-objektet og videre i database
	public void settInn(Boks boks)
	{
		db = DBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(NR, boks.getNummer());
		values.put(INNHOLD, boks.getInnhold());
		values.put(STED, boks.getSted());
		
		db.insert(TABELL, null, values);
		db.close();
	}
	//Metode for å slette en boks med gitt ID fra databasen
	public void slettEn(int id)
	{
		db = DBHelper.getReadableDatabase();
		String whereClause = "_ID = " + id;
		db.delete(TABELL, whereClause, null);
	}
	//Metode for å slette alle fra databasen
	public void slettDBinnhold()
	{
		db = DBHelper.getWritableDatabase();
		String sql= "DELETE FROM ";
		db.execSQL(sql + TABELL);
	}
	//Metode for å oppdatere boks i databasen etter redigering
	public void oppdater(int id, int nr, String innhold, String sted)
	{
		db = DBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(NR, nr);
		values.put(INNHOLD, innhold);
		values.put(STED, sted);
		
		String whereClause = "_ID = " + id;
		db.update(TABELL, values, whereClause, null);
		db.close();
	}
	//Metode for å vise alle bokser i listview
	public List<Boks> visAlle()
	{
		List<Boks> boksListe = new ArrayList<Boks>();
		String sql = "SELECT * FROM " + TABELL;
		db = DBHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		
		if(cursor.moveToFirst())
		{
			do
			{
				Boks boks = new Boks();
				boks.setID(Integer.parseInt(cursor.getString(0)));
				boks.setNummer(Integer.parseInt(cursor.getString(1)));
				boks.setInnhold(cursor.getString(2));
				boks.setSted(cursor.getString(3));

				boksListe.add(boks);
				Collections.sort(boksListe, new BoksComparator());
			}
			while(cursor.moveToNext());
		}
		db.close();
		return boksListe;
	}
	//Metode for å finne en boks
	public Cursor finnEn(Boks boks)
	{
		Cursor cur;
		cur = db.query(TABELL, cols, ID + "='" + boks.getID() + "'", null, null, null, null);
		return cur;
	}
	//Metode for å hente ut antall bokser i databasen
	public int getAntallPersonerDB() 
	{
		db = DBHelper.getReadableDatabase();
        String tellerQuery = "SELECT * FROM " + TABELL;
        
        Cursor cursor = db.rawQuery(tellerQuery, null);

        int count = cursor.getCount();
        db.close();
        return count;
    }
	
	public class BoksComparator implements Comparator<Boks> 
	{
	    public int compare(Boks b1, Boks b2) 
	    {
	    	return (Integer.compare(b1.getNummer(),b2.getNummer()));
	        //return p1.getFornavn().compareTo(p2.getFornavn());
	    }
	}
}

