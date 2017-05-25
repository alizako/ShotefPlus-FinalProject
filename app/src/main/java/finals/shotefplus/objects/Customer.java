package finals.shotefplus.objects;


import java.util.List;
import android.content.res.Resources;
import finals.shotefplus.R;

/**
 * Created by Aliza on 14/01/2017.
 */
public class Customer {

    private static long idNumGlobal = 0;
    private String idNum;
    private String name;
    // private boolean isPriceOfferSent;
    private String adrs;
    private String phoneNum;
    private String email;
    private List<Work> workList;
    //private List<Receipt> receiptList;
    private String customerType; // 0-none 1- ESEK, 2- PRATI
    private String customerContactName;
    private String dateInsertion;


    static final String ESEK_TYPE ="עסק";
    static final String PRIVTE_TYPE ="פרטי";


    public Customer(String idNum, String name, String adrs,
                    String phoneNum, String email, List<Work> workList,
                    String customerType, String customerContactName) {
        this.idNum = idNum;
        this.name = name;
        this.adrs = adrs;
        this.phoneNum = phoneNum;
        this.email = email;
        this.workList = workList;
        this.customerType = customerType;
        this.customerContactName = customerContactName;
    }

    public Customer() {
        this.idNum = "";
        this.name = "";
        this.adrs = "";
        this.phoneNum = "";
        this.email = "";
        //this.workList = new List<Work>() ;
        this.customerType = "";
        this.customerContactName = "";
    }

    public String getIdNum() {
        return idNum;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*public boolean isPriceOfferSent() {
        return isPriceOfferSent;
    }

    public void setPriceOfferSent(boolean priceOfferSent) {
        isPriceOfferSent = priceOfferSent;
    }*/

    public String getAdrs() {
        return adrs;
    }

    public void setAdrs(String adrs) {
        this.adrs = adrs;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Work> getWorkList() {
        return workList;
    }

    public void setWorkList(List<Work> workList) {
        this.workList = workList;
    }

   /* public List<Receipt> getReceiptList() {
        return receiptList;
    }

    public void setReceiptList(List<Receipt> receiptList) {
        this.receiptList = receiptList;
    }*/

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getCustomerContactName() {
        return customerContactName;
    }

    public void setCustomerContactName(String customerContactName) {
        this.customerContactName = customerContactName;
    }

    /*public String getSummary() {
        return adrs + " | " +
                phoneNum + " | " +
                email;
    }*/

    public String getDateInsertion() {
        return dateInsertion;
    }

    public void setDateInsertion(String dateInsertion) {
        if (dateInsertion.contains("/"))
            this.dateInsertion = dateInsertion.substring(6, 10) + //year
                    dateInsertion.substring(3, 5) + // month
                    dateInsertion.substring(0, 2); //day
        else
            this.dateInsertion = dateInsertion;
    }

    public String dateInsertionToString() {
        return dateInsertion.substring(6, 8) + "/" + //day
                dateInsertion.substring(4, 6) + "/" + //month
                dateInsertion.substring(0, 4);//year
    }

    public String getCustomerTypeToString() {
        return customerType.equals("0")? "":
                customerType.equals("1")? ESEK_TYPE : PRIVTE_TYPE;

    }
}
