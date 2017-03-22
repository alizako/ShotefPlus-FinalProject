package finals.shotefplus.objects;

/**
 * Created by Aliza on 08/03/2017.
 */

public class UserProfile {
    private String businessName;
    private String tik;
    private String phone;
    private String email;
    private String password;

    public UserProfile(String businessName, String tik, String phone, String email) {
        this.businessName = businessName;
        this.tik = tik;
        this.phone = phone;
        this.email = email;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getTik() {
        return tik;
    }

    public void setTik(String tik) {
        this.tik = tik;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}


