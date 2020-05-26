package com.idk.smartalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class adminreq extends AppCompatActivity {
    ListView l;
    Query query;
    String s="sddd",st;
    public ArrayList<String> al;
    static String keyn;
    ArrayAdapter arrayAdapter;
    static Double Longitude,latitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminreq);
        l=findViewById(R.id.lv);
        al=new ArrayList<>();
        query = FirebaseDatabase.getInstance().getReference().child("users").orderByChild("status").equalTo("help");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    //String s= String.valueOf(postSnapShot.getValue("username").toString());
                    s = postSnapShot.child("username").getValue(String.class);
                    //Log.e("bhetla", " " + s);
                    //if(s.contains("help"))
                    {
                        al.add(s);
                        Log.e("list",al.toString());
                        st=st+","+s;
                        Refres();
//                        Log.e("array",al.toString());
                       //Toast.makeText(adminreq.this, al.toString(), Toast.LENGTH_LONG).show();

                        //String[] values = s.split(",");
                      //  AlertDialog.Builder builder;
                      //  builder = new AlertDialog.Builder(adminreq.this);
                     //   builder.setMessage(s)
                          //      .setCancelable(false)
                           /*   .setPositiveButton("Visit Location", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    Intent ss = new Intent(adminreq.this, frdmap.class);
                                        startActivity(ss);
                                        //Intent callIntent = new Intent(Intent.ACTION_CALL);
                                        //callIntent.setData(Uri.parse("tel:"+9049827604))/change the number
                                        //startActivity(callIntent);
                                        // finish();
                                        Toast.makeText(getApplicationContext(), "you choose yes action for alertbox",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                })*/
                              /*  .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //  Action for 'NO' Button
                                        dialog.cancel();
                                        Toast.makeText(getApplicationContext(), "you choose no action for alertbox",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                                //add end coment here!@#$%^&*(*)*&$%#@!%#^$&*)&(^%&$^#^&%(^
                                */
                    //    AlertDialog alert = builder.create();
                        //Setting the title manually
                    //    alert.setTitle("Someone needs your help");
                     //   alert.show();
                     //   Toast.makeText(adminreq.this, "help de re", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

       l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Log.e("iii",""+al.get(i));
                String name=al.get(i);
               DatabaseReference ref=FirebaseDatabase.getInstance().getReference();
               Query query=ref.child("users").orderByChild("username").equalTo(name);
               query.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       if(dataSnapshot.exists())
                       {
                           for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                           {
                                keyn=dataSnapshot1.getKey();
                               Log.e("loc",keyn);
                           }

                       }
                       DatabaseReference ref1=FirebaseDatabase.getInstance().getReference().child("users").child(keyn).child("CurrentLocation");

                       ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               Longitude=dataSnapshot.child("longitude").getValue(Double.class);
                               latitude=dataSnapshot.child("latitude").getValue(Double.class);
                               Log.e("long",""+Longitude+","+latitude);
                               Intent ss = new Intent(getApplicationContext(), frdmap.class);
                               startActivity(ss);

                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                       });
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });



         }
        });

    }

    public void Refres() {
        Log.e("bahar",al.toString());
        //end dialogue bo
   //     Toast.makeText(adminreq.this,al.toString(),Toast.LENGTH_LONG).show();

        //     al.add("abcd");al.add("efgh");
//        String[] values = st.split(",");
        // al.add("ijkl");
//        for(Object s1:al)
//        {
//            al.add(String.valueOf(s1));
//            Log.e("ha",""+al);
//        }
        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,al);
        l.setAdapter(arrayAdapter);
    }
}