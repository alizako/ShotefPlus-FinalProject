package finals.shotefplus;


/**
 * Created by Aliza on 14/01/2017.
 */
public class Customer {

    private long idNum;
    private String name;
    private boolean isPriceOfferSent;
    private String adrs;
    private String phoneNum;
    private String email;
   // private String List<Work>;
    //private String List<Receipt>;

    public Customer(String name, boolean isPriceOfferSent, String adrs, String phoneNum, String email) {
        this.name = name;
        this.isPriceOfferSent = isPriceOfferSent;
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

    public boolean getIsPriceOfferSent() {
        return isPriceOfferSent;
    }

    public String getSummary() {
        return adrs + " | " +
                phoneNum + " | " +
                email;
    }
}
