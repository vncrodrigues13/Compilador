package Scanner;

import java.io.*;
import Exception.*;
import Token.Token;
public class Test {
    public static void main(String [] args) throws IOException, inteiroInvalido, exceptionFloatMalFormado, IdentificadorMalFormado,
            exclamacaoNaoSeguidaDeIgual {
        method();
        // lexemaTest();
    }


    static void method() throws IOException, inteiroInvalido, exceptionFloatMalFormado, IdentificadorMalFormado,
            exclamacaoNaoSeguidaDeIgual {
        try{
            Scanner leitor = new Scanner(".\\src\\arquivo\\file.txt");
            leitor.lerCodigo();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    static void lexemaTest() throws IdentificadorMalFormado{
        Token token = new Token("vinicius");
        System.out.println(token.getLexema());
        System.out.println(token.getValor());
    }
}
