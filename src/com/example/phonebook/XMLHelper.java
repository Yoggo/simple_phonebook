package com.example.phonebook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.ContentHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;

import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

public class XMLHelper {
	   //list of tags
	   public final String contactsListTag = "<CONTACTS_LIST>";
	   public final String contactsListTagEnd = "</CONTACTS_LIST>";
	   public final String contactTag = "<CONTACT>";
	   public final String contactTagEnd = "</CONTACT>";
	   public final String idTag = "<ID>";
	   public final String idTagEnd = "</ID>";
	   public final String firstNameTag = "<FIRST_NAME>";
	   public final String firstNameTagEnd = "</FIRST_NAME>";
	   public final String lastNameTag = "<LAST_NAME>";
	   public final String lastNameTagEnd = "</LAST_NAME>";
	   public final String dateOfBirthTag = "<DATE_OF_BIRTH>";
	   public final String dateOfBirthTagEnd = "</DATE_OF_BIRTH>";
	   public final String genderTag = "<GENDER>";
	   public final String genderTagEnd = "</GENDER>";
	   public final String addressTag = "<ADDRESS>";
	   public final String addressTagEnd = "</ADDRESS>";
	   public final String avatarUrlTag = "<AVATAR_URL>";
	   public final String avatarUrlTagEnd = "</AVATAR_URL>";
	   
	   public final String newLine = "\n";
	   
	   //method for export database to SD
	   public void exportDatabase(DBHelper dbHelper){
		   StringBuilder strBuilder = new StringBuilder();
		   strBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		   strBuilder.append(contactsListTag);
		   Cursor cursor = dbHelper.getAllContacts();
		   //generate XML markup
		   for(int i = 0; i<dbHelper.countContacts(); i++){
			   strBuilder.append(newLine +contactTag);
			       
			       strBuilder.append(newLine + idTag);
			       String id = cursor.getString(0);
			       strBuilder.append(newLine + id );
			       strBuilder.append(newLine + idTagEnd);
			       
			       strBuilder.append(newLine + firstNameTag);
			       String first_name = cursor.getString(1);
			       strBuilder.append(newLine + first_name);
			       strBuilder.append(newLine + firstNameTagEnd);
			       
			       strBuilder.append(newLine + lastNameTag);
			       String last_name = cursor.getString(2);
			       strBuilder.append(newLine + last_name);
			       strBuilder.append(newLine + lastNameTagEnd);
			       
			       strBuilder.append(newLine + dateOfBirthTag);
			       String date_of_birth = cursor.getString(3);
			       strBuilder.append(newLine + date_of_birth);
			       strBuilder.append(newLine + dateOfBirthTagEnd);
			       
			       strBuilder.append(newLine + genderTag);
			       String gender = cursor.getString(4);
			       strBuilder.append(newLine + gender);
			       strBuilder.append(newLine + genderTagEnd);
			       
			       strBuilder.append(newLine + addressTag);
			       String address = cursor.getString(5);
			       strBuilder.append(newLine + address);
			       strBuilder.append(newLine + addressTagEnd);
			       
			       strBuilder.append(newLine + avatarUrlTag);
			       String avatar_url = cursor.getString(6);
			       strBuilder.append(newLine + avatar_url);
			       strBuilder.append(newLine + avatarUrlTagEnd);
			       
			   strBuilder.append(newLine + contactTagEnd);
			   cursor.moveToNext();
		   }
		   strBuilder.append(contactsListTagEnd);
		   
		   try {
			    File newFolder = new File(Environment.getExternalStorageDirectory(), "XML_database");
			    if (!newFolder.exists()) {
			        newFolder.mkdir();
			    }
			    try {
			    	//create and write data to file
			        File file = new File(newFolder, "phonebook" + ".xml");
			        file.createNewFile();
			        Log.e("PATH", file.getAbsolutePath());
			        try {
			        	FileOutputStream fOut = new FileOutputStream(file);
                        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                        myOutWriter.append(strBuilder.toString());
                        myOutWriter.close();
                        fOut.close();
			        }
			        catch (IOException e) {
			            Log.e("TAG", "File write failed: " + e.toString());
			        } 
			        
			    } catch (Exception ex) {
			        System.out.println("ex: " + ex);
			    }
			} catch (Exception e) {
			    System.out.println("e: " + e);
			}
	   }
	   
	   public String importDatabase(DBHelper dbHelper){
		   XmlPullParser xpp = Xml.newPullParser();
		   Xml xml;
		   String docToString = null;
		   Element root;
		   try {
			   File file = new File(Environment.getExternalStorageDirectory() + "/XML_database"
					   + "/phonebook.xml");
			   InputStream is = new FileInputStream(file.getPath());
			   DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			   DocumentBuilder db = dbf.newDocumentBuilder();
			   Document doc = db.parse(new InputSource(is));
			   root = doc.getDocumentElement();
			   NodeList elements = doc.getElementsByTagName("CONTACT");
			   for(int i = 0; i<elements.getLength(); i++){
				   
				   Element e =  (Element)elements.item(i);
				   NodeList firstNameNode = doc.getElementsByTagName(DBHelper.FIRST_NAME.toUpperCase());
				   Element firstNameElement = (Element) firstNameNode.item(i);
				   
				   NodeList lastNameNode = doc.getElementsByTagName(DBHelper.LAST_NAME.toUpperCase());
				   Element lastNameElement = (Element) lastNameNode.item(i);
				   
				   NodeList dateOfBirthNode = doc.getElementsByTagName(DBHelper.DATE_OF_BIRTH.toUpperCase());
				   Element dateOfBirthElement = (Element) dateOfBirthNode.item(i);
				   
				   NodeList genderNode = doc.getElementsByTagName(DBHelper.GENDER.toUpperCase());
				   Element genderElement = (Element) genderNode.item(i);
				   
				   NodeList addressNode = doc.getElementsByTagName(DBHelper.ADDRESS.toUpperCase());
				   Element addressElement = (Element) addressNode.item(i);
				   
				   NodeList avatarUrlNode = doc.getElementsByTagName(DBHelper.AVATAR_URL.toUpperCase());
				   Element avatarUrlElement = (Element) avatarUrlNode.item(i);
				   
				   String firstName =  firstNameElement.getTextContent();
				   firstName = firstName.replace("\n", "").replace("\r", "");
				   String lastName = lastNameElement.getTextContent();
				   lastName = lastName.replace("\n", "").replace("\r", "");
				   String dateOfBirth = dateOfBirthElement.getTextContent();
				   dateOfBirth = dateOfBirth.replace("\n", "").replace("\r", "");
				   String gender = genderElement.getTextContent();
				   gender = gender.replace("\n", "").replace("\r", "");
				   String address = addressElement.getTextContent();
				   address = address.replace("\n", "").replace("\r", "");
				   String avatarUrl = avatarUrlElement.getTextContent();
				   avatarUrl = avatarUrl.replace("\n", "").replace("\r", "");
				   
				   dbHelper.insertContact(firstName, lastName, dateOfBirth, gender, address, avatarUrl);
			   }
			   
			   
			  
			   
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		   return docToString;
	   }
	   
	   private static String getValue(String sTag, Element eElement) {
		   
		     NodeList nlList = eElement.getElementsByTagName(sTag).item(0)
		     .getChildNodes();   
		     Node nValue = (Node) nlList.item(0);
		     return nValue.getNodeValue();
		  
		    }
}


