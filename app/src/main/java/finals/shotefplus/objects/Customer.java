package finals.shotefplus.objects;


import java.util.List;

/**
 * Created by Aliza on 14/01/2017.
 */
public class Customer {

    private static long idNumGlobal = 0;
    private long idNum = 0;

    private String name;
   // private boolean isPriceOfferSent;
    private String adrs;
    private String phoneNum;
    private String email;
    private List<Work> workList;
    private List<Receipt> receiptList;

    public Customer(String name, boolean isPriceOfferSent, String adrs, String phoneNum, String email) {
        idNumGlobal++;
        idNum=idNumGlobal;
        this.name = name;
    //    this.isPriceOfferSent = isPriceOfferSent;
        this.adrs = adrs;
        this.phoneNum = phoneNum;
        this.email = email;
    }

    public long getIdNum() {
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

    public List<Receipt> getReceiptList() {
        return receiptList;
    }

    public void setReceiptList(List<Receipt> receiptList) {
        this.receiptList = receiptList;
    }

    public String getSummary() {
        return adrs + " | " +
                phoneNum + " | " +
                email;
    }
}
