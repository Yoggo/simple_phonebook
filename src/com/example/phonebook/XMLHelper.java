package com.example.phonebook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
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
import org.xmlpull.v1.XmlSerializer;

import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

public class XMLHelper {
	   //list of tags
	   static public final String contactsListTag = "CONTACTS_LIST";
	   static public final String contactTag = "CONTACT";
	   static public final String idTag = "ID";
	   static public final String firstNameTag = "FIRST_NAME";
	   static public final String lastNameTag = "LAST_NAME";
	   static public final String dateOfBirthTag = "DATE_OF_BIRTH";
	   static public final String genderTag = "GENDER";
	   static public final String addressTag = "ADDRESS";
	   static public final String avatarUrlTag = "AVATAR_URL";
	   
	   public final String newLine = "\n";
	   
	   private XmlSerializer serializer;
	   

	   
	   //method for export database to SD
	   public void exportDatabase(DBHelper dbHelper){
		   
		   serializer = Xml.newSerializer();
		   StringWriter writer = new StringWriter();
		   try {
			serializer.setOutput(writer);
			serializer.startDocument("utf-8", true);
			serializer.startTag("", contactsListTag);
		   } catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		   } catch (IllegalStateException e1) {
			e1.printStackTrace();
		   } catch (IOException e1) {
		 	e1.printStackTrace();
		   }
		   Cursor cursor = dbHelper.getAllContacts();
		   
		   //generate XML markup
		   for(int i = 0; i<dbHelper.countContacts(); i++){
			       
			       try {
			    	   
			    	serializer.startTag("", contactTag);
			    	   
				    String id = cursor.getString(0); 
					serializer.startTag("", idTag);
					serializer.text(id);
					serializer.endTag("", idTag);
					
				    String first_name = cursor.getString(1);
					serializer.startTag("", firstNameTag);
					serializer.text(first_name);
					serializer.endTag("", firstNameTag);
					
				    String last_name = cursor.getString(2);
					serializer.startTag("", lastNameTag);
					serializer.text(last_name);
					serializer.endTag("", lastNameTag);

				    String date_of_birth = cursor.getString(3);
					serializer.startTag("", dateOfBirthTag);
					serializer.text(date_of_birth);
					serializer.endTag("", dateOfBirthTag);
				       
				    String gender = cursor.getString(4);
					serializer.startTag("", genderTag);
					serializer.text(gender);
					serializer.endTag("", genderTag);
					
				    String address = cursor.getString(5);
					serializer.startTag("", addressTag);
					serializer.text(address);
					serializer.endTag("", addressTag);
					
				    String avatar_url = cursor.getString(6);
					serializer.startTag("", avatarUrlTag);
					serializer.text(avatar_url);
					serializer.endTag("", avatarUrlTag);
					
					serializer.endTag("", contactTag);
					
				   } catch (IllegalArgumentException e) {
					e.printStackTrace();
				   } catch (IllegalStateException e) {
					e.printStackTrace();
				   } catch (IOException e) {
					e.printStackTrace();
				   }
			   cursor.moveToNext();
		   }
		   
		   try {
			   serializer.endTag("", contactsListTag);
			   serializer.endDocument();
		   } catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		   } catch (IllegalStateException e1) {
			e1.printStackTrace();
		   } catch (IOException e1) {
			e1.printStackTrace();
		   }
		   
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
                        myOutWriter.append(writer.toString());
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
				   is.close();
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


