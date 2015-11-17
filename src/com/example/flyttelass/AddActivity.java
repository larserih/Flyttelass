package com.example.flyttelass;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class AddActivity extends Activity
{
	DB db;
	protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        db = new DB(this);
        
        ImageButton settinn = (ImageButton)findViewById(R.id.save);
        final EditText nummerfelt = (EditText) findViewById(R.id.nummerfield);
        final EditText innholdfelt = (EditText) findViewById(R.id.innholdfield);
        final EditText stedfelt = (EditText) findViewById(R.id.stedfield);
        
        final ImageView failed1 = (ImageView) findViewById(R.id.failed1);
        final ImageView failed2 = (ImageView) findViewById(R.id.failed2);
        final ImageView failed3 = (ImageView) findViewById(R.id.failed3);
        
        final ImageView approved1 = (ImageView) findViewById(R.id.approved1);
        approved1.setVisibility(View.GONE);
        
        final ImageView approved2 = (ImageView) findViewById(R.id.approved2);
        approved2.setVisibility(View.GONE);
        
        final ImageView approved3 = (ImageView) findViewById(R.id.approved3);
        approved3.setVisibility(View.GONE);
        
        nummerfelt.addTextChangedListener(new TextWatcher()
        {
        	@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) 
			{}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{}
			@Override
			public void afterTextChanged(Editable s) 
			{
				if(!sjekkTom(nummerfelt))
				{
					approved1.setVisibility(View.VISIBLE);
					failed1.setVisibility(View.GONE);
				}
				else
				{
					approved1.setVisibility(View.GONE);
					failed1.setVisibility(View.VISIBLE);
				}
					
			}
        });
        
        innholdfelt.addTextChangedListener(new TextWatcher()
        {
        	@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) 
			{}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{}
			@Override
			public void afterTextChanged(Editable s) 
			{
				if(!sjekkTom(innholdfelt))
				{
					approved2.setVisibility(View.VISIBLE);
					failed2.setVisibility(View.GONE);
				}
				else
				{
					approved2.setVisibility(View.GONE);
					failed2.setVisibility(View.VISIBLE);
				}
			}
        });
        
        stedfelt.addTextChangedListener(new TextWatcher()
        {
        	@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) 
			{}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{}
			@Override
			public void afterTextChanged(Editable s) 
			{
				if(!sjekkTom(stedfelt))
				{
					approved3.setVisibility(View.VISIBLE);
					failed3.setVisibility(View.GONE);
				}
				else
				{
					approved3.setVisibility(View.GONE);
					failed3.setVisibility(View.VISIBLE);
				}
			}
        });
        if(!sjekkTom(innholdfelt))
		{
        	approved2.setVisibility(View.VISIBLE);
		}
        if(!sjekkTom(stedfelt))
		{
        	approved3.setVisibility(View.VISIBLE);
		}
        
        settinn.setOnClickListener(new OnClickListener()
        {
			public void onClick(View v) 
			{
				int nummer = -1;
				if(!sjekkTom(nummerfelt))
				{
					nummer = Integer.parseInt(nummerfelt.getText().toString());
				}
				String innhold = innholdfelt.getText().toString();
				String sted = stedfelt.getText().toString();
				List<Boks> boksene;
				boksene = db.visAlle();
				try
				{
					if(!sjekkTom(nummerfelt) && !sjekkTom(innholdfelt) && !sjekkTom(stedfelt))
					{
						if(finnesBoksen(boksene, nummer))
						{
							db.settInn(new Boks(nummer, innhold, sted));
							
							AlertDialog.Builder innleggingOK = new AlertDialog.Builder(AddActivity.this);
							innleggingOK
							.setTitle("Ok")
							.setMessage("Boks nr " + nummer + " lagt til.")
							.setCancelable(false)
							.setPositiveButton("Legg til", new DialogInterface.OnClickListener() 
							{
								@Override
								public void onClick(DialogInterface dialog, int which) 
								{
									nummerfelt.setText("");
									innholdfelt.setText("");
									stedfelt.setText("");
									dialog.cancel();
									Intent language = new Intent(AddActivity.this,AddActivity.class);
						            finish();
						            overridePendingTransition(0, 0);
						            startActivity(language);
								}
							})
							.setNegativeButton("Ferdig", new DialogInterface.OnClickListener() 
							{
								@Override
								public void onClick(DialogInterface dialog, int which) 
								{
									Intent language = new Intent(AddActivity.this,MainActivity.class);
						            finish();
						            overridePendingTransition(0, 0);
						            startActivity(language);
								}
							});
							innleggingOK.show();
						}
						else
						{
							AlertDialog.Builder boksFinnes = new AlertDialog.Builder(AddActivity.this);
							boksFinnes
							.setTitle("Feil")
							.setMessage("Boks nr " + nummer + " finnes allerede.")
							.setCancelable(false)
							.setNeutralButton("Ok", new DialogInterface.OnClickListener() 
							{
								@Override
								public void onClick(DialogInterface dialog, int which) 
								{
									nummerfelt.setText("");
									dialog.cancel();
								}
							});
							boksFinnes.show();
						}
					}
					else
					{		
						AlertDialog.Builder innleggingIkkeOK = new AlertDialog.Builder(AddActivity.this);
						innleggingIkkeOK
						.setTitle("Feil")
						.setMessage("Fyll inn alle felt") // Fiks string
						.setCancelable(false)
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								dialog.cancel();
							}
						});
						innleggingIkkeOK.show();
					}
				}
				catch(SQLiteException e)
				{
					Toast toast = Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT);
					toast.show();
				}
			}
        });
    }
	public static boolean sjekkTom(EditText text)
	{
		return text.getText().toString().trim().length() == 0;
	}
	public static boolean finnesBoksen(List<Boks> boks, int nr)
	{
		for(int i = 0; i < boks.size(); i++)
		{
			if(boks.get(i).getNummer() == nr)
			{
				return false;
			}
		}
		return true;
	}
}
