package finals.shotefplus.objects;

import java.util.Date;

/**
 * Created by Aliza on 15/01/2017.
 */

public class Expense {
    private static long idNumGlobal = 0;
    //private long idNum = 0;
    private String idNum;
    private String dateExpense;
    private int expenseType;
    private double sumPayment;
    private String sumDetails;
    private String workIdNum;


    public Expense() {
        idNum = "";
        this.dateExpense = "";
        this.expenseType = 0;
        this.sumPayment = 0;
        this.sumDetails = "";
        this.workIdNum="";
    }

    // public Expense(String date, Work work, int paymentType, double sumPayment, String sumDetails) {
    public Expense(String dateExpense, double sumPayment,
                   String sumDetails, String idNum, int expenseType, String workIdNum) {
        this.idNum = idNum;
        this.dateExpense = dateExpense;
        // this.paymentType = paymentType;
        this.sumPayment = sumPayment;
        this.sumDetails = sumDetails;
        this.expenseType =expenseType;
        this.workIdNum = workIdNum;
    }

    public String getDate() {
        return dateExpense;
    }

    public void setDate(String dateExpense) {
        if (dateExpense.contains("/"))
            this.dateExpense = dateExpense.substring(6, 10) + //year
                    dateExpense.substring(3, 5) + // month
                    dateExpense.substring(0, 2); //day
        else
            this.dateExpense = dateExpense;
    }
    public String dateToString() {
        return dateExpense.substring(6, 8) + "/" + //day
                dateExpense.substring(4, 6) + "/" + //month
                dateExpense.substring(0, 4);//year
    }

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

    public String getStringExpenseType(int expenseType) {
        return expenseTypeToString(expenseType);
    }

       public String expenseTypeToString(){
        return expenseTypeToString(expenseType);
    }

    private String expenseTypeToString(int expenseType){
        switch (expenseType) {
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


    public int getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(int expenseType) {
        this.expenseType = expenseType;
    }
    public String getWorkIdNum() {
        return workIdNum;
    }

    public void setWorkIdNum(String workidNum) {
        this.workIdNum = workidNum;
    }

}
