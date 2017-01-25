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
}
