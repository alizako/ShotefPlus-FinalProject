package finals.shotefplus.objects;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    private boolean isPicReceiptExist;

    //calculated fields
    private int monthPay;
    private int yearPay;
    private double paymentWithMaam;

    public Receipt() {
        //idNumGlobal++;
        idNum = "";
        this.paymentType = 0;
        this.paymentMethod = 0;
        this.dealDetails = "";
        this.sumPayment = 0;
        this.isSumPaymentMaam = false;
        this.dateReceipt = "";
        this.workIdNum = "";
        this.isPaymentCanceled = false;
        this.isPaymentReceived = false;
        this.isPaid = false;
        isPicReceiptExist = false;
    }

    public Receipt(String idNum, int paymentMethod, int paymentType, String workDetails, String dateReceipt,
                   double sumPayment, boolean isSumPaymentMaam, String workIdNum, String customerIdNum, boolean isPaymentReceived,
                   boolean isPaymentCanceled, boolean isPaid, boolean isPicReceiptExist) {

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
        this.isPaid = isPaid;
        this.isPicReceiptExist = isPicReceiptExist;
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

    public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
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

    public boolean isPicReceiptExist() {
        return isPicReceiptExist;
    }

    public void setPicReceiptExist(boolean picReceiptExist) {
        isPicReceiptExist = picReceiptExist;
    }

    @PropertyName("monthPay")
    public int getMonthPay() {
        return monthPay;
    }

    @PropertyName("yearPay")
    public int getYearPay() {
        return yearPay;
    }

    @PropertyName("getPaymentWithMaam")
    public double getPaymentWithMaam() {
        return paymentWithMaam;
    }

    @Exclude
    public void setDueDatePayment() {
        if (this.paymentMethod > 0) {
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            Date dateFormatted = new Date();
            try {
                dateFormatted = dateFormat.parse(this.dateReceipt);
            } catch (ParseException pe) {
               // throw new Exception("") ;
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateFormatted);
            cal.add(Calendar.DATE, this.paymentMethod);

            String strDate = dateFormat.format(cal.getTime());

            this.monthPay = Integer.parseInt(strDate.substring(4, 6));
            this.yearPay = Integer.parseInt(strDate.substring(0, 4));
        } else {
            this.monthPay = Integer.parseInt(this.dateReceipt.substring(4, 6));
            this.yearPay = Integer.parseInt(this.dateReceipt.substring(0, 4));
        }
    }

    public void setPaymentWithMaam() {
        if (this.isSumPaymentMaam)
            this.paymentWithMaam = this.sumPayment * 1.17;
    }
}
