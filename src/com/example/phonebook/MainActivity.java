package com.example.phonebook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
	DBHelper dbHelper;
	ListView contactsListView;
	ContactsAdapter contactsAdapter;
	ArrayList<String> listIdToDelete;
	Menu menu;
	SharedPreferences settings;
	
	String genderFilter;
	String genderSelect;
	String manColor;
	String womanColor;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        
       
        setContentView(R.layout.main_activity);
        
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        
        contactsListView = (ListView) findViewById(R.id.contactsListView);
        
        dbHelper = new DBHelper(this);
        
        //SQLiteDatabase db = dbHelper.getWritableDatabase();
       
        initList();

        
    }
    
    public void refreshDB(){
    	dbHelper = new DBHelper(this);
    }
    
    public void initList(){
	Cursor cursor;
	if(genderSelect != null && !genderSelect.equals("")){
		cursor = dbHelper.getContactsByGender(genderSelect);
	} else{
		cursor = dbHelper.getAllContacts();
	}

	//contactsAdapter = new SimpleCursorAdapter(this, R.layout.contact,cursor,columns,to,0);
	contactsAdapter = new ContactsAdapter(this, cursor);
	if(manColor != null && !manColor.equals("")){
		contactsAdapter.setManColor(manColor);
	}
	if(womanColor != null && !womanColor.equals("")){
		contactsAdapter.setWomanColor(womanColor);
	}
	contactsListView.setAdapter(contactsAdapter);
    }
    
    public void refreshList(){
        contactsAdapter.setDeleteMode(false);
      	 contactsAdapter.notifyDataSetChanged();
    }
    
    public void toDelete(){
     contactsAdapter.setDeleteMode(true);
   	 contactsAdapter.notifyDataSetChanged();
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	genderFilter = settings.getString("gender_select", "");
    	if(!genderFilter.equals("")){
    	   if(genderFilter.equals(getResources().getStringArray(R.array.gender_select_array)[0])){
    		   genderSelect = DBHelper.GENDER_MAN;
    	   } else if(genderFilter.equals(genderFilter.equals(getResources().getStringArray(R.array.gender_select_array)[0]))){
    		   genderSelect = DBHelper.GENDER_WOMAN;
    	   }else{
    		   genderSelect = "";
    	   }
    	}
    	manColor = settings.getString("man_color", "");
    	womanColor = settings.getString("woman_color", "");
    	initList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void deleteContacts(){
    	listIdToDelete = contactsAdapter.getListIdToDelete();
    	if(listIdToDelete.size()>0){
    		for(int i = 0; i<listIdToDelete.size(); i++){
        		dbHelper.deleteContact(listIdToDelete.get(i));
        	}
    		listIdToDelete.clear();
    		menu.clear();
        	getMenuInflater().inflate(R.menu.main, menu);
    		initList();
    	} else{
    		getMenuInflater().inflate(R.menu.main_delete, menu);
        	toDelete();
    	}
    	
    	
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
        case R.id.action_add:
        	Intent intent = new Intent(MainActivity.this, AddContact.class);
        	startActivity(intent);
        	break;
        case R.id.action_delete:
        	menu.clear();
        	deleteContacts();
        	break;
        case R.id.action_cancel:
        	menu.clear();
        	getMenuInflater().inflate(R.menu.main, menu);
        	refreshList();
        	listIdToDelete.clear();
        	break;
        case R.id.action_settings:
        	Intent toSettings = new Intent(MainActivity.this, Settings.class);
        	startActivity(toSettings);
        	break;
        case R.id.action_import_export:
        	registerForContextMenu(contactsListView);
        	openContextMenu(contactsListView);
        	break;
        }

        return super.onOptionsItemSelected(item);
    }
    
    /*@Override
    public void onBackPressed(){
        
    }*/
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
        ContextMenuInfo menuInfo) {
      menu.add(0, 0, 0, getResources().getString(R.string.action_import));
      menu.add(0, 0, 0, getResources().getString(R.string.action_export));
    }
    
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		   if (item.getTitle() == getResources().getString(R.string.action_export)) {
			   XMLOperations xmlOperations = new XMLOperations();
			   xmlOperations.setOperationType("export");
			   xmlOperations.execute();
			   return true;
			   
		   } else if(item.getTitle() == getResources().getString(R.string.action_import)){
			   XMLOperations xmlOperations = new XMLOperations();
			   xmlOperations.setOperationType("import");
			   xmlOperations.execute();

			   return true;
		   }
		   return false;
	}
	
	class XMLOperations extends AsyncTask<Void, Void, Void>{
		
		private String operationType;
		
		public void setOperationType(String operation){
			this.operationType = operation;
		}
		
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void...params){
			if(operationType.equals("export")){
				XMLHelper xmlHelper = new XMLHelper();
				xmlHelper.exportDatabase(dbHelper);
			} else if(operationType.equals("import")){
				XMLHelper xmlHelper = new XMLHelper();
				xmlHelper.importDatabase(dbHelper);

			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			   refreshDB();
			   initList();
			Toast.makeText(getApplicationContext(), "Operation was ended!", Toast.LENGTH_SHORT).show();
		}
	}


}
