package com.example.phonebook;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
	
	public static final String CONTACT_TABLE_NAME = "contacts";
	
	//table column constants
	public static final String ID = "_id";
	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String DATE_OF_BIRTH = "date_of_birth";
	public static final String GENDER = "gender";
	public static final String GENDER_MAN = "man";
	public static final String GENDER_WOMAN = "woman";
	public static final String ADDRESS = "address";
	public static final String AVATAR_URL = "avatar_url";
	
	SQLiteDatabase db;
	
	public DBHelper(Context context){
		super(context, "phonebookDB", null, 1);
		db = this.getWritableDatabase();
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
		String sql = "CREATE TABLE IF NOT EXISTS "+ CONTACT_TABLE_NAME + "("
				+ ID +" integer primary key autoincrement,"
				+ FIRST_NAME +" text,"
				+ LAST_NAME + " text,"
				+ DATE_OF_BIRTH + " text,"
				+ GENDER +" text,"
				+ ADDRESS +" text,"
				+ AVATAR_URL +" text);";
		DatabaseUtils.sqlEscapeString(sql);
		db.execSQL(sql);
		
	}
	
	public Cursor getContactsByGender(String gender){
		Cursor cursor = db.query(DBHelper.CONTACT_TABLE_NAME, new String[] {ID,FIRST_NAME, LAST_NAME, DATE_OF_BIRTH,				
				GENDER, ADDRESS,AVATAR_URL},
				" gender = '" + gender + "'", null, null, null, null);
		if(cursor!=null){
			cursor.moveToFirst();
		}
		return cursor;
	}

	public Cursor getAllContacts(){
		Cursor cursor = db.query(DBHelper.CONTACT_TABLE_NAME, new String[] {ID,FIRST_NAME, LAST_NAME, DATE_OF_BIRTH,				
				GENDER, ADDRESS,AVATAR_URL},
				null, null, null, null, null);
		if(cursor!=null){
			cursor.moveToFirst();
		}
		return cursor;
	}
	
	//get all info about contact by id
	public Cursor getInfoById(String id){
		String sql = "SELECT * FROM "+ CONTACT_TABLE_NAME +" WHERE "+ ID + " = '"+ id +"'";
		DatabaseUtils.sqlEscapeString(sql);
		Cursor cursor = db.rawQuery(sql, null);
		if(cursor !=null){
			cursor.moveToFirst();
		}
		return cursor;
	}
	
	
	public void insertContact(String firstName, String lastName, String dateOfBirth,
			                  String gender, String address, String avatar_url){
		String sql = "INSERT INTO "+ CONTACT_TABLE_NAME +" (" + FIRST_NAME +","
				+ LAST_NAME + ","
				+ DATE_OF_BIRTH + ","
				+ GENDER + ","
				+ ADDRESS + ","
				+ AVATAR_URL + ") VALUES ('"+ firstName +"','"+ lastName +"','"
						+ dateOfBirth + "','" + gender + "','" + address + "','" 
				+ avatar_url + "')";
		DatabaseUtils.sqlEscapeString(sql);
		db.execSQL(sql);
	}
	
	public void updateContact(String id, String firstName, String lastName, String dateOfBirth,
            String gender, String address, String avatar_uri){
		db.execSQL("UPDATE "+ CONTACT_TABLE_NAME + " SET " + FIRST_NAME +" = '"+ firstName
				+ "', " + LAST_NAME + " = '" + lastName + "',"
				+ DATE_OF_BIRTH + " = '"+ dateOfBirth
				+ "', " + GENDER + " = '" + gender
				+ "', " + ADDRESS + " = '" + address
				+ "', " + AVATAR_URL + " = '" + avatar_uri + "' WHERE " + ID +" = '" + id + "'");
	}
	
	public int countContacts(){
		String sql = "SELECT COUNT(*) FROM " + CONTACT_TABLE_NAME;
		DatabaseUtils.sqlEscapeString(sql);
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		return count;
	}
	
	public void deleteContact(String id){
		String sql = "DELETE FROM " + CONTACT_TABLE_NAME + " WHERE _id = '"+ id +"'";
		DatabaseUtils.sqlEscapeString(sql);
		db.execSQL(sql);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
