package com.peekatucorp.peekatu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Random;
public class ChatActivity extends Activity {
	private com.peekatucorp.peekatu.DiscussArrayAdapter adapter;
	private ListView lv;
//	private LoremIpsum ipsum;
	private EditText editText1;
	private static Random random;
	private String selected_room;
	private String last_message;
	private Document responseDoc;
	@Override protected void onResume() { 
		super.onResume(); // setText() here 
		Log.v("response", "request");
		
		Intent i = this.getIntent();
		String type = i.getExtras().getString("type");
		Log.v("response ", "messages type "+type);
		if(type.equalsIgnoreCase("1"))
			
		
		getChatMessages();
		else if(type.equalsIgnoreCase("2"))
			getInboxMessages();
		else if(type.equalsIgnoreCase("3"))
			getUserMessages(i.getExtras().getString("conv_user"));
	}
	public void getUserMessages(final String forUserID){
		final SharedPreferences preferences = this.getSharedPreferences("MyPreferences", MODE_PRIVATE);
		AsyncHttpClient  client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
    	params.put("action", "get");
    	 params.put("room", preferences.getString("selected_room", "Lobby"));
    	 params.put("dist", "0");
    	 params.put("pmsgid", "1");
    	 params.put("msgid", "9999999999999");
    	 params.put("private_toid", forUserID);
    	 params.put("webversion", "1");
    	 params.put("censor", "0");
    	 params.put("email", preferences.getString("loggedin_user", ""));
    	 /* pmsgid:906588*/
    	 
    ///	 params.put("profile_picture", new File("pic.jpg")); // Upload a File
    //	 params.put("profile_picture2", someInputStream); // Upload an InputStream
    //	 params.put("profile_picture3", new ByteArrayInputStream(someBytes)); // Upload some bytes

        client.post("http://peekatu.com/apiweb/messagetest.php",params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
            //	Log.v("response", response);
            	 XMLParser parser = new XMLParser();
               
                 Document doc = parser.getDomElement(response); // getting DOM element
          
            	
            	NodeList nl = doc.getElementsByTagName("PMESSAGE");
            	Log.v("response ", "messages count "+nl.getLength());
                // looping through all item nodes <item>
                for (int i = 0; i < nl.getLength(); i++) {
                    // creating new HashMap
                	
                    Element e = (Element) nl.item(i);
              //      Log.v("response ", "messages  "+parser.getValue(e, "TEXT"));
                   // adapter.add(new OneComment(true, "OKOKOK"));
                    Boolean dir = true;
                    if(!parser.getValue(e, "USERID").equalsIgnoreCase(forUserID)){
                    	dir = true;
                    }
                    adapter.add(new OneComment(dir, 
                    		parser.getValue(e, "TEXT"),
                    		parser.getValue(e, "USERID"),
                    		parser.getValue(e, "ALIAS"),
                    		parser.getValue(e, "GENDER"),
                    		parser.getValue(e, "PIC"),
                    		parser.getValue(e, "TDATE"),
                    		parser.getValue(e, "ONLINE"),
                    		parser.getValue(e, "MSGPIC")));
                    // adding HashList to ArrayList
                   // menuItems.add(map);
                }
                lv.setSelection(adapter.getCount() - 1);
            }
        });
	}
	public void getInboxMessages(){
		final SharedPreferences preferences = this.getSharedPreferences("MyPreferences", MODE_PRIVATE);
		AsyncHttpClient  client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
    	params.put("action", "get");
    	 params.put("room", preferences.getString("selected_room", "Lobby"));
    	 params.put("dist", "0");
    	 params.put("email", preferences.getString("loggedin_user", ""));
    	 
    	 params.put("msgid", "1");
    ///	 params.put("profile_picture", new File("pic.jpg")); // Upload a File
    //	 params.put("profile_picture2", someInputStream); // Upload an InputStream
    //	 params.put("profile_picture3", new ByteArrayInputStream(someBytes)); // Upload some bytes
    	 
        client.post("http://peekatu.com/apiweb/messagetest.php",params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
            //	Log.v("response", response);
            	 XMLParser parser = new XMLParser();
               
                 Document doc = parser.getDomElement(response); // getting DOM element
          
            	
            	NodeList nl = doc.getElementsByTagName("PRIVATE");
            	Log.v("response ", "inbox count(activity) "+nl.getLength());
                // looping through all item nodes <item>
                for (int i = 0; i < nl.getLength(); i++) {
                    // creating new HashMap
                	
                    Element e = (Element) nl.item(i);
                   Log.v("response ", "messages  "+parser.getValue(e, "MESG"));
                   // adapter.add(new OneComment(true, "OKOKOK"));
                    adapter.add(new OneComment(true, parser.getValue(e, "MESG"),
                    		parser.getValue(e, "USERID"),
                    		parser.getValue(e, "ALIAS"),
                    		parser.getValue(e, "GENDER"),
                    		parser.getValue(e, "PIC"),
                    		parser.getValue(e, "TDATE"),
                    		parser.getValue(e, "ONLINE"),
                    		null));
                    // adding HashList to ArrayList
                   // menuItems.add(map);
                }
             //   lv.setSelection(adapter.getCount() - 1);
            }
        });
	}
