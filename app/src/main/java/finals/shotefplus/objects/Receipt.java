package finals.shotefplus.objects;

import java.util.Date;

/**
 * Created by Aliza on 15/01/2017.
 */

public class Receipt {
    private static long idNumGlobal = 0;
    private long idNum = 0;
    private String date;
    private Customer customer;
    private String workDetails;
    private int paymentType;
    private double sumPayment;
    private double sumPaymentMaam;


    public Receipt(Customer customer, int paymentType, String workDetails, String date,
                   double sumPayment, double sumPaymentMaam) {
        idNumGlobal++;
        idNum=idNumGlobal;
        this.customer = customer;
        this.paymentType = paymentType;
        this.workDetails = workDetails;
        this.sumPayment = sumPayment;
        this.sumPaymentMaam = sumPaymentMaam;
        this.date=date;
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

    public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    public String getWorkDetails() {
        return workDetails;
    }

    public void setWorkDetails(String workDetails) {
        this.workDetails = workDetails;
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


}
