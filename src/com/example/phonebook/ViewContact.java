package com.example.phonebook;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Build;

public class ViewContact extends ActionBarActivity implements View.OnClickListener{
	
	String id;
	DBHelper dbHelper;
	
	ImageView view_contact_avatar;
	TextView firstNameTextView;
	TextView lastNameTextView;
	TextView dateOfBirthTextView;
	TextView genderTextView;
	TextView addressTextView;
	
	Button backButton, editButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_contact);
		dbHelper = new DBHelper(this);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		//get id contact to view info
		id = extras.getString(DBHelper.ID);
		findViews();
		bindViews();
		
	}
	
	public void findViews(){
		view_contact_avatar = (ImageView) findViewById(R.id.view_contact_avatar);
		firstNameTextView = (TextView) findViewById(R.id.view_contact_first_name);
		lastNameTextView = (TextView) findViewById(R.id.view_contact_last_name);
		dateOfBirthTextView = (TextView) findViewById(R.id.view_contact_date_of_birth);
		genderTextView = (TextView) findViewById(R.id.view_contact_gender);
		addressTextView = (TextView) findViewById(R.id.view_contact_address);
		
		backButton = (Button) findViewById(R.id.view_contact_back_button);
		backButton.setOnClickListener(this);
		editButton = (Button) findViewById(R.id.view_contact_edit_button);
		editButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v){
		switch(v.getId()){
		case R.id.view_contact_back_button:
			Intent toMainActivity = new Intent(ViewContact.this, MainActivity.class);
			toMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(toMainActivity);
			break;
		case R.id.view_contact_edit_button:
			Intent toAddContact = new Intent(ViewContact.this, AddContact.class);
			toAddContact.putExtra(DBHelper.ID, id);
			startActivity(toAddContact);
			break;
		}
	}
	
	@Override
	public void onBackPressed(){
		Intent toMainActivity = new Intent(ViewContact.this, MainActivity.class);
		toMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(toMainActivity);
	}
	
	public void bindViews(){
		Cursor cursor = dbHelper.getInfoById(id);
		
		if(cursor.getString(cursor.getColumnIndex(DBHelper.AVATAR_URL))!=null){
			Bitmap yourSelectedImage = BitmapFactory.decodeFile(cursor.getString(cursor.getColumnIndex("avatar_url")));
			if(yourSelectedImage !=null){
				view_contact_avatar.setImageBitmap(yourSelectedImage);
			}
		}
		
		firstNameTextView.setText(cursor.getString(cursor.getColumnIndex(DBHelper.FIRST_NAME)));
		lastNameTextView.setText(cursor.getString(cursor.getColumnIndex(DBHelper.LAST_NAME)));
		dateOfBirthTextView.setText(cursor.getString(cursor.getColumnIndex(DBHelper.DATE_OF_BIRTH)));
		
		if(cursor.getString(cursor.getColumnIndex(DBHelper.GENDER)).equals(DBHelper.GENDER_MAN)){
			genderTextView.setText(getResources().getString(R.string.man));
		} else{
			genderTextView.setText(getResources().getString(R.string.woman));
		}
		addressTextView.setText(cursor.getString(cursor.getColumnIndex(DBHelper.ADDRESS)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_contact, menu);
		return true;
	}

}
