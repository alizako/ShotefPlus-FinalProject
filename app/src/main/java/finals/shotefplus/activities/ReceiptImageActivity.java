package finals.shotefplus.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import finals.shotefplus.R;

public class ReceiptImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_image);

        final TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        final ImageView imgReceiptPicture = (ImageView) findViewById(R.id.imgReceiptPicture);
        Button btnClose = (Button) findViewById(R.id.btnClose);

        final ProgressDialog dialog = ProgressDialog.show(ReceiptImageActivity.this,
                "",
                "טוען תמונה..",
                true);

        Intent intent = getIntent();
        boolean isFromStorage = intent.getBooleanExtra("isFromStorage", false);

        if (isFromStorage) {

            String receiptPicId = intent.getStringExtra("receiptPictureIdNum");
            final String receiptNum = tvTitle.getText() + " - " + intent.getStringExtra("receiptNum");

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef =
                    storage.getReferenceFromUrl("gs://shotefplus-72799.appspot.com/" + firebaseAuth.getCurrentUser().getUid())
                            .child(receiptPicId + ".jpg");

            final long ONE_MEGABYTE = 1024 * 1024;
            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    /*Bitmap bitmapScaled = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
                    Drawable drawable = new BitmapDrawable(bitmapScaled);
                    imgReceiptPicture.setBackgroundDrawable(drawable);*/
                    imgReceiptPicture.setScaleType(ImageView.ScaleType.FIT_XY);
                    imgReceiptPicture.setAdjustViewBounds(true);
                    imgReceiptPicture.setImageBitmap(bitmap);
                    tvTitle.setText(receiptNum);
                    dialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    setResult(Activity.RESULT_CANCELED, new Intent());
                    dialog.dismiss();
                    finish();
                }
            });
        } else // isFromStorage ==false
        {
            try {
                Uri filePath = intent.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgReceiptPicture.setImageBitmap(bitmap);
                tvTitle.setText("");
                dialog.dismiss();

            } catch (Exception e) {
                setResult(Activity.RESULT_CANCELED, new Intent());
                dialog.dismiss();
                finish();
            }
        }


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_OK, new Intent());
                finish();
            }
        });

    }
}
