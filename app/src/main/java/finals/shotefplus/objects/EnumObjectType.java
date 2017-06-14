package finals.shotefplus.objects;

/**
 * Created by Aliza on 11/06/2017.
 */

public enum EnumObjectType {
    CUSTOMER(1),
    PRICE_OFFER(2),
    WORK(3),
    RECEIPT(4),
    EXPENSE(5);

    private final int type;
    EnumObjectType(int type) { this.type = type; }
    public final int getValue() { return type; }
}
