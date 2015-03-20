package org.integration.persistance.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.integration.persistance.IPersist;

public class PersistData implements IPersist {
	
	private static PersistData persistData = new PersistData();
	private List<String> names = new ArrayList<String>();
	
	public static PersistData getPersistDataInstance(){
		return persistData;
	}
	
	private PersistData(){
		
	}

	public void insert(String name) throws Exception{
		names.add(name);
	}
	
	public void delete(String name) throws Exception {
		names.remove(name);
	}
	
	public List<String> getUsers() throws Exception {
		List<String> newList = new ArrayList<String>(names);
		return newList;
	}
}
