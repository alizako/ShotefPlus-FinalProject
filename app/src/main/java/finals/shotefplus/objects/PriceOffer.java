package finals.shotefplus.objects;

import java.util.Date;

/**
 * Created by Aliza on 15/01/2017.
 */

public class PriceOffer {
    private static long idNumGlobal = 0;
    private long idNum = 0;
    private Date date;
    private Customer customer;
    //    private int paymentType;
    private double sumPayment;
    private double sumPaymentMaam;
    private String workDetails;

    public PriceOffer(Customer customer, double sumPayment, double sumPaymentMaam, String workDetails) {
        idNumGlobal++;
        idNum=idNumGlobal;
        this.customer = customer;
        this.sumPayment = sumPayment;
        this.sumPaymentMaam = sumPaymentMaam;
        this.workDetails = workDetails;
    }

    public long getIdNum() {
        return idNum;
    }
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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

    public double getSumPaymentMaam() {
        return sumPaymentMaam;
    }

    public void setSumPaymentMaam(double sumPaymentMaam) {
        this.sumPaymentMaam = sumPaymentMaam;
    }

    public String getWorkDetails() {
        return workDetails;
    }

    public void setWorkDetails(String workDetails) {
        this.workDetails = workDetails;
    }
}
