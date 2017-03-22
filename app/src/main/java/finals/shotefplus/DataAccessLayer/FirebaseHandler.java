package finals.shotefplus.DataAccessLayer;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import finals.shotefplus.R;
import finals.shotefplus.objects.Customer;
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
        this.mDatabase =  FirebaseDatabase.getInstance().getReference();
    }

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

    public void insertCustomer(Customer customer) {
        mDatabase.child("Users").child(userId).child("Customers").push().setValue(customer);
    }
    public void insertExpense(Expense expense) {
        mDatabase.child("Users").child(userId).child("Expenses").push().setValue(expense);
    }
    public void insertReceipt(Receipt receipt) {
        mDatabase.child("Users").child(userId).child("Receipts").push().setValue(receipt);
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
    public void insertWork(Work work) {
        mDatabase.child("Users").child(userId).child("Works").push().setValue(work);
    }

    //******************** Update Objects ********************//
    public void updatePriceOffer(PriceOffer priceOffer, String key) {

        mDatabase.getDatabase().getInstance().getReference().child("Users").child(userId).
                child("PriceOffers").child(key).setValue(priceOffer);
        //mDatabase.child("Users").child(userId).child("PriceOffers").push().setValue(priceOffer);
    }

    //******************** Get Objects ********************//
    public PriceOffer getPriceOffer(String key) {

        mDatabase.getDatabase().getInstance().getReference().child("Users").child(userId).
                child("PriceOffers").child(key).getDatabase();
        return new PriceOffer();
        //mDatabase.child("Users").child(userId).child("PriceOffers").push().setValue(priceOffer);
    }


    //******************** Get Lists Objects ********************//

    public  List<PriceOffer> getPriceOffersList() {
        List<PriceOffer> priceOfferList = new ArrayList<PriceOffer>();

        mDatabase.getDatabase().getInstance().getReference().child("Users").child(userId).
                child("PriceOffers").getDatabase();

        return priceOfferList;

    }



}
