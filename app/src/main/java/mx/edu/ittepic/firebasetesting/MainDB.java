package mx.edu.ittepic.firebasetesting;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import mx.edu.ittepic.firebasetesting.Utilidades.AdaptadorAlumno;
import mx.edu.ittepic.firebasetesting.Utilidades.Alumno;

public class MainDB extends AppCompatActivity {
    public ArrayList<Alumno> alumoslista;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    AlertDialog.Builder dialogBuilder;
    AlertDialog alertDialog;
    Button btnAgregar;
    EditText nombre,numerocontrol;

    RecyclerView listaObjetos;
    private RecyclerView.LayoutManager mLayoutManager;
    AdaptadorAlumno adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_db);

        listaObjetos = (RecyclerView) findViewById(R.id.lista);
        listaObjetos.setHasFixedSize(true);
        listaObjetos.setLayoutManager(new LinearLayoutManager(this));

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("alumnos");
        alumoslista = new ArrayList<>();
        cargarAlumnos();

    }
    private void agregarNuevoAlumno(String nombre,String numero_control) {
        Alumno alumnoAgregar = new Alumno(nombre, numero_control);
        mDatabase.child(mDatabase.child("alumnos").push().getKey()).setValue(alumnoAgregar);
        alertDialog.dismiss();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.salirSesion) {
            auth.signOut();
            startActivity(new Intent(MainDB.this, MainActivity.class));
            finish();

            return true;
        }
        if (id == R.id.agregarAlumno) {
            agregarAlumno();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void agregarAlumno(){
        dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.agregar_alumno, null);
        dialogBuilder.setView(dialogView);
        nombre = (EditText)dialogView.findViewById(R.id.nombre);
        numerocontrol = (EditText)dialogView.findViewById(R.id.numerocontrol);
        btnAgregar= (Button) dialogView.findViewById(R.id.btnAgregarAlumno);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarNuevoAlumno(nombre.getText().toString(),numerocontrol.getText().toString());
            }
        });
        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
    public void cargarAlumnos(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                alumoslista = new ArrayList<>();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    alumoslista.add(childSnapshot.getValue(Alumno.class));
                }
                if(alumoslista.size()>0){
                    adapter = new AdaptadorAlumno(alumoslista, getApplicationContext(), MainDB.this);
                    adapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });
                    listaObjetos.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mDatabase.addValueEventListener(postListener);
    }
}
