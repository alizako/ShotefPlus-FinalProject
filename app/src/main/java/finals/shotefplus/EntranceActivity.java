package finals.shotefplus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.View;
import android.widget.Button;

public class EntranceActivity extends AppCompatActivity {

    Button btnSignIn;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);

        btnSignIn = (Button) findViewById(R.id.btnIn);
        btnSignUp = (Button) findViewById(R.id.btnUp);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

    }

    private void signIn(){
        Intent intent = new Intent(EntranceActivity.this,MainActivity.class);
        startActivity(intent);
    }

    private void signUp(){
        Intent intent = new Intent(EntranceActivity.this,SignUpActivity.class);
        startActivity(intent);
    }


}
