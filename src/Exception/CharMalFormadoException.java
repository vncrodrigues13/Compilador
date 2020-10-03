package Exception;

import Scanner.ScannerCompilador;
import Buffer.Buffer;
public class CharMalFormadoException extends Exception{
    public CharMalFormadoException(){
        System.out.printf("Caractere invalido na linha: %d e na coluna %d\n",ScannerCompilador.getLinha(),Buffer.getColuna());
    }
}
