package com.example.flyttelass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity 
{
	private ListView listView; 
	TextView antall;
	DB db;
	Boks boks;
	ImageButton slettAlleBtn;
	ImageButton leggTilBtn;
	ListAdapter adapter;
	ArrayList<HashMap<String, String>> Items;
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final DB db = new DB(this);
        
        Items = new ArrayList<HashMap<String,String>>();
		List<Boks> data = db.visAlle();
		
		for(Boks val : data)
		{
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("_ID", Integer.toString(val.getID()));
			map.put("nummer", Integer.toString(val.getNummer()));
			map.put("innhold", val.getInnhold());
			map.put("sted", val.getSted());
			
			Items.add(map);
		}
		
		
		
		adapter = new SimpleAdapter(this, Items, R.layout.list_item, new String[]{"_ID","nummer","innhold","sted"}, 
				new int[] {R.id.itemId,R.id.itemNummer, R.id.itemInnhold, R.id.itemSted});
		setListAdapter(adapter);
		
		antall = (TextView)findViewById(R.id.antall);
		String count = Integer.toString(db.getAntallPersonerDB());
		antall.setText("Antall: " + count);
		listView = getListView();
		
		onPostExecute();
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, final View view,int position, long id) 
			{
				AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
				alert
				 	  .setTitle("Velg")
                     .setMessage("Vil du slette eller endre boksen?")
                     .setCancelable(true)
                     .setNegativeButton("Endre", new DialogInterface.OnClickListener() 
                     {

						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							int DBid = Integer.parseInt(((TextView) view.findViewById(R.id.itemId)).getText().toString());
							String nr = ((TextView)view.findViewById(R.id.itemNummer)).getText().toString();
							String innhold = ((TextView)view.findViewById(R.id.itemInnhold)).getText().toString();
							String sted = ((TextView)view.findViewById(R.id.itemSted)).getText().toString();
							
							//Legge variabler i intentens extras for å hente ut i redigerfragment
							Intent i = new Intent(getApplicationContext(), EditActivity.class);
							i.putExtra("id", DBid);
							i.putExtra("nummer", nr);
							i.putExtra("innhold", innhold);
							i.putExtra("sted", sted);
			                startActivity(i);
			                overridePendingTransition(0,0);
						} 
                     })
                     .setPositiveButton("Slette", new DialogInterface.OnClickListener() 
                     {
                    	int DBid = Integer.parseInt(((TextView) view.findViewById(R.id.itemId)).getText().toString());
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							db.slettEn(DBid);
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            overridePendingTransition(0,0);
						}
					})
					.setNeutralButton("Avbryt", new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							dialog.cancel();
						}
					});;
				AlertDialog alertDialog = alert.create();
                alertDialog.show();
				return true;
			}
		});
		
		leggTilBtn = (ImageButton)findViewById(R.id.leggTil);
		leggTilBtn.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{
				Intent i = new Intent(getApplicationContext(), AddActivity.class);
                startActivity(i);
                overridePendingTransition(0,0);
			}
			
		});
		
		slettAlleBtn = (ImageButton)findViewById(R.id.slettAlle);
        slettAlleBtn.setOnClickListener(new View.OnClickListener() 
        {
			
			@Override
			public void onClick(View v) 
			{
				// Sjekker om Person-tabellen allerede er tom
				if (db.getAntallPersonerDB() == 0)
				{
					AlertDialog.Builder TomTabell = new AlertDialog.Builder(MainActivity.this);
					TomTabell
					.setTitle("OBS")
					.setMessage("Listen er allerede tom.")
					.setCancelable(false)
					.setNeutralButton("Ok", new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							dialog.cancel();
						}
					});
					TomTabell.show();
				}
				else
				{
					AlertDialog.Builder bekreftSlettAlle = new AlertDialog.Builder(MainActivity.this);
					bekreftSlettAlle
					.setTitle("Sikker")
					.setMessage("Er du sikker på at du vil slette ALLE eskene?")
					.setCancelable(false)
					.setNegativeButton("Ja", new DialogInterface.OnClickListener() 
					{
						public void onClick(DialogInterface dialog, int which) 
						{
							db.slettDBinnhold();
				            
				          //Sjekke om tabellen er tømt ordentlig, AlertDialog 
				            if (db.getAntallPersonerDB() == 0)
							{
								Toast.makeText(getBaseContext(), "Alle esker er slettet", Toast.LENGTH_LONG).show();
							}
							//Om tabellen fortsatt inneholder elementer, AlertDialog
							else
							{
								Toast.makeText(getBaseContext(), "Kunne ikke slette alle. Kontakt sjefen(Lars-Erik)", Toast.LENGTH_LONG).show();
							}
							dialog.cancel();
				            Intent language = new Intent(MainActivity.this,MainActivity.class);
				            finish();
				            overridePendingTransition(0, 0);
				            startActivity(language);
						}
					})
					.setPositiveButton("Nei", new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							dialog.cancel();
						}
					});
					bekreftSlettAlle.show();
				}
				
				
			}
		});
    }
    
    protected void onPostExecute() 
    {
        runOnUiThread(new Runnable() 
        {
            public void run() 
            {

            	adapter = new SimpleAdapter(MainActivity.this, Items, R.layout.list_item, new String[]{"_ID","nummer","innhold","sted"}, 
        				new int[] {R.id.itemId,R.id.itemNummer, R.id.itemInnhold, R.id.itemSted});
                ListView lv = getListView();
                

                // updating listview
                setListAdapter(adapter);

                EditText inputSearch = (EditText) findViewById(R.id.search);
                
                inputSearch.addTextChangedListener(new TextWatcher() 
                {

                    @Override
                    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) 
                    {
                        // When user changed the Text
                        ((SimpleAdapter)MainActivity.this.adapter).getFilter().filter(cs); 
                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                            int arg3) {
                        // TODO Auto-generated method stub


                    }

                    @Override
                    public void afterTextChanged(Editable arg0) {
                        // TODO Auto-generated method stub

                    }
                });
            }
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        // Inflate the menu; this adds items to the action bar if it is present.
    	getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) 
        {
        	AlertDialog.Builder innleggingOK = new AlertDialog.Builder(MainActivity.this);
			innleggingOK
			.setTitle(R.string.action_settings)
			.setMessage("Laget av Lars-Erik Holte")
			.setCancelable(false)
			.setNeutralButton("Ok", new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					dialog.cancel();
				}
			});
			innleggingOK.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
