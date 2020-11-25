package Exception;

public class EOF extends Exception {
    public EOF(){
        System.out.println("Fim de arquivo");
        System.exit(0);
    }
}
