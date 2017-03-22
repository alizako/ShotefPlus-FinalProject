package finals.shotefplus.objects;


import java.util.List;

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
    private String customerType; // ESEK, PRATI
    private String customerContactName;


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

    public String getSummary() {
        return adrs + " | " +
                phoneNum + " | " +
                email;
    }
}
