package finals.shotefplus.objects;

import java.util.Date;

/**
 * Created by Aliza on 15/01/2017.
 */

public class Receipt {
    private static long idNumGlobal = 0;
    private long idNum = 0;
    private String date;
    //private Customer customer;
    private Work work; //+Customer
    private String workDetails;
    private int paymentType;//1-cash, 2-credit card, 3-cheque,4-transfer...
    private int paymentMethod; //+30,+60..120
    private double sumPayment;
    private double sumPaymentMaam;
    private boolean isPaymentReceived;
    private boolean isPaymentCanceled;


    public Receipt(int paymentMethod,int paymentType,  String workDetails, String date,
                   double sumPayment, double sumPaymentMaam, Work work) {
        idNumGlobal++;
        idNum=idNumGlobal;
       // this.customer = customer;
        this.paymentType = paymentType;
        this.workDetails = workDetails;
        this.sumPayment = sumPayment;
        this.sumPaymentMaam = sumPaymentMaam;
        this.date=date;
        this.work=work;
    }

    public long getIdNum() {
        return idNum;
    }

    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

   /* public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }*/

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

    public String getStringPaymentType (int paymentType){
        switch (paymentType) {
            case 1:
                return "מזומן";
            case 2:
                return "אשראי";
            case 3:
                return "המחאה";
            case 4:
                return "העברה";
        }
        return "";
    }

}
