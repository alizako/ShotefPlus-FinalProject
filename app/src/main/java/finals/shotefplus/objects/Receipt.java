package finals.shotefplus.objects;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

import finals.shotefplus.DataAccessLayer.FirebaseHandler;

/**
 * Created by Aliza on 15/01/2017.
 */

public class Receipt {
    private static long idNumGlobal = 0;
    private String idNum;
    private String receiptNum;
    private String dateReceipt;
    //private Customer customer;
    private String workIdNum; //+Customer
    private String customerIdNum;
    private String dealDetails;
    private int paymentType;//1-cash, 2-credit card, 3-cheque,4-transfer...
    private int paymentMethod; //+30,+60..120
    private double sumPayment;
    private boolean isSumPaymentMaam;
    private boolean isPaymentReceived;
    private boolean isPaymentCanceled;
    private boolean isPaid;

    public Receipt() {
        //idNumGlobal++;
        idNum = "";
        this.paymentType = 0;
        this.paymentMethod = 0;
        this.dealDetails = "";
        this.sumPayment = 0;
        this.isSumPaymentMaam = false;
        this.dateReceipt = "";
        this.workIdNum="";
        this.isPaymentCanceled = false;
        this.isPaymentReceived = false;
        this.isPaid=false;
    }

    public Receipt(String idNum, int paymentMethod, int paymentType, String workDetails, String dateReceipt,
                   double sumPayment, boolean isSumPaymentMaam, String workIdNum, String customerIdNum, boolean isPaymentReceived,
                   boolean isPaymentCanceled, boolean isPaid) {

        idNum = idNum;
        // this.customer = customer;
        this.paymentMethod = paymentMethod;
        this.paymentType = paymentType;
        this.dealDetails = workDetails;
        this.sumPayment = sumPayment;
        this.isSumPaymentMaam = isSumPaymentMaam;
        this.dateReceipt = dateReceipt;
        this.workIdNum = workIdNum;
        this.customerIdNum = customerIdNum;
        this.isPaymentCanceled = isPaymentCanceled;
        this.isPaymentReceived = isPaymentReceived;
        this.isPaid=isPaid;
    }

    public String getIdNum() {
        return idNum;
    }

    public String getReceiptNum() {
        return receiptNum;
    }

    public void setReceiptNum(String receiptNum) {
        this.receiptNum = receiptNum;
    }
    public String getCustomerIdNum() {
        return customerIdNum;
    }

    public void setCustomerIdNum(String customerIdNum) {
        this.customerIdNum = customerIdNum;
    }

   /* public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }*/

   /* public String getDate() {
        return dateReceipt;
    }

    public void setDate(String date) {
        this.dateReceipt = date;
    }
*/
   /* public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }*/

    public String getWorkIdNum() {
        return workIdNum;
    }

    public void setWorkIdNum(String workidNum) {
        this.workIdNum = workidNum;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    public String getDealDetails() {
        return dealDetails;
    }

    public void setDealDetails(String workDetails) {
        this.dealDetails = workDetails;
    }

    public double getSumPayment() {
        return sumPayment;
    }

    public void setSumPayment(double sumPayment) {
        this.sumPayment = sumPayment;
    }


    public String getStringPaymentType(int paymentType) {
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

    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }

    public boolean isPaymentReceived() {
        return isPaymentReceived;
    }

    public void setPaymentReceived(boolean paymentReceived) {
        isPaymentReceived = paymentReceived;
    }

    public boolean isPaymentCanceled() {
        return isPaymentCanceled;
    }

    public void setPaymentCanceled(boolean paymentCanceled) {
        isPaymentCanceled = paymentCanceled;
    }

    public void setDateReceipt(String dateReceipt) {
        if (dateReceipt.contains("/"))
            this.dateReceipt = dateReceipt.substring(6, 10) + //year
                    dateReceipt.substring(3, 5) + // month
                    dateReceipt.substring(0, 2); //day
        else
            this.dateReceipt = dateReceipt;
    }

    public String dateReceiptToString() {
        return dateReceipt.substring(6, 8) + "/" + //day
                dateReceipt.substring(4, 6) + "/" + //month
                dateReceipt.substring(0, 4);//year
    }

    public int getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(int paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public boolean isSumPaymentMaam() {
        return isSumPaymentMaam;
    }

    public void setSumPaymentMaam(boolean sumPaymentMaam) {
        isSumPaymentMaam = sumPaymentMaam;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public String getDateReceipt() {
        return dateReceipt;
    }
}
