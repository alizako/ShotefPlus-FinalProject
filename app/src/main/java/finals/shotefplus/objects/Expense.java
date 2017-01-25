package finals.shotefplus.objects;

import java.util.Date;

/**
 * Created by Aliza on 15/01/2017.
 */

public class Expense {
    private static long idNum = 0;
    private String date;
    private Work work;
    //private int paymentType;
    private double sumPayment;
    private String sumDetails;

    public Expense(String date, Work work, int paymentType, double sumPayment, String sumDetails) {
        idNum++;
        this.date = date;
        this.work = work;
       // this.paymentType = paymentType;
        this.sumPayment = sumPayment;
        this.sumDetails = sumDetails;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    /*public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }*/

    public double getSumPayment() {
        return sumPayment;
    }

    public void setSumPayment(double sumPayment) {
        this.sumPayment = sumPayment;
    }

    public String getSumDetails() {
        return sumDetails;
    }

    public void setSumDetails(String sumDetails) {
        this.sumDetails = sumDetails;
    }
}
