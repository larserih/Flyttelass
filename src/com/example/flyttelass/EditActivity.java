package com.example.flyttelass;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditActivity extends Activity
{
	Button editBtn;
    DB db;
    Boks boks;
		@Override
		 protected void onCreate(Bundle savedInstanceState) 
		    {
		        super.onCreate(savedInstanceState);
		        setContentView(R.layout.activity_edit);
		        db = new DB(this);

		        final EditText nummer = (EditText) findViewById(R.id.editNummer);
		        final EditText innhold = (EditText) findViewById(R.id.editInnhold);
		        final EditText sted = (EditText) findViewById(R.id.editSted);
		        
		        Intent intent = getIntent();
				String nr = intent.getStringExtra("nummer");
				nummer.setText(nr);
				String innholdet = intent.getStringExtra("innhold");
				innhold.setText(innholdet);
				String stedet = intent.getStringExtra("sted");
				sted.setText(stedet);
				
				editBtn = (Button)findViewById(R.id.edit);
		        editBtn.setOnClickListener(new View.OnClickListener() 
		        {	
					@Override
					public void onClick(View v) 
					{
						Intent intent = getIntent();
						String num = nummer.getText().toString();
						
						int theid = intent.getIntExtra("id", 0);
						int nr = -1;
						if(!sjekkTom(nummer))
						{
							nr = Integer.parseInt(num);
						}
						
						String ih = innhold.getText().toString();
						String sd = sted.getText().toString();
						
						List<Boks> boksene;
						boksene = db.visAlle();

						try
						{
							if(!sjekkTom(nummer) && !sjekkTom(innhold) && !sjekkTom(sted))
							{
								if(finnesBoksen(boksene, nr, theid))
								{
									db = new DB(v.getContext());
									db.oppdater(theid, nr, ih, sd);
									AlertDialog.Builder innleggingOK = new AlertDialog.Builder(EditActivity.this);
									innleggingOK
									.setTitle("Ok")
									.setMessage("Boksen ble endret")
									.setCancelable(false)
									.setNeutralButton("Ok", new DialogInterface.OnClickListener() 
									{
										@Override
										public void onClick(DialogInterface dialog, int which) 
										{
											Intent i = new Intent(EditActivity.this,MainActivity.class);
								            finish();
								            overridePendingTransition(0, 0);
								            startActivity(i);
										}
									})
									.show();
								}
								else
								{
									AlertDialog.Builder boksFinnes = new AlertDialog.Builder(EditActivity.this);
									boksFinnes
									.setTitle("Feil")
									.setMessage("Boks nr " + nr + " finnes allerede.")
									.setCancelable(false)
									.setNeutralButton("Ok", new DialogInterface.OnClickListener() 
									{
										@Override
										public void onClick(DialogInterface dialog, int which) 
										{
											dialog.cancel();
										}
									});
									boksFinnes.show();
								}
							}	
							else
							{		
								AlertDialog.Builder innleggingIkkeOK = new AlertDialog.Builder(EditActivity.this);
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
	public static boolean finnesBoksen(List<Boks> boks, int nr, int id)
	{
		for(int i = 0; i < boks.size(); i++)
		{
			if(boks.get(i).getNummer() == nr && boks.get(i).getID() != id)
			{
				return false;
			}
		}
		return true;
	}
}


