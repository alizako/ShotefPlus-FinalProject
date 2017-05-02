package finals.shotefplus.objects;

/**
 * Created by Aliza on 02/05/2017.
 */

public class EmailTemplate {
    private String subject, body;

    public EmailTemplate(){
        this.subject = "";
        this.body = "";
    }
    public EmailTemplate(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
