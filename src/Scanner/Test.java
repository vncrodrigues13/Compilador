package Scanner;

import java.io.*;
import Exception.*;
import Token.Token;
public class Test {
    public static void main(String [] args) throws IOException, InteiroMalFormadoException, FloatMalFormadoException,
            ExclamacaoSemIgualException, EOF, CaractereInvalidoException, CharMalFormadoException {
        method();
        // lexemaTest();
    }


    static void method() throws IOException, InteiroMalFormadoException, FloatMalFormadoException,
            ExclamacaoSemIgualException, EOF, CaractereInvalidoException, CharMalFormadoException {
        try{
            ScannerCompilador leitor = new ScannerCompilador(".\\src\\arquivo\\file.txt");
            System.out.println(leitor.getNextToken().getValor());
            System.out.println(leitor.getNextToken().getValor());
            
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    static void lexemaTest() throws CharMalFormadoException, FloatMalFormadoException {
        Token token = new Token("vinicius");
        System.out.println(token.getLexema());
        System.out.println(token.getValor());
    }
}
