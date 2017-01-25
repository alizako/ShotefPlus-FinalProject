package finals.shotefplus.objects;

import java.util.Date;

/**
 * Created by Aliza on 15/01/2017.
 */

public class PriceOffer {
    private static long idNumGlobal = 0;
    private long idNum = 0;
    private String date;
    private String location;
    private String dueDate;
    private Customer customer;//contact: name, phone, type,
    private String workDetails;
    private long quantity;
    //    private int paymentType;
    private double sumPayment;
    private boolean isSumPaymentMaam; // is payment includes Maam
    private String comments;
    private boolean isPriceOfferSent;
    private boolean isPriceOfferApproved; // if yes-> need to add to work list

    public PriceOffer(Customer customer, double sumPayment, double sumPaymentMaam,
                      String workDetails,String date,boolean isPriceOfferSent ) {
        idNumGlobal++;
        idNum=idNumGlobal;
        this.customer = customer;
        this.sumPayment = sumPayment;
       // this.sumPaymentMaam = sumPaymentMaam;
        this.workDetails = workDetails;
        this.isPriceOfferSent = isPriceOfferSent;
    }

    public long getIdNum() {
        return idNum;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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
}
