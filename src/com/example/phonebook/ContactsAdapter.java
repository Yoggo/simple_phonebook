package com.example.phonebook;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ContactsAdapter extends CursorAdapter{
	private LayoutInflater inflater;
	private boolean isDelete;
	ArrayList<String> idToDelete;
	private Context context;
	private String id;
	
	String manColor;
	String womanColor;
	
	public ContactsAdapter(Context context, Cursor c){
		super(context,c);
		this.context = context;
		inflater = LayoutInflater.from(context);
		idToDelete = new ArrayList<String>();
	}
	
	public void setManColor(String man){
		this.manColor = man;
	}
	
	public void setWomanColor(String woman){
		this.womanColor = woman;
	}
	
	public void setDeleteMode(boolean isDelete){
		this.isDelete = isDelete;
	}
	
	@Override
	public void bindView(View v, final Context context, final Cursor cursor){
		TextView firstNameTextView = (TextView) v.findViewById(R.id.firstNameTextView);
		TextView lastNameTextView = (TextView) v.findViewById(R.id.lastNameTextView);
		CheckBox toDeleteCheckbox = (CheckBox) v.findViewById(R.id.toDeleteCheckBox);
		ImageView avatarImageView = (ImageView) v.findViewById(R.id.contact_avatar_ImageView);
		
		v.setTag(cursor.getString(0));
		firstNameTextView.setText(cursor.getString(1));
		lastNameTextView.setText(cursor.getString(2));
		//checking color for man
		if(cursor.getString(cursor.getColumnIndex(DBHelper.GENDER)).equals(DBHelper.GENDER_MAN)){
			if(manColor != null && !manColor.equals("")){
				firstNameTextView.setTextColor(Color.parseColor(manColor));
				lastNameTextView.setTextColor(Color.parseColor(manColor));
			}
		//checking color for woman
		} else if(cursor.getString(cursor.getColumnIndex(DBHelper.GENDER)).equals(DBHelper.GENDER_WOMAN)){
			if(womanColor != null && !womanColor.equals("")){
				firstNameTextView.setTextColor(Color.parseColor(womanColor));
				lastNameTextView.setTextColor(Color.parseColor(womanColor));
			}
		}

		toDeleteCheckbox.setTag(cursor.getString(0));
		toDeleteCheckbox.setChecked(false);
		if(isDelete){
			toDeleteCheckbox.setVisibility(View.VISIBLE);
			toDeleteCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if(isChecked){
						String id = (String) buttonView.getTag();
						idToDelete.add(id);
					}else {
						String id = (String) buttonView.getTag();
						idToDelete.remove(id);
					}
					
				}
				
			});
		} else{
			toDeleteCheckbox.setVisibility(View.INVISIBLE);
		}
		
		if(cursor.getString(cursor.getColumnIndex(DBHelper.AVATAR_URL))!=null){
			Bitmap yourSelectedImage = BitmapFactory.decodeFile(cursor.getString(cursor.getColumnIndex(DBHelper.AVATAR_URL)));
			if(yourSelectedImage !=null){
				avatarImageView.setImageBitmap(Bitmap.createScaledBitmap(yourSelectedImage, 160, 120, false));
			} else{
				avatarImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.abc_ab_solid_dark_holo));
			}
		}
		
		v.setOnClickListener(new OnClickListener(){
			
			
			@Override
			public void onClick(View v){
				Intent intent = new Intent(context.getApplicationContext(), ViewContact.class);
				intent.putExtra(DBHelper.ID, String.valueOf(v.getTag()));
				context.startActivity(intent);
				
			}
		});
	}
	
	public ArrayList<String> getListIdToDelete(){
		return idToDelete;
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent){
		return inflater.inflate(R.layout.contact, parent, false);
	}
}
