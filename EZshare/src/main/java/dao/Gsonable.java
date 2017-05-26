package dao;

import com.google.gson.Gson;


/*
 * the abstract class to show the class can be changed to json format
 */
public abstract class Gsonable {
	public String toJson(Gson gson) {
		return null;
	}
}
