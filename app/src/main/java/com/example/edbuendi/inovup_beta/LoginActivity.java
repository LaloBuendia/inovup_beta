package com.example.edbuendi.inovup_beta;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends ActionBarActivity {
    LinearLayout linearLayout;
    //Botón de facebook
    LoginButton buttonLoginFacebook;
    CallbackManager callbackManager;
    ImageView imageViewPhoto;
    ImageButton imageButtonSignOut;
    TextView textViewFullName, textViewEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);


        linearLayout = (LinearLayout) findViewById(R.id.linear_layout_sign_in);
        imageButtonSignOut = (ImageButton) findViewById(R.id.image_button_sign_out);
        //Instanciamos el botón de facebook
        buttonLoginFacebook = (LoginButton) findViewById(R.id.connectWithFbButton);
        imageViewPhoto = (ImageView) findViewById(R.id.image_view_photo);
        textViewFullName = (TextView) findViewById(R.id.text_view_full_name);
        textViewEmail = (TextView) findViewById(R.id.text_view_email);

        //Pedimos permiso para poder obtener el email
        buttonLoginFacebook.setReadPermissions("email");

        //Registramos un callback se ejecutará una vez se hace introducido las credenciales
        //de la cuenta de facebook
        buttonLoginFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Método usado para obtener los campos o atributos solciitados
                getFaceBookProfileDetails(loginResult.getAccessToken());
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        imageButtonSignOut.setVisibility(View.GONE);

        imageButtonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cierra la sesion inciada en la aplicación
                LoginManager.getInstance().logOut();
                textViewFullName.setText(getString(R.string.full_name));
                textViewEmail.setText(getString(R.string.email));
                imageViewPhoto.setImageResource(R.mipmap.ic_place_holder_photo);
                imageButtonSignOut.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
            }
        });

    }

    private void getFaceBookProfileDetails(final AccessToken accessToken) {

        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            //object retorna lo indicado en paramters.putString("fields", "email") en este caso, solo contiene el email
            @Override
            public void onCompleted(final JSONObject object, GraphResponse response) {
                try {
                    linearLayout.setVisibility(View.GONE);
                    imageButtonSignOut.setVisibility(View.VISIBLE);
                    //Profile clase que contiene las características báscias de la cuenta de facebook (No retorna email)
                    Profile profileDefault = Profile.getCurrentProfile();
                    //Librería usada para poder mostrar la foto de perfil de facebook con una transformación circular
                    Picasso.with(LoginActivity.this).load(profileDefault.getProfilePictureUri(100, 100)).transform(new CircleTransform()).into(imageViewPhoto);
                    textViewFullName.setText(profileDefault.getLastName()+", "+profileDefault.getFirstName());
                    textViewEmail.setText(object.getString("email"));
                } catch (Exception e) {
                    Log.e("E-MainActivity", "getFaceBook" + e.toString());
                }
            }
        });
        Bundle parameters = new Bundle();
        //solicitando el campo email
        parameters.putString("fields", "email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Retorna la reppuesta después del ingreso de las credenciales de facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}




