package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.security.identity.SessionTranscriptMismatchException;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    ActionBar actionBar;
    TextInputEditText tiedemail,tietnum;
    Button btnsubmit;
    RecyclerView rv;
    SharedPreferences spf;
    LinearLayout loutforinput;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String email, number;
    ArrayList emaillist ,numberlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tiedemail = (TextInputEditText)findViewById(R.id.tiedemail);
        tietnum = (TextInputEditText)findViewById(R.id.tietnum);
        btnsubmit = (Button)findViewById(R.id.btnsubmit);
        loutforinput = (LinearLayout)findViewById(R.id.loutforinput);
        rv = (RecyclerView)findViewById(R.id.rv);
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#2196F3"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<small><font color='#FFFFFF'>Assignment</font></small>"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colormyPrimary));
        }
        spf = getSharedPreferences("myapplication", Context.MODE_PRIVATE);
        emaillist = new ArrayList();
        numberlist = new ArrayList();
        if(spf.contains("log")){
            loutforinput.setVisibility(View.GONE);
            emaillist =  getArrayList("em");
            numberlist = getArrayList("no");
            rvadapter rvadapter = new rvadapter(MainActivity.this,emaillist,numberlist);
            rv.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false));
            rv.setAdapter(rvadapter);
            rv.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                    DividerItemDecoration.HORIZONTAL));
        }else{
            loutforinput.setVisibility(View.VISIBLE);
        }


        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tiedemail.getText().toString().matches(emailPattern) || tiedemail.getText().toString().isEmpty() || tietnum.getText().toString().isEmpty()){
                    if(!tiedemail.getText().toString().matches(emailPattern)){
                        tiedemail.setError("Enter Valid Email Address");
                        tiedemail.requestFocus();
                    }else if (tiedemail.getText().toString().isEmpty()){
                        tiedemail.setError("Field Cannot be empty");
                        tiedemail.requestFocus();
                    }else if (tietnum.getText().toString().isEmpty()){
                        tietnum.setError("Field Cannot be empty");
                        tietnum.requestFocus();
                    }
                }else{
                    email = tiedemail.getText().toString().trim();
                    number = tietnum.getText().toString().trim();

                    if(emaillist.contains(email) || numberlist.contains(number)){
                        if(emaillist.contains(email)){
                            Toast.makeText(MainActivity.this, "Duplicate emails are not allowed", Toast.LENGTH_SHORT).show();
                        }else if(numberlist.contains(number)){
                            Toast.makeText(MainActivity.this, "Duplicate numbers are not allowed", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        emaillist.add(email);
                        numberlist.add(number);

                        loadrv();
                        saveArrayList(emaillist, "em");
                        saveArrayList(numberlist,"no");
                    }

                }
            }
        });
    }

    public void saveArrayList(ArrayList<String> list, String key){
        spf = getSharedPreferences("myapplication", Context.MODE_PRIVATE);
        SharedPreferences.Editor spe = spf.edit();
        spe.putString("log", "1");
        Gson gson = new Gson();
        String json = gson.toJson(list);
        spe.putString(key, json);
        spe.apply();
    }

    public ArrayList<String> getArrayList(String key){
        spf = getSharedPreferences("myapplication", Context.MODE_PRIVATE);
        SharedPreferences.Editor spe = spf.edit();
        Gson gson = new Gson();
        String json = spf.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void loadrv(){
        rvadapter rvadapter = new rvadapter(MainActivity.this,emaillist,numberlist);
        rv.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false));
        rv.setAdapter(rvadapter);
        rv.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.HORIZONTAL));
    }
}
class rvholder extends RecyclerView.ViewHolder{

    TextView tvemail,tvnum;
    public rvholder(@NonNull View itemView) {
        super(itemView);
        tvemail = (TextView)itemView.findViewById(R.id.tvemail);
        tvnum = (TextView)itemView.findViewById(R.id.tvnum);
    }
}
class rvadapter extends RecyclerView.Adapter<rvholder>{
    Context activity;
    ArrayList emaillist,numberlist;

    public rvadapter(MainActivity mainActivity, ArrayList emaillist, ArrayList numberlist) {
        this.activity = mainActivity;
        this.emaillist = emaillist;
        this.numberlist = numberlist;
    }

    @NonNull
    @Override
    public rvholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View v =layoutInflater.inflate(R.layout.rvlayout,parent,false);
        rvholder rvholder = new rvholder(v);
        return rvholder;
    }

    @Override
    public void onBindViewHolder(@NonNull rvholder holder, int position) {
        holder.tvemail.setText("Email: "+emaillist.get(position).toString());
        holder.tvnum.setText("Number: "+numberlist.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return emaillist.size();
    }
}