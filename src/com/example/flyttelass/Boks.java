package com.example.flyttelass;

public class Boks 
{
	int _id;
	int _nr;
	String _innhold;
	String _sted;
	
	public Boks()
	{}
	
	public Boks(int nr, String innhold, String sted)
	{
		this._nr = nr;
		this._innhold = innhold;
		this._sted = sted;
	}
	
	//Get og set-metoder for Person
	public int getID()
	{
		return this._id;
	}
	public void setID(int id)
	{
		this._id = id;
	}
	
	
	public int getNummer()
	{
		return this._nr;
	}
	public void setNummer(int nummer)
	{
		this._nr = nummer;
	}
	
	public String getInnhold()
	{
		return this._innhold;
	}
	public void setInnhold(String innhold)
	{
		this._innhold = innhold;
	}
	
	public String getSted()
	{
		return this._sted;
	}
	public void setSted(String sted)
	{
		this._sted = sted;
	}
	
}
