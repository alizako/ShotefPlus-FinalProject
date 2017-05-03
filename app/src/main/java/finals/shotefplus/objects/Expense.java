package finals.shotefplus.objects;

import java.util.Date;

/**
 * Created by Aliza on 15/01/2017.
 */

public class Expense {
    private static long idNumGlobal = 0;
    //private long idNum = 0;
    private String idNum;
    private String date;
    private Work work;
    //private int paymentType;
    private double sumPayment;
    private String sumDetails;


    public Expense() {
        idNum = "";
        this.date = "";
        this.work = new Work();
        // this.paymentType = paymentType;
        this.sumPayment = 0;
        this.sumDetails = "";
    }

    // public Expense(String date, Work work, int paymentType, double sumPayment, String sumDetails) {
    public Expense(String date, Work work, double sumPayment, String sumDetails, String idNum) {
        this.idNum = idNum;
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
        if (date.contains("/"))
            this.date = date.substring(6, 10) + //year
                    date.substring(3, 5) + // month
                    date.substring(0, 2); //day
        else
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

    public String getIdNum() {
        return idNum;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }

    public String dateToString() {
        return date.substring(6, 8) + "/" + //day
                date.substring(4, 6) + "/" + //month
                date.substring(0, 4);//year
    }
}
