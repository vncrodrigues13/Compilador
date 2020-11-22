package Exception;

public class EOFemComentarioMultilinha extends Exception{
    public EOFemComentarioMultilinha(){
        System.out.println("EOF em comentario multilinha");
        System.exit(0);
    }
    
}
