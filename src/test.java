
import java.io.FileNotFoundException;
import Scanner.ScannerCompilador;
import java.io.IOException;
import Parser.Parser;
import Exception.*;
public class Test {
    public static void main(String [] args) throws IOException, FloatMalFormadoException,ExclamacaoSemIgualException, EOF, CaractereInvalidoException, CharMalFormadoException,EOFemComentarioMultilinha,
            OpChareNaoChar {
            // String path = "C:\\Users\\mathe\\Documents\\Parser\\Compilador\\src\\file\\test.txt";
            String path = args[0];
            ler(path);
    }


    static void ler(String path) throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha, OpChareNaoChar {
        try{
            ScannerCompilador leitor = new ScannerCompilador(path);
            Parser pars = new Parser(leitor);
            pars.programa();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
    

}
