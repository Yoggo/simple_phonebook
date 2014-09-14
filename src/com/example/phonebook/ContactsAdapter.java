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
import android.widget.Toast;

public class ContactsAdapter extends CursorAdapter{

	private final int KEY_ID = 1;

	private LayoutInflater inflater;
	private boolean isDelete;
	private ArrayList<String> idToDelete;
	private Context context;
	private Cursor cursor;
	
	private String manColor;
	private String womanColor;
	
	public ContactsAdapter(Context context, Cursor c){
		super(context,c);
		this.context = context;
		this.cursor = c;
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
	public View getView(int position, View v, ViewGroup parent) {
		View view;
		cursor.moveToPosition(position);
		if(v == null){
			view = newView(context, cursor, parent);
		} else{
			view = v;
		}
		bindView(view, context, cursor);
		return view;
	}
	
	@Override
	public void bindView(View v, final Context context, final Cursor cursor){
		
		ViewHolder viewHolder = (ViewHolder) v.getTag();
		viewHolder.id = cursor.getString(0);
		viewHolder.firstNameTextView.setText(cursor.getString(1));
		viewHolder.lastNameTextView.setText(cursor.getString(2));
		//checking color for man
		if(cursor.getString(cursor.getColumnIndex(DBHelper.GENDER)).equals(DBHelper.GENDER_MAN)){
			if(manColor != null && !manColor.equals("")){
				viewHolder.firstNameTextView.setTextColor(Color.parseColor(manColor));
				viewHolder.lastNameTextView.setTextColor(Color.parseColor(manColor));
			}
		//checking color for woman
		} else if(cursor.getString(cursor.getColumnIndex(DBHelper.GENDER)).equals(DBHelper.GENDER_WOMAN)){
			if(womanColor != null && !womanColor.equals("")){
				viewHolder.firstNameTextView.setTextColor(Color.parseColor(womanColor));
				viewHolder.lastNameTextView.setTextColor(Color.parseColor(womanColor));
			}
		}

		viewHolder.toDeleteCheckbox.setTag(cursor.getString(0));
		viewHolder.toDeleteCheckbox.setChecked(false);
		if(isDelete){
			viewHolder.toDeleteCheckbox.setVisibility(View.VISIBLE);
			viewHolder.toDeleteCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

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
			viewHolder.toDeleteCheckbox.setVisibility(View.INVISIBLE);
		}
		
		if(cursor.getString(cursor.getColumnIndex(DBHelper.AVATAR_URL))!=null){
			Bitmap yourSelectedImage = BitmapFactory.decodeFile(cursor.getString(cursor.getColumnIndex(DBHelper.AVATAR_URL)));
			if(yourSelectedImage !=null){
				viewHolder.avatarImageView.setImageBitmap(Bitmap.createScaledBitmap(yourSelectedImage, 160, 120, false));
			} else{
				viewHolder.avatarImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.abc_ab_bottom_solid_dark_holo));
			}
		}
		
		v.setOnClickListener(new OnClickListener(){
			
			
			@Override
			public void onClick(View v){
				Intent intent = new Intent(context.getApplicationContext(), ViewContact.class);
				ViewHolder viewHolder = (ViewHolder) v.getTag();
				intent.putExtra(DBHelper.ID, String.valueOf(viewHolder.id));
				context.startActivity(intent);
				
			}
		});
	}
	
	public ArrayList<String> getListIdToDelete(){
		return idToDelete;
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent){
		
		ViewHolder viewHolder = new ViewHolder();
		View view = inflater.inflate(R.layout.contact, parent, false);
		
		viewHolder.firstNameTextView = (TextView) view.findViewById(R.id.firstNameTextView);
		viewHolder.lastNameTextView = (TextView) view.findViewById(R.id.lastNameTextView);
		viewHolder.toDeleteCheckbox = (CheckBox) view.findViewById(R.id.toDeleteCheckBox);
		viewHolder.avatarImageView = (ImageView) view.findViewById(R.id.contact_avatar_ImageView);
		
		view.setTag(viewHolder);
		
		return view;
	}
	
	class ViewHolder {
		String id;
		TextView firstNameTextView;
		TextView lastNameTextView;
		CheckBox toDeleteCheckbox;
		ImageView avatarImageView;
	}
}
