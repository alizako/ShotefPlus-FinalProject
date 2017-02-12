package finals.shotefplus.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import finals.shotefplus.R;

public class SignInActivity extends AppCompatActivity {

    Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        btnSignIn = (Button) findViewById(R.id.btnIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callMainActivity();
            }
        });
    }

    private void callMainActivity(){
        Toast.makeText(this,R.string.signInToast , Toast.LENGTH_LONG).show();
        /*Toast toast = Toast.makeText(this, R.string.signInToast, Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        v.getShadowColor(Color.RED);
        toast.show();*/
        Intent intent = new Intent(SignInActivity.this,MainActivity.class);
        startActivity(intent);
    }
}
