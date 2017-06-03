package finals.shotefplus.objects;

/**
 * Created by Aliza on 03/06/2017.
 */

public enum EnumPaymentType {
        CASH(1),
        CREDIT(2),
        CHEQUE(3),
        TRANS(4);

    private final int type;
    EnumPaymentType(int type) { this.type = type; }
    public int getValue() { return type; }
}
