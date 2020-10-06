
import java.io.FileNotFoundException;
import Scanner.ScannerCompilador;
import java.io.IOException;
import Exception.*;
public class Test {
    public static void main(String [] args) throws IOException, FloatMalFormadoException,ExclamacaoSemIgualException, EOF, CaractereInvalidoException, CharMalFormadoException,EOFemComentarioMultilinha {
            String path = "C:\\Users\\User\\Documents\\Vinicius\\Projects\\Java\\Compilador\\src\\file\\file.txt";
            ler(path);
    }


    static void ler(String path) throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha {
        try{
            ScannerCompilador leitor = new ScannerCompilador(path);
            leitor.getNextToken();
            leitor.getNextToken();
            leitor.getNextToken();
            leitor.getNextToken();
            leitor.getNextToken();
            leitor.getNextToken();
            leitor.getNextToken();
            leitor.getNextToken();
            leitor.getNextToken();
            leitor.getNextToken();
            leitor.getNextToken();
            leitor.getNextToken();
            leitor.getNextToken();
            leitor.getNextToken();
            
            
        
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

}
