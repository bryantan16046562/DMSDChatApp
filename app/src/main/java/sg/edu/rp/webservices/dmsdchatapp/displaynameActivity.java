package sg.edu.rp.webservices.dmsdchatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class displaynameActivity extends AppCompatActivity {
    private EditText etName;
    private Button btnSubmit;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userProfileRef, existingName;


    private static final String TAG = "displaynameActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displayname);

        etName = (EditText) findViewById(R.id.editTextName);
        btnSubmit = (Button) findViewById(R.id.button_submitdisplayname);

        /* --- 1st way of getting child id ---
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userProfileRef = firebaseDatabase.getReference("profiles");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userProfileRef.child(firebaseUser.getUid()).setValue(etName.getText().toString());

                Toast.makeText(getApplicationContext(),"Registration successfully",Toast.LENGTH_LONG).show();

                Intent a = new Intent(displaynameActivity.this, MainActivity.class);
                startActivity(a);
            }
        });
        */
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        String userid = firebaseUser.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userProfileRef = firebaseDatabase.getReference("profiles/" + userid);

        existingName = userProfileRef.child(userid);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = firebaseUser.getUid();
                String displayName = etName.getText().toString();
                userProfileRef.setValue(displayName);
                Intent i = new Intent(displaynameActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dislaynamemenu, menu);
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
        } else if (id == R.id.action_homepage) {
            Intent i = new Intent(getBaseContext(), MainActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}
