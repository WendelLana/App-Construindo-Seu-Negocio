package com.example.redelogin;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

//import com.squareup.picasso.Picasso; ||  Imagem \/  ||
//Picasso.get().load(R.drawable.ic_google_logo).into(image);

public class MainActivity extends AppCompatActivity{

    static final int GOOGLE_SIGN_IN = 123;
    FirebaseAuth mAuth;
    Button btn_login, btn_logout, btn_jury, btn_fina, btn_mark, btn_huma, btn_ger, btn_log, tour;
    ImageView image;
    ProgressBar progressBar;
    GoogleSignInClient mGoogleSignInClient;
    private Spinner isLegal, typeCompany;
    private EditText qtFunc, nameCompany;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        login();

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        if (mAuth.getCurrentUser() != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            loginEfetivo(user);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void login() {
        setContentView(R.layout.login);

        btn_login = findViewById(R.id.login);
        image = findViewById(R.id.image);
        progressBar = findViewById(R.id.progress_circular);
        btn_login.setOnClickListener(v -> SignInGoogle());
    }

    public void SignInGoogle() {
        progressBar.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.INVISIBLE);

                        Log.d("TAG", "signInWithCredential:success");

                        FirebaseUser user = mAuth.getCurrentUser();
                        loginEfetivo(user);
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);

                        Log.w("TAG", "signInWithCredential:failure", task.getException());

                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        loginEfetivo(null);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) firebaseAuthWithGoogle(account); //faz requesição ao Firebase
            } catch (ApiException e) {
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }

    private void loginEfetivo(FirebaseUser user) {
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            //String photo = String.valueOf(user.getPhotoUrl());

            db = FirebaseFirestore.getInstance();
            FirebaseUser fUser = mAuth.getCurrentUser();
            CollectionReference firstAcess = db.collection( "Empresas" ).document( fUser.getUid() ).collection( "Questionário" );

            firstAcess
                    .get()
                    .addOnSuccessListener( queryDocumentSnapshots -> {
                        if(queryDocumentSnapshots.isEmpty()){
                            startQuest( name, email );
                        } else {
                            menuPrincipal( name );
                        }
                    } )
                    .addOnFailureListener( err -> Toast.makeText( this, err.toString(), Toast.LENGTH_LONG ).show() );
        }
    }

    private void startQuest(String nome, String email){
        setContentView( R.layout.activity_quest );

        isLegal = findViewById( R.id.cbLeg );
        typeCompany = findViewById( R.id.cbSegm );
        qtFunc = findViewById( R.id.edFunc );
        nameCompany = findViewById( R.id.edNomeQuest );
        TextView cnpjComp = findViewById( R.id.lbCnpjQuest );
        EditText cnpjCompany = findViewById( R.id.edCnpjQuest );
        EditText addressCompany = findViewById( R.id.edEndQuest );
        TextView title = findViewById( R.id.title );

        title.setText( "Bem Vindo (a), "+ nome );

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( this, R.array.simplechoice, android.R.layout.simple_spinner_item );
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        isLegal.setAdapter( adapter );

        ArrayAdapter<CharSequence> adap = ArrayAdapter.createFromResource( this, R.array.segmchoice, android.R.layout.simple_spinner_item );
        adap.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        typeCompany.setAdapter( adap );

        isLegal.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(isLegal.getSelectedItemPosition() == 0){
                    cnpjComp.setVisibility( View.VISIBLE );
                    cnpjCompany.setVisibility( View.VISIBLE );
                } else {
                    cnpjComp.setVisibility( View.GONE );
                    cnpjCompany.setVisibility( View.GONE );
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        Button btFinish = findViewById( R.id.btConcluir );
        btFinish.setOnClickListener( (View v) -> {
            FirebaseUser user = mAuth.getCurrentUser();
            String cnpj = "";
            if (cnpjCompany.getVisibility() == View.VISIBLE) cnpj = cnpjCompany.getText().toString().trim();
            String name = nameCompany.getText().toString().trim();
            String endereco = addressCompany.getText().toString().trim();
            String segmento = typeCompany.getSelectedItem().toString().trim();
            String legal = isLegal.getSelectedItem().toString().trim();
            String qtFuncionarios = qtFunc.getText().toString().trim();

            if(validateInputs( name, segmento, legal, qtFuncionarios )){
                CollectionReference dbQuest = db.collection( "Empresas" ).document( user.getUid() ).collection( "Questionário" );

                Quest qt = new Quest(
                        cnpj,
                        name,
                        endereco,
                        email,
                        segmento,
                        legal,
                        Integer.parseInt( qtFuncionarios )
                );

                dbQuest.add(qt)
                        .addOnSuccessListener( documentReference -> {
                            Toast.makeText( this, "Questionário completo!", Toast.LENGTH_LONG).show();
                        } )
                        .addOnFailureListener( e -> {
                            Toast.makeText( this, "Erro ao enviar o formulário! Tente novamente.", Toast.LENGTH_LONG).show();
                        } );
            }

            menuPrincipal( nome );
        } );
    }

    private boolean validateInputs(String nome, String segmento, String legal, String qtFuncio){
        if(nome.isEmpty()){
            nameCompany.requestFocus();
            return false;
        }

        if(segmento.isEmpty()){
            typeCompany.requestFocus();
            return false;
        }

        if(legal.isEmpty()){
            isLegal.requestFocus();
            return false;
        }

        if(qtFuncio.isEmpty()){
            qtFunc.setError( "Campo necessário" );
            qtFunc.requestFocus();
            return false;
        }
        return true;
    }

    private void menuPrincipal(String nome) {
        setContentView( R.layout.menu_principal );

        btn_logout = findViewById( R.id.logout );
        btn_jury = findViewById( R.id.btJury );
        btn_mark = findViewById( R.id.btMark );
        btn_fina = findViewById( R.id.btFin );
        btn_huma = findViewById( R.id.btHuma );
        btn_ger = findViewById( R.id.btGer );
        btn_log = findViewById( R.id.btLog );
        TextView text = findViewById( R.id.textWelcome );
        tour = findViewById( R.id.tour );

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Trocchi.ttf");
        text.setTypeface(custom_font);
        btn_jury.setTypeface( custom_font );
        btn_log.setTypeface( custom_font );
        btn_fina.setTypeface( custom_font );
        btn_ger.setTypeface( custom_font );
        btn_huma.setTypeface( custom_font );
        btn_mark.setTypeface( custom_font );
        tour.setTypeface( custom_font );

        btn_logout.setOnClickListener( v -> Logout() );
        btn_huma.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), HumaActivity.class );
            v.getContext().startActivity( intent );
        } );
        btn_ger.setOnClickListener( ( View v ) -> {
            Intent intent = new Intent( getBaseContext(), GereActivity.class );
            v.getContext().startActivity( intent );
        });
        btn_jury.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), JuryActivity.class );
            v.getContext().startActivity( intent );
        } );
        btn_log.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), LogActivity.class );
            v.getContext().startActivity( intent );
        } );
        btn_mark.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), MarkActivity.class );
            v.getContext().startActivity( intent );
        } );
        btn_fina.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), FinaActivity.class );
            v.getContext().startActivity( intent );
        } );
        tour.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), TourActivity.class );
            v.getContext().startActivity( intent );
        } );


        tour.getBackground().setAlpha(102);
        text.setText( "Bem vindo (a), \n" + nome );
    }

    private void Logout() {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                task -> login());
    }
}