package sg.edu.rp.webservices.dmsdchatapp;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference messageListRef, nameRef;

    TextView tvWeather;
    EditText etMessage;
    Button btnsendmessage;
    String messageuser;
    Long time;
    ArrayList<Message> alMessage = new ArrayList<Message>();
    ArrayAdapter aaMessage;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = firebaseAuth.getCurrentUser();
        String uid = firebaseUser.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        tvWeather = (TextView) findViewById(R.id.textViewWeather);
        etMessage = (EditText) findViewById(R.id.etmsg);
        btnsendmessage = (Button) findViewById(R.id.btnAddMessage);
        lv = (ListView) findViewById(R.id.listViewMessage);

        alMessage = new ArrayList<Message>();
        aaMessage = new CustomAdapter(getBaseContext(), alMessage);
        lv.setAdapter(aaMessage);
        nameRef = firebaseDatabase.getReference("profiles/" + uid);
        messageListRef = firebaseDatabase.getReference("messages/");

        HttpRequest request = new HttpRequest
                ("https://api.data.gov.sg/v1/environment/2-hour-weather-forecast");
        request.setAPIKey("USgDjj3BSjvuIANTX4LDvacxs5BG39Jx", "USgDjj3BSjvuIANTX4LDvacxs5BG39Jx");
        request.setOnHttpResponseListener(mHttpResponseListener);
        request.setMethod("GET");
        request.execute();

        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageuser = (String) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        btnsendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = etMessage.getText().toString();

                time = new Date().getTime();

                Message messages = new Message(msg, time, messageuser);
                messageListRef.push().setValue(messages);
                etMessage.setText("");


            }
        });
        messageListRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i("CommunicateFragment", "onChildAdded");
                Message msg = dataSnapshot.getValue(Message.class);
                if (msg != null) {
                    msg.setId(dataSnapshot.getKey());
                    alMessage.add(msg);
                    aaMessage.notifyDataSetChanged();
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String selectedId = dataSnapshot.getKey();
                Message msg = dataSnapshot.getValue(Message.class);

                if (msg != null) {
                    for (int i = 0; i < alMessage.size(); i++) {
                        if (alMessage.get(i).getId().equals(selectedId)) {
                            msg.setId(selectedId);
                            alMessage.set(i, msg);
                        }
                    }
                    aaMessage.notifyDataSetChanged();
                }

            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.i("MainActivity", "onChildRemoved()");

                String selectedId = dataSnapshot.getKey();
                Message msg = dataSnapshot.getValue(Message.class);
                if (msg != null) {
                    for (int i = 0; i < alMessage.size(); i++) {
                        if (alMessage.get(i).getId().equals(selectedId)) {
                            msg.setId(selectedId);
                            alMessage.remove(i);
                        }
                    }
                    aaMessage.notifyDataSetChanged();
                }
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.optionmenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            Intent i = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(i);
        } else if (id == R.id.action_changeusername) {
            Intent i = new Intent(getBaseContext(), displaynameActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    // Code for step 2 start
    private HttpRequest.OnHttpResponseListener mHttpResponseListener =
            new HttpRequest.OnHttpResponseListener() {
                @Override
                public void onResponse(String response) {
                    // process response here
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject woodlandsObj = (JSONObject) jsonObject.getJSONArray("items").getJSONObject(0).getJSONArray("forecasts").get(45);
                        String weather = (String) woodlandsObj.get("forecast");
                        tvWeather.setText("Weather Forecast @ Woodlands " + weather);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };
}
