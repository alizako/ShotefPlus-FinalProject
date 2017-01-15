package finals.shotefplus.objects;

/**
 * Created by Aliza on 15/01/2017.
 */

public class Work {
    private static long idNumGlobal = 0;
    private long idNum = 0;

    public long getIdNum() {
        return idNum;
    }

    public Work() {
        idNumGlobal++;
        idNum=idNumGlobal;
    }
}
