import Scanner.ScannerCompilador;

import java.io.FileNotFoundException;
import java.io.IOException;

import Exception.*;

public class main {
    public static void main(String[] args) throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException,
            EOF, CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha {
                String path = "C:\\Users\\User\\Documents\\Vinicius\\Projects\\Java\\Compilador\\src\\file\\file.txt";
        ler(args[0]);
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
