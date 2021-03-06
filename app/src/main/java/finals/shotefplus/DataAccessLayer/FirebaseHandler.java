package finals.shotefplus.DataAccessLayer;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import finals.shotefplus.R;
import finals.shotefplus.objects.Customer;
import finals.shotefplus.objects.EmailTemplate;
import finals.shotefplus.objects.Expense;
import finals.shotefplus.objects.PriceOffer;
import finals.shotefplus.objects.Receipt;
import finals.shotefplus.objects.UserProfile;
import finals.shotefplus.objects.Work;

/**
 * Created by Aliza on 08/03/2017.
 */

public class FirebaseHandler {

    private DatabaseReference mDatabase;
    private static String userId;

    private FirebaseHandler() {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }


    //Objects:
    Customer customer;
    PriceOffer priceOffer;
    Work work;
    boolean flag;

    //Singleton
    private static class SingletonFirebaseHandler {
        private static final FirebaseHandler INSTANCE = new FirebaseHandler();
    }

    public static FirebaseHandler getInstance(String userId1) {
        userId = userId1;
        return SingletonFirebaseHandler.INSTANCE;
    }
    //eof singleton


    //******************** Insert New Objects ********************//
    public void insertUserProfile(UserProfile userProfile) {
        mDatabase.child("Users").child(userId).child("UserProfile").setValue(userProfile);
        // mDatabase.child("Users").child(userId).child("Customers").setValue(userProfile);
        // mDatabase.child("users").child(userId).setValue(userProfile);
        // mDatabase.child("profile").child(userId).setValue(userProfile);
    }

    public String insertCustomer(Customer customer) {
        DatabaseReference tmp = mDatabase.child("Users").child(userId).child("Customers").push();//blank child
        String key = tmp.getKey();
        customer.setIdNum(key);
        tmp.setValue(customer);
        return key;
        //mDatabase.child("Users").child(userId).child("Customers").push().setValue(customer);
    }

    public String insertExpense(Expense expense) {
        DatabaseReference tmp = mDatabase.child("Users").child(userId).child("Expenses").push();//blank child
        String key = tmp.getKey();
        expense.setIdNum(key);
        tmp.setValue(expense);
        //return mDatabase.getKey();
        return key;
    }

    public String insertReceipt(Receipt receipt) {
        DatabaseReference tmp = mDatabase.child("Users").child(userId).child("Receipts").push();//blank child
        String key = tmp.getKey();
        receipt.setIdNum(key);
        tmp.setValue(receipt);
        //return mDatabase.getKey();
        return key;
    }

    public String insertPriceOffer(PriceOffer priceOffer) {
        //mDatabase.child("Users").child(userId).child("PriceOffers").push().setValue(priceOffer);
        DatabaseReference tmp = mDatabase.child("Users").child(userId).child("PriceOffers").push();//blank child
        String key = tmp.getKey();
        priceOffer.setIdNum(key);
        tmp.setValue(priceOffer);
        //return mDatabase.getKey();
        return key;
    }

    public String insertWork(Work work) {
        DatabaseReference tmp = mDatabase.child("Users").child(userId).child("Works").push();//blank child
        String key = tmp.getKey();
        work.setIdNum(key);
        tmp.setValue(work);
        return key;
    }

    //******************** Update Objects ********************//
   /* public boolean changeCredential(final String newPassword,final String newEmail,final FirebaseUser user ){

      // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

       // user.updatePassword(newPassword);

           boolean isComplete = false;
        AuthCredential credential = EmailAuthProvider
                .getCredential(newEmail, newPassword);

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        isComplete= true;
                                    } else {
                                        Log.d(TAG, "Error password not updated")
                                    }
                                }
                            });
                        } else {
                            Log.d(TAG, "Error auth failed")
                        }
                    }
                });

        return true;
    }*/


    public void updateUserProfile(UserProfile userProfile) {
        mDatabase.getDatabase().getInstance().getReference().child("Users").child(userId).
                child("UserProfile").setValue(userProfile);
    }

    public void updatePriceOffer(PriceOffer priceOffer, String key) {
        mDatabase.getDatabase().getInstance().getReference().child("Users").child(userId).
                child("PriceOffers").child(key).setValue(priceOffer);
        //mDatabase.child("Users").child(userId).child("PriceOffers").push().setValue(priceOffer);
    }

    public void updateWork(Work work, String key) {
        mDatabase.getDatabase().getInstance().getReference().child("Users").child(userId).
                child("Works").child(key).setValue(work);
    }
    public void updateReceipt(Receipt receipt , String key) {
        mDatabase.getDatabase().getInstance().getReference().child("Users").child(userId).
                child("Receipts").child(key).setValue(receipt);
    }

    public void updateExpense(Expense expense, String key) {
        mDatabase.getDatabase().getInstance().getReference().child("Users").child(userId).
                child("Expenses").child(key).setValue(expense);
    }

    public void updateCustomer(Customer customer, String key) {
        mDatabase.getDatabase().getInstance().getReference().child("Users").child(userId).
                child("Customers").child(key).setValue(customer);
    }

    public void updateBusinessType(String type) {
        mDatabase.getDatabase().getInstance().getReference().child("Users").child(userId).
                child("BusinessType").setValue(type);
    }

    public void updateMsgTemplate(String msg) {
        mDatabase.getDatabase().getInstance().getReference().child("Users").child(userId).
                child("MsgTemplate").setValue(msg);
    }

    public void updateEmailTemplate(EmailTemplate emailTemplate) {
        mDatabase.getDatabase().getInstance().getReference().child("Users").child(userId).
                child("EmailTemplate").setValue(emailTemplate);
    }

    //******************** Delete Objects ********************//
    public void deleteReceipt(Receipt receipt , String key) {
        mDatabase.getDatabase().getInstance().getReference().child("Users").child(userId).
                child("Receipts").child(key).setValue(null);
    }

    //******************** Get Objects ********************//
    public FirebaseDatabase getUserProfile() {

        return mDatabase.getDatabase().getInstance().getReference().child("Users").child(userId).
                child("UserProfile").getDatabase();
        //return new PriceOffer();

    }


    public PriceOffer getPriceOffer(String key) {

        mDatabase.getDatabase().getInstance().getReference().child("Users").child(userId).
                child("PriceOffers").child(key).getDatabase();
        return new PriceOffer();
        //mDatabase.child("Users").child(userId).child("PriceOffers").push().setValue(priceOffer);
    }


    //******************** Get Lists Objects ********************//

    public List<PriceOffer> getPriceOffersList() {
        List<PriceOffer> priceOfferList = new ArrayList<PriceOffer>();

        mDatabase.getDatabase().getInstance().getReference().child("Users").child(userId).
                child("PriceOffers").getDatabase();

        return priceOfferList;

    }

    //******************** Get Object By ID  ********************//
    public PriceOffer getPriceOfferById(String priceOfferIdNum) {
        DatabaseReference dbRef = mDatabase.child("Users").child(userId).child("PriceOffers");
        priceOffer = new PriceOffer();

        dbRef.orderByChild("idNum").startAt(priceOfferIdNum).endAt(priceOfferIdNum)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                            try {
                                priceOffer = postSnapshot.getValue(PriceOffer.class);

                            } catch (Exception ex) {
                                //Toast.makeText(getBaseContext(), "ERROR: " + ex.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        //   Toast.makeText(getBaseContext(), "ERROR: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                        // dialog.dismiss();
                    }
                });
        return priceOffer;
    }

    public Work getWorkById(String workIdNum) {

        DatabaseReference dbRef = mDatabase.child("Users").child(userId).child("Works");
        work = new Work();

        dbRef.orderByChild("idNum").startAt(workIdNum).endAt(workIdNum)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                            try {
                                work = postSnapshot.getValue(Work.class);

                            } catch (Exception ex) {
                                //Toast.makeText(getBaseContext(), "ERROR: " + ex.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        //   Toast.makeText(getBaseContext(), "ERROR: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                        // dialog.dismiss();
                    }
                });
        return work;

    }


}
