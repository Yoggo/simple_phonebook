package com.example.phonebook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class AddContact extends ActionBarActivity {
	
	final private int SELECT_PHOTO = 1;
	final private int TAKE_PHOTO = 2;
	
	private String id;
	private boolean isEdit;
	private String avatar_uri;
	private EditText firstNameEditText, lastNameEditText, dateOfBirthEditText,
	         addressEditText;
	private ImageView avatarImageView;
	private Spinner genderSpinner;
	private String gender;
	private Button addButton;
	private DBHelper dbHelper;
    private Activity activity;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_contact_activity);
		activity = this;
		dbHelper = new DBHelper(this);
		
        findViews();
        
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.gender_array,
				R.layout.spinner_item);
		adapter.setDropDownViewResource(R.layout.spinner_item);

		genderSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				switch(position){
				case 0:
					gender = DBHelper.GENDER_MAN;
					break;
				case 1:
					gender = DBHelper.GENDER_WOMAN;
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				gender = "";
				
			}

		});
		genderSpinner.setAdapter(adapter);
		
		addButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if(isEdit){
					dbHelper.updateContact(id,
							firstNameEditText.getText().toString(),
							lastNameEditText.getText().toString(),
							dateOfBirthEditText.getText().toString(),
							gender,
							addressEditText.getText().toString(),
							avatar_uri);
					Intent intent = new Intent(AddContact.this, ViewContact.class);
					intent.putExtra(DBHelper.ID, id);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}else{
					if(isFillFields()){
						dbHelper.insertContact(firstNameEditText.getText().toString(),
								lastNameEditText.getText().toString(),
								dateOfBirthEditText.getText().toString(),
								gender,
								addressEditText.getText().toString(),
								avatar_uri);
					} else{
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.warning_fill), Toast.LENGTH_SHORT).show();
						return;
					}
					
				}
				
				finish();
			}
		});
		
		activity.registerForContextMenu(avatarImageView);
		
		avatarImageView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				
				activity.openContextMenu(avatarImageView);
			}
		});
		//method for checking of editing this contact
		checkIsEdit();
		
	}
	
	public boolean isFillFields(){
		if(firstNameEditText.getText().toString().trim().equals("")
			|| lastNameEditText.getText().toString().trim().equals("")){
			return false;
		} else{
			return true;
		}
		
	}
	
	//method for checking of editing this contact
	public void checkIsEdit(){
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if(extras != null){
			id = extras.getString(DBHelper.ID);
			if(id !=null){
				isEdit =true;
			}
		}
		
		//if this contact is editing than fill of fields
		if(isEdit){
			getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_edit_contact));
			Cursor cursor = dbHelper.getInfoById(id);
			if(cursor.getString(cursor.getColumnIndex(DBHelper.AVATAR_URL))!=null){
				Bitmap yourSelectedImage = BitmapFactory.decodeFile(cursor.getString(cursor.getColumnIndex("avatar_url")));
				if(yourSelectedImage !=null){
					avatarImageView.setImageBitmap(yourSelectedImage);
				}
			}
			
			firstNameEditText.setText(cursor.getString(cursor.getColumnIndex(DBHelper.FIRST_NAME)));
			lastNameEditText.setText(cursor.getString(cursor.getColumnIndex(DBHelper.LAST_NAME)));
			dateOfBirthEditText.setText(cursor.getString(cursor.getColumnIndex(DBHelper.DATE_OF_BIRTH)));
			if(cursor.getString(cursor.getColumnIndex("gender")).equals(DBHelper.GENDER_MAN)){
				genderSpinner.setSelection(0);
			} else{
				genderSpinner.setSelection(1);
			}
			addressEditText.setText(cursor.getString(cursor.getColumnIndex(DBHelper.ADDRESS)));
			avatar_uri = cursor.getString(cursor.getColumnIndex(DBHelper.AVATAR_URL));
		}
		
	}
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v,
        ContextMenuInfo menuInfo) {
      menu.add(0, 0, 0, getResources().getString(R.string.loadFromSD));
      menu.add(0, 0, 0, getResources().getString(R.string.getPhoto));
      menu.add(0, 0, 0, getResources().getString(R.string.loadFromNet));
 
    }
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		   if (item.getTitle() == getResources().getString(R.string.loadFromSD)) {
			   //loading photo from SD
			   Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			   photoPickerIntent.setType("image/*");
			   startActivityForResult(photoPickerIntent, SELECT_PHOTO);    
		   } else if(item.getTitle() == getResources().getString(R.string.getPhoto)){
			   //take a photo from camera
			   Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
               startActivityForResult(cameraIntent, TAKE_PHOTO); 
		   } else if(item.getTitle() == getResources().getString(R.string.loadFromNet)){
			   DownloadFromNet download = new DownloadFromNet();
			   download.execute();
		   }
		   else {
		      return false;
		   }
		   return true;
	}
	
	class DownloadFromNet extends AsyncTask <Void, Void, Void>{

		String error;
		
		@Override
		protected void onPostExecute(Void result) {
			if(avatar_uri !=null && !avatar_uri.equals("")){
				Bitmap yourSelectedImage = BitmapFactory.decodeFile(avatar_uri);
	            avatarImageView.setImageBitmap(yourSelectedImage);
			}else if(error != null){
	            	Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
	            }
			
			super.onPostExecute(result);
		}

		@Override
		protected Void doInBackground(Void... params) {
			InputStream input = null;
			try {
				URL url = new URL (getResources().getString(R.string.server_to_download_image));
				input = url.openStream();
				String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    	    String imageFileName = "JPEG_" + timeStamp + ".gif";
	    	    File storageDir = Environment.getExternalStoragePublicDirectory(
	    	            Environment.DIRECTORY_DCIM);
	    	    File image = (new File(storageDir,imageFileName));
			    OutputStream output = new FileOutputStream(image);
			    avatar_uri = image.getAbsolutePath();
			    try {
			        byte[] buffer = new byte[1024];
			        int bytesRead = 0;
			        while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
			            output.write(buffer, 0, bytesRead);
			        }
			    } finally {
			        output.close();
			    }
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			    try {
					input.close();
				} catch (Exception e) {
					error = getResources().getString(R.string.warning_internet);
					e.printStackTrace();
				}
			}
			return null;
		}
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, 
		       Intent imageReturnedIntent) {
		    super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

		    switch(requestCode) { 
		    case SELECT_PHOTO:
		        if(resultCode == RESULT_OK){  
		            Uri selectedImage = imageReturnedIntent.getData();
		            String[] filePathColumn = {MediaStore.Images.Media.DATA};
		            
		            Cursor cursor = getContentResolver().query(
		                               selectedImage, filePathColumn, null, null, null);
		            cursor.moveToFirst();

		            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		            String filePath = cursor.getString(columnIndex);
		            cursor.close();

		            avatar_uri = filePath;
		            Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
		            avatarImageView.setImageBitmap(yourSelectedImage);
		            break;
		        }
		    case TAKE_PHOTO:
		    	if(resultCode == RESULT_OK){
		    		Bitmap yourSelectedImage = (Bitmap) imageReturnedIntent.getExtras().get("data");
		    		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		    	    String imageFileName = "JPEG_" + timeStamp + "_";
		    	    File storageDir = Environment.getExternalStoragePublicDirectory(
		    	            Environment.DIRECTORY_DCIM);
		    	    File image = new File(storageDir, imageFileName);
		    	    try {
		    	        FileOutputStream out = new FileOutputStream(image);
		    	        yourSelectedImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
		    	        out.flush();
		    	        out.close();

		    	    } catch (Exception e) {
		    	        e.printStackTrace();
		    	    }
		    	    avatar_uri = image.getAbsolutePath();
		    		avatarImageView.setImageBitmap(yourSelectedImage);
		    		break;
		    	}
		    default: break;
		    }
		} 
	
	private void findViews(){
		firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
		lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
		dateOfBirthEditText = (EditText) findViewById(R.id.dateOfBirthEditText);
		genderSpinner = (Spinner) findViewById(R.id.genderSpinner);
		addressEditText =(EditText) findViewById(R.id.addressEditText);
		addButton = (Button) findViewById(R.id.addButton);
		avatarImageView = (ImageView) findViewById(R.id.avatarImageView);
	}



}
