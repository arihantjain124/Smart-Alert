package com.idk.smartalert;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.idk.smartalert.Model.user;
import com.idk.smartalert.Model.userloginmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    EditText email,password;
    TextView login, resetpass;
    ProgressBar simpleProgressBar=null;
    TextView register_here;
    String userss;
    FirebaseAuth auth;


    user user1;
    Query query;
    FirebaseUser firebaseUser;
    DatabaseReference ref;

    ImageView sback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        resetpass=findViewById(R.id.forgot_password);
        resetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(LoginActivity.this,resetpassword.class);
                startActivity(in);
            }
        });
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        //FirebaseAuth mAuth = FirebaseAuth.getInstance().getUid();
        //String provider = user.getProvider().get(0);
        String user="";
        // FirebaseUser user =mAuth.getCurrentUser();

            //user= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        try {
            user= FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        catch(Exception e){}
        // Log.e("dd",""+user);
        if (user  != null) {
            DatabaseReference reference;
            reference= FirebaseDatabase.getInstance().getReference();
            Query query1 = reference.child("users").orderByChild("id").equalTo(user);
            final List<userloginmodel> usertype = new ArrayList<>();
            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for(DataSnapshot postSnapShot:dataSnapshot.getChildren()) {

                        user1 = postSnapShot.getValue(user.class);
                        usertype.add(new userloginmodel(user1.getId(), user1.getUsertype()));                                            //s= Boolean.parseBoolean(String.valueOf(query = ref.child("USER").orderByChild("usertype").equalTo("client")));
                    }
                    for(int i =0; i<usertype.size();i++){
                        userloginmodel uwtm = usertype.get(i);
                        if (uwtm.getUsertype().equals("user")){
                            Log.e("user",uwtm.getUsertype());
                            Toast.makeText(LoginActivity.this, "Logged in as: user", Toast.LENGTH_SHORT).show();
                            Intent ii = new Intent(LoginActivity.this, MapsActivity.class);
                            ii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(ii);
                            break;
                        }else if (uwtm.getUsertype().equals("admin")){
                            Log.e("user1",uwtm.getUsertype());
                            Toast.makeText( LoginActivity.this,"Logged in as: admin", Toast.LENGTH_SHORT).show();
                            Intent ij = new Intent(LoginActivity.this, Admin.class);
                            ij.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(ij);
                            break;
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            // User is signed in
        }
        sback = (ImageView)findViewById(R.id.sinb);
        sback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getApplicationContext(),Register.class);
                startActivity(it);
            }
        });
//        Toolbar toolbar=findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Login");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user1=new user();
        auth=FirebaseAuth.getInstance();
        ref= FirebaseDatabase.getInstance().getReference();
        email=findViewById(R.id.emailss);
        password=findViewById(R.id.password);
        login=findViewById(R.id.logins);
        //register_here=findViewById(R.id.register_here);

//        register_here.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
//            }
//        });

        simpleProgressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email=email.getText().toString();
                String txt_password=password.getText().toString();
                if (TextUtils.isEmpty(txt_email)||TextUtils.isEmpty(txt_password)){
                    Toast.makeText(LoginActivity.this, "Fill all", Toast.LENGTH_SHORT).show();
                }else {
                    simpleProgressBar.setVisibility(View.VISIBLE);
                    auth.signInWithEmailAndPassword(txt_email,txt_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            final List<userloginmodel> usertype = new ArrayList<>();
                            if (task.isSuccessful()){
                                FirebaseUser users = FirebaseAuth.getInstance().getCurrentUser();
                                if (users != null) {
                                    userss = users.getUid();
                                }
                                query = ref.child("users").orderByChild("id").equalTo(userss);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot postSnapShot:dataSnapshot.getChildren()) {
                                            user1 = postSnapShot.getValue(user.class);
                                            usertype.add(new userloginmodel(user1.getId(), user1.getUsertype()));                                            //s= Boolean.parseBoolean(String.valueOf(query = ref.child("USER").orderByChild("usertype").equalTo("client")));
                                        }
                                        for(int i =0; i<usertype.size();i++){
                                            userloginmodel uwtm = usertype.get(i);
                                            if (uwtm.getUsertype().equals("user")){
                                                simpleProgressBar.setVisibility(View.INVISIBLE);
                                                Intent intent=new Intent(LoginActivity.this,MapsActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            }else if (uwtm.getUsertype().equals("admin")){
                                                simpleProgressBar.setVisibility(View.INVISIBLE);
                                                Intent intent=new Intent(LoginActivity.this,Admin.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            }else {
                                                Toast.makeText(LoginActivity.this, "no user type", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }
                            else {
                                Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                simpleProgressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }
}