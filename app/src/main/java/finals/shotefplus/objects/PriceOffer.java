package finals.shotefplus.objects;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import finals.shotefplus.DataAccessLayer.FirebaseHandler;

/**
 * Created by Aliza on 15/01/2017.
 */

public class PriceOffer {
    private static long idNumGlobal = 0;
    //private long idNum = 0;
    private String idNum;
    private String dateInsertion; //automatic
    private String location;
    private String dueDate;
    private String customerIdNum;
    // private Customer customer;//contact: name, phone, type,
    private String workDetails;
    private long quantity;
    //    private int paymentType;
    private double sumPayment;
    private boolean isSumPaymentMaam; // is payment includes Maam
    //private String comments;
    private boolean isPriceOfferSent;
    private boolean isPriceOfferApproved; // if yes-> need to add to work list

    public PriceOffer() {
        this.idNum = "";
        this.dateInsertion = "";
        this.location = "";
        this.dueDate = "";
        //this.customer = "";
        this.workDetails = "";
        this.quantity = 0;
        this.sumPayment = 0;
        this.isSumPaymentMaam = true;
        this.isPriceOfferSent = false;
        this.isPriceOfferApproved = false;
    }

    public PriceOffer(String idNum, String dateInsertion, String location,
                      String dueDate, String customerIdNum, String workDetails,
                      long quantity, double sumPayment, boolean isSumPaymentMaam,
                      boolean isPriceOfferSent, boolean isPriceOfferApproved) {
        this.idNum = idNum;
        this.dateInsertion = dateInsertion;
        this.location = location;
        this.dueDate = dueDate;
        this.customerIdNum = customerIdNum;
        this.workDetails = workDetails;
        this.quantity = quantity;
        this.sumPayment = sumPayment;
        this.isSumPaymentMaam = isSumPaymentMaam;
        this.isPriceOfferSent = isPriceOfferSent;
        this.isPriceOfferApproved = isPriceOfferApproved;
    }


    /*public long getIdNum() {
        return idNum;
    }*/
    public String getIdNum() {
        return idNum;
    }

    public String getDateInsertion() {
        return dateInsertion;
    }

    public void setDateInsertion(String dateInsertion) {
        if (dateInsertion.contains("/"))
            this.dateInsertion = dateInsertion.substring(6, 10) + //year
                    dateInsertion.substring(3, 5) + // month
                    dateInsertion.substring(0, 2); //day
        else
            this.dateInsertion = dateInsertion;
    }

    public String dateInsertionToString() {
        return dateInsertion.substring(6, 8) + "/" + //day
                dateInsertion.substring(4, 6) + "/" + //month
                dateInsertion.substring(0, 4);//year
    }

    /*public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }*/

    public String getCustomerIdNum() {
        return customerIdNum;
    }

    public void setCustomerIdNum(String customerIdNum) {
        this.customerIdNum = customerIdNum;
    }

    public double getSumPayment() {
        return sumPayment;
    }

    public void setSumPayment(double sumPayment) {
        this.sumPayment = sumPayment;
    }

    /*public double getSumPaymentMaam() {
        return sumPaymentMaam;
    }

    public void setSumPaymentMaam(double sumPaymentMaam) {
        this.sumPaymentMaam = sumPaymentMaam;
    }*/

    public String getWorkDetails() {
        return workDetails;
    }

    public void setWorkDetails(String workDetails) {
        this.workDetails = workDetails;
    }

    public boolean isPriceOfferSent() {
        return isPriceOfferSent;
    }

    public void setPriceOfferSent(boolean priceOfferSent) {
        isPriceOfferSent = priceOfferSent;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDueDate() {
        return dueDate;
    }


    //Sets date from format dd/MM/yyyy
    public void setDueDate(String dueDate) {
        if (dueDate.contains("/"))
            this.dueDate = dueDate.substring(6, 10) + //year
                    dueDate.substring(3, 5) + // month
                    dueDate.substring(0, 2); //day
        else
            this.dueDate = dueDate;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public boolean isSumPaymentMaam() {
        return isSumPaymentMaam;
    }

    public void setSumPaymentMaam(boolean sumPaymentMaam) {
        isSumPaymentMaam = sumPaymentMaam;
    }

    public boolean isPriceOfferApproved() {
        return isPriceOfferApproved;
    }

    public void setPriceOfferApproved(boolean priceOfferApproved) {
        isPriceOfferApproved = priceOfferApproved;
    }


    public String dueDateToString() {
        return dueDate.substring(6, 8) + "/" + //day
                dueDate.substring(4, 6) + "/" + //month
                dueDate.substring(0, 4);//year
    }

   /* public Customer getCustomerByID() {
        Customer customer = new Customer();

        customer = FirebaseHandler.getInstance(
                FirebaseAuth.getInstance().getCurrentUser().getUid()).getCustomerById(customerIdNum);


        return customer;
    }*/
}
