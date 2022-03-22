public class UtilsTest {
    public static void main(String[] args) {
        System.out.println(Utils.getNumDigits(1000) == 4);
        System.out.println(Utils.getNumDigits(999) == 3);
        System.out.println(Utils.getNumDigits(2342342) == 7);
        System.out.println(Utils.getNumDigits(1) == 1);
        System.out.println(Utils.getNumDigits(-2) == 1);
        System.out.println(Utils.getNumDigits(12) == 2);
        System.out.println(Utils.getNumDigits(0) == 1);
    }
}
