package Exception;

public class EOF extends Exception {
    public EOF(){
        System.out.println("End of file");
        System.exit(0);
    }
}
