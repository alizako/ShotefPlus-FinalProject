package finals.shotefplus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.View;
import android.widget.Button;

public class EntranceActivity extends AppCompatActivity {

    Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);

        btnSignIn = (Button) findViewById(R.id.btnIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callMainActivity();
            }
        });
    }

    private void callMainActivity(){
        Intent intent = new Intent(EntranceActivity.this,MainActivity.class);
        startActivity(intent);
    }



}
