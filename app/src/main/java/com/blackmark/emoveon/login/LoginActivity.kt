package com.blackmark.emoveon.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.blackmark.emoveon.R
import com.blackmark.emoveon.clases.User
import com.blackmark.emoveon.dashboard.DashboardActivity
import com.blackmark.emoveon.register.register
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.google.firebase.database.*


class LoginActivity : AppCompatActivity() {

    //Inicialización de la variable de autenticación de Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var ref: DatabaseReference
    //Inicialización del Gestor de llamadas
    private var cbm = CallbackManager.Factory.create()
    //Intanciación de variables de Google SignIn
    private val RC_SIGN_IN: Int = 1
    private lateinit var gsic: GoogleSignInClient
    private lateinit var gsio: GoogleSignInOptions
    private lateinit var email: String
    private lateinit var user: User

    //Metodo ejecutado al iniciar la Activity
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            startActivity(
                Intent(this, DashboardActivity::class.java)
            )
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        //Initializa Firebase Database
        database = FirebaseDatabase.getInstance()
        ref = database.reference
        //Instanciación de la Activity y su Layout
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        googleSetUp()
        setupUI()
    }

    // ========================= AUTENTICACIÓN CON FACEBOOK Y FIREBASE ============================
    fun signInFacebook(facebookBtn: LoginButton) {
        facebookBtn.registerCallback(cbm, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("SUCCESS", "facebook:onSuccess:$loginResult")
                setFacebookData(loginResult)
                firebaseAuthWithFacebook(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d("CANCEL", "facebook:onCancel")
                google_button.isEnabled = true
                facebook_button.isEnabled = true
            }

            override fun onError(error: FacebookException) {
                Log.d("ERROR", "facebook:onError", error)
                google_button.isEnabled = true
                facebook_button.isEnabled = true
            }
        })
    }

    private fun setFacebookData(loginResult: LoginResult) {
        val request = GraphRequest.newMeRequest(
            loginResult.accessToken
        ) { `object`, response ->
            try {
                Log.i("Response", response.toString())
                email = response.jsonObject.getString("email")
            } catch (e: JSONException) {

            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,email,first_name,last_name")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun firebaseAuthWithFacebook(token: AccessToken) {
        Log.d("TOKEN", "handleFacebookAccessToken:$token")
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SUCCESS", "signInWithCredential:success")
                    storeDatabaseUser()
                } else {
                    google_button.isEnabled = true
                    facebook_button.isEnabled = true
                    Log.w("ERROR", "Error al iniciar sesion", task.exception)
                    Toast.makeText(this, "Facebook sign in failed :(", Toast.LENGTH_LONG)
                        .show()
                    val currentUser = null
                    LoginManager.getInstance().logOut()
                }
            }

    }
    //=========================================================================================


    //===================== INICIO DE SESION CON GOOGLE Y FIREBASE ============================
    private fun googleSetUp() {
        gsio = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        gsic = GoogleSignIn.getClient(this, gsio)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener() {
                if (it.isSuccessful) {
                    Log.d("SUCCESS", "signInWithCredential:success")
                    storeDatabaseUser()
                } else {
                    google_button.isEnabled = true
                    facebook_button.isEnabled = true
                    Log.w("ERROR", "signInWithCredential:failure", it.exception)
                    Toast.makeText(
                        this,
                        "No se ha podido iniciar sesión",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun signInGoogle() {
        val signInIntent: Intent = gsic.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    //================================================================================

    // ============ METODO SOBREESCRITO QUE RECIBE EL ACTIVITY RESULT ============================
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                google_button.isEnabled = true
                facebook_button.isEnabled = true
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
                Log.w("ERROR", "Google sign in failed", e)
            }
        } else {
            cbm.onActivityResult(requestCode, resultCode, data)
        }

    }

    fun onClickRegister(view: View) {
        val intent = Intent(this, register::class.java)
        startActivity(intent)
    }

    private fun setupUI() {
        google_button.setOnClickListener {
            google_button.isEnabled = false
            facebook_button.isEnabled = false
            signInGoogle()
        }
        facebook_button.setOnClickListener {
            facebook_button.isEnabled = false
            google_button.isEnabled = false
            signInFacebook(facebook_button)
        }
    }

    //Función encargada de agregar un nodo a Realtime Database con la información del usuario
    fun storeDatabaseUser() {
        val currentUser = auth.currentUser //Declarando el usuario obtenido de Firebase
        //Obtencion de el usuario, si este existe, si no, lo crea en la RealTimeDatabase
        ref.child("users").child(currentUser!!.uid)//Se declara la referencia, en el nodo Users, y en el nodo con ID del User se insertara el objeto
            .addValueEventListener(object : ValueEventListener {//Evento de escucha
                override fun onDataChange(p0: DataSnapshot) {//Se toma una captura de los datos en el nodo
                    if (!p0.exists()) {//Si no existe el usuario en la Realtime DB, lo crea y lo almacena
                        user = User(
                            currentUser.uid,
                            currentUser.displayName,
                            currentUser.email
                        )
                        ref.child("users").child(user.userId.toString()).setValue(user)//Referencia -> Nodo Users -> Nodo User ID -> User Object
                    } else {
                        //Si ya existe el usuario, se inicia el intent del Dashboard y se culmina this
                        startActivity(
                            Intent(
                                this@LoginActivity,
                                DashboardActivity::class.java
                            )
                        )
                        finish()
                    }
                }
                
                override fun onCancelled(p0: DatabaseError) {}
            })
    }

}
