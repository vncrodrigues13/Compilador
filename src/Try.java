import Scanner.ScannerCompilador;

import java.io.FileNotFoundException;
import java.io.IOException;

import Exception.*;

public class Try {
    public static void main(String[] args) throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException,
            EOF, CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha {
                String path = "C:\\Users\\User\\Documents\\Vinicius\\Projects\\Java\\Compilador\\src\\file\\file.txt";
        ler(args[0]);
        ler(path);
    }


    static void ler(String path) throws IOException, FloatMalFormadoException, ExclamacaoSemIgualException, EOF,
            CaractereInvalidoException, CharMalFormadoException, EOFemComentarioMultilinha {
        try{
            ScannerCompilador leitor = new ScannerCompilador(path);
            System.out.println(leitor.getNextToken().getValor());
            System.out.println(leitor.getNextToken().getValor());
            System.out.println(leitor.getNextToken().getValor());
            System.out.println(leitor.getNextToken().getValor());
            System.out.println(leitor.getNextToken().getValor());
            System.out.println(leitor.getNextToken().getValor());
            System.out.println(leitor.getNextToken().getValor());
            System.out.println(leitor.getNextToken().getValor());
            System.out.println(leitor.getNextToken().getValor());
            System.out.println(leitor.getNextToken().getValor());
            System.out.println(leitor.getNextToken().getValor());
            
            
        
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
    
}
