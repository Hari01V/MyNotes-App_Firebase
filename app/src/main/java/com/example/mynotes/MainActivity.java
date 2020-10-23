package com.example.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private static ex_adapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    public  ArrayList<ex_item> mex_list;
    public ArrayList<ex_item> selectionlist;
    private ActionMode mActionMode;
    private int longclick_position;
    public boolean isActionModeenabled = false;
    public static boolean flag_selectall = false;
    final Context context=this;
    private DrawerLayout drawerLayout;

    //FIREBASE
    FirebaseAuth firebaseAuth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);

        //FOR LOGIN/SIGNUP PAGE
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser==null){
            startActivity(new Intent(MainActivity.this,Loginup.class));
        }

        create_exlist();
        build_RecyclerView();
        selectionlist=new ArrayList<>();

        //NAV VIEW
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_nav_drawer,R.string.close_nav_drawer);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        //FOR SCREEN SIZE
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height=displayMetrics.heightPixels;
        final int width=displayMetrics.widthPixels;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //NEW ITEM TO BE ADDED !
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                final View view = layoutInflater.inflate(R.layout.new_input,null);
                view.setMinimumWidth(width);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(view);
                final AlertDialog alertDialog = builder.create();

                Button dialog_add = view.findViewById(R.id.dialog_add);
                dialog_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText editText1 = view.findViewById(R.id.edit_name);
                        String name = editText1.getText().toString();
                        EditText editText2 = view.findViewById(R.id.edit_desc);
                        String desc = editText2.getText().toString();
                        mex_list.add(new ex_item(name,desc));
                        alertDialog.dismiss();
                        mAdapter.notifyItemInserted(mex_list.size());

                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        assert firebaseUser != null;
                        String userId = firebaseUser.getUid();
                        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("List").child(""+mex_list.size());
                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put("name",name);
                        hashMap.put("desc",desc);
                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(MainActivity.this,"Added to Notes",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                Button dialog_cancel = view.findViewById(R.id.dialog_cancel);
                dialog_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });


    }

    public void create_exlist(){
        mex_list = new ArrayList<>();
        Toast.makeText(MainActivity.this,"Fetched Data !",Toast.LENGTH_SHORT).show();
            String userId = firebaseAuth.getCurrentUser().getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("List");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mex_list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String desc = dataSnapshot.child("desc").getValue().toString();
                        mex_list.add(new ex_item(name, desc));
                    }
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }
    public void build_RecyclerView(){
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ex_adapter(mex_list,MainActivity.this);

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemLongClickListener(new ex_adapter.OnItemLongClickListener() {
            @Override
            public void OnItemLongClick(int position) {
                longclick_position = position;
                mActionMode=startSupportActionMode(mActionModeCallback);
                isActionModeenabled=true;
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    public void deleteselected(ArrayList<ex_item> selectionlist){
        for (ex_item item : selectionlist){
            mex_list.remove(item);
        }
        mAdapter.notifyDataSetChanged();
        /*String userId = firebaseAuth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("List");
        for(final ex_item item : selectionlist) {
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String name = item.getName();
                        String desc = item.getDesc();
                        Query delete = dataSnapshot.getRef().orderByChild("name").equalTo(name);
                        delete.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }*/
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    int getlongclickposition(){
        return longclick_position;
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback(){
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_main,menu);
            int position = getlongclickposition();
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()){
                case R.id.delete_option :
                    deleteselected(selectionlist);
                    mode.finish();
                    return true;
                case R.id.select_all:
                    if(!flag_selectall){
                        flag_selectall=true;
                    }
                    else {
                        flag_selectall = false;
                    }
                    mAdapter.notifyDataSetChanged();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isActionModeenabled=false;
            selectionlist.clear();
            flag_selectall=false;
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_settings){
            return true;
        }
        if (id==R.id.logout){
            firebaseAuth.signOut();
            startActivity(new Intent(MainActivity.this, Loginup.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void MakeSelection(View v,int position){
        if(((CheckBox)v).isChecked()){
            selectionlist.add(mex_list.get(position));
        }
        else{
            selectionlist.remove(mex_list.get(position));
        }
        mActionMode.setTitle(selectionlist.size() + " items selected");
    }


}