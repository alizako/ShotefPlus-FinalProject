package finals.shotefplus.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import finals.shotefplus.R;

public class AboutActivity extends AppCompatActivity {

    TextView txtAbout;
    Button btnClose;
    String strAbout= "אפליקציית שוטף פלוס מיועדת לשימוש עוסק שרוצה לנהל ולעקוב אחר ההכנסות השוטפות שלו.\n\n" +
            "האפליקציה מאפשרת לשמור נתוני לקוחות תוך יצירת קשר עמם, להנפיק הצעת מחיר ולהעבירה לביצוע. לנהל את הקבלות לפי עבודה שבוצעה ולשמור תמונה של קבלה שהמשתמש הפיק.\n\n" +
            "האפליקציה מציגה גרפים של הוצאות מול הכנסות ברמה השנתית וברמה החודשית, כך שהמשתמש יכול לדעת כמה כסף אמור להכנס לו בכל חודש תוך התחשבות בסוג התשלום: שוטף +30, +60...\n\n" +
            "המשתמש צריך להיות רשום למערכת וביכולתו להכנס עם פרטיו מכל מכשיר אנדרואיד.\n" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        txtAbout = (TextView) findViewById(R.id.txtAbout);
        txtAbout.setText(strAbout);

        btnClose = (Button) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
