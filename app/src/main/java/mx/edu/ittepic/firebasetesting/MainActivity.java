package mx.edu.ittepic.firebasetesting;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private EditText editCorreo, editContraseña;
    private FirebaseAuth firebaseAuth;
    public Button botonLogin;
    String correo="", contrasena="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {

            startActivity(new Intent(MainActivity.this,MainDB.class));
            finish();
        }

        editCorreo = (EditText) findViewById(R.id.correo);
        editContraseña = (EditText) findViewById(R.id.contrasena);
        botonLogin = (Button) findViewById(R.id.btnLogin);
        botonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                correo = editCorreo.getText().toString().trim();
                contrasena = editContraseña.getText().toString().trim();
                if (TextUtils.isEmpty(correo)) {
                    Toast.makeText(getApplicationContext(), "Ingrese un Correo", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(contrasena)) {
                    Toast.makeText(getApplicationContext(), "Ingrese una contraseñ", Toast.LENGTH_SHORT).show();
                    return;
                }
                firebaseAuth.createUserWithEmailAndPassword(correo,contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            firebaseAuth.signInWithEmailAndPassword(correo, contrasena)
                                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                            if (!task.isSuccessful()) {
                                                if (contrasena.length() < 6) {
                                                    Toast.makeText(getApplicationContext(), "Ingrese una contraseñ", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Error al iniciar sesion", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Intent intent = new Intent(MainActivity.this, MainDB.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                        } else {
                            startActivity(new Intent(MainActivity.this,MainDB.class));
                            finish();
                        }
                    }
                });
            }
        });
    }


}
