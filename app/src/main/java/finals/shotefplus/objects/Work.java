package finals.shotefplus.objects;

import java.util.List;

/**
 * Created by Aliza on 15/01/2017.
 */

public class Work {
    private static long idNumGlobal = 0;
    private long idNum = 0;
    private PriceOffer priceOffer;
    private List<Expense>expenseList;
    private boolean isWorkDone;
    private long receiptNum;
    private boolean isWorkCanceled;


    public long getIdNum() {
        return idNum;
    }

    public Work() {
        idNumGlobal++;
        idNum = idNumGlobal;
    }
    public Work( PriceOffer priceOffer, boolean isWorkCanceled,
               boolean isWorkDone, long receiptNum) {
        idNumGlobal++;
        idNum = idNumGlobal;
        this.priceOffer=priceOffer;
        this.isWorkCanceled=isWorkCanceled;
        this.isWorkDone=isWorkDone;
        this.receiptNum=receiptNum;
    }

    public void setIdNum(long idNum) {
        this.idNum = idNum;
    }

    public PriceOffer getPriceOffer() {
        return priceOffer;
    }

    public void setPriceOffer(PriceOffer priceOffer) {
        this.priceOffer = priceOffer;
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
}
