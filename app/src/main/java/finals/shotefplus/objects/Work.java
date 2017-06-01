package finals.shotefplus.objects;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.PropertyName;

import java.util.List;

import finals.shotefplus.DataAccessLayer.FirebaseHandler;

/**
 * Created by Aliza on 15/01/2017.
 */

public class Work extends PriceOffer {
    private static long idNumGlobal = 0;
    //private long idNum = 0;
    private String priceOfferIdNum;
    private String idNum;
    //private PriceOffer priceOffer;
    private List<Expense> expenseList; //?????????????????????????? LOOP!!
    private boolean isWorkDone;
    private long receiptNum;
    private boolean isWorkCanceled;

    //
   // private String dateInsertion;
  //  private String dueDate;

    public Work() {
        idNum = "";
        this.priceOfferIdNum = "";
        this.isWorkCanceled = false;
        this.isWorkDone = false;
        this.receiptNum = 0;
       // this.dateInsertion = "";
       // this.dueDate = "";
    }

    public Work(String idNum, String priceOfferIdNum, boolean isWorkCanceled,
                boolean isWorkDone, long receiptNum,String dateInsertion, String dueDate) {
        //idNumGlobal++;
        this.idNum = idNum;
        this.priceOfferIdNum = priceOfferIdNum;
        this.isWorkCanceled = isWorkCanceled;
        this.isWorkDone = isWorkDone;
        this.receiptNum = receiptNum;
    //    this.dateInsertion = dateInsertion;
     //   this.dueDate = dueDate;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }

    public String getIdNum() {
        return idNum;
    }


    public String getPriceOfferIdNum() {
        return priceOfferIdNum;
    }

    public void setPriceOfferIdNum(String priceOfferIdNum) {
        this.priceOfferIdNum = priceOfferIdNum;
    }

    public List<Expense> getExpenseList() {
        return expenseList;
    }

    public void setExpenseList(List<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    public boolean isWorkDone() {
        return isWorkDone;
    }

    public void setWorkDone(boolean workDone) {
        isWorkDone = workDone;
    }

    public long getReceiptNum() {
        return receiptNum;
    }

    public void setReceiptNum(long receiptNum) {
        this.receiptNum = receiptNum;
    }

    public boolean isWorkCanceled() {
        return isWorkCanceled;
    }

    public void setWorkCanceled(boolean workCanceled) {
        isWorkCanceled = workCanceled;
    }

    @PropertyName("dateInsertion")
    public String getDateInsertion() {
        return super.getDateInsertion();
    }

    @PropertyName("dueDate")
    public String getDueDate() {
        return super.getDueDate();
    }

    @PropertyName("customerIdNum")
    public String getCustomerIdNum() {
        return super.getCustomerIdNum();
    }

    @PropertyName("sumPayment")
    public double getSumPayment() {
        return super.getSumPayment();
    }

    @PropertyName("workDetails")
    public String getWorkDetails() {
        return super.getWorkDetails();
    }

    @PropertyName("isPriceOfferSent")
    public boolean isPriceOfferSent() {
        return super.isPriceOfferSent();
    }

    @PropertyName("location")
    public String getLocation() {
        return super.getLocation();
    }

    @PropertyName("quantity")
    public long getQuantity() {
        return super.getQuantity();
    }

    @PropertyName("isSumPaymentMaam")
    public boolean isSumPaymentMaam() {
        return super.isSumPaymentMaam();
    }

    @PropertyName("isPriceOfferApproved")
    public boolean isPriceOfferApproved() {
        return super.isPriceOfferApproved();
    }


    public void setSumPayment(double sumPayment) {
        super.setSumPayment(sumPayment);
    }

    public void setDateInsertion(String dateInsertion) {
        super.setDateInsertion(dateInsertion);
    }

    public String dateInsertionToString() {return super.dateInsertionToString();}

    public void setCustomerIdNum(String customerIdNum) {
        super.setCustomerIdNum(customerIdNum);
    }

    public void setWorkDetails(String workDetails) {
        super.setWorkDetails(workDetails);
    }

    public void setPriceOfferSent(boolean priceOfferSent) {
        super.setPriceOfferSent(priceOfferSent);
    }

    public void setLocation(String location) {
        super.setLocation(location);
    }

    public void setDueDate(String dueDate) {
        super.setDueDate(dueDate);
    }

    public void setQuantity(long quantity) {
        super.setQuantity(quantity);
    }

    public void setSumPaymentMaam(boolean sumPaymentMaam) {
        super.setSumPaymentMaam(sumPaymentMaam);
    }

    public void setPriceOfferApproved(boolean priceOfferApproved) {
        super.setPriceOfferApproved(priceOfferApproved);
    }

    public String dueDateToString() {
        return super.dueDateToString();
    }
}
