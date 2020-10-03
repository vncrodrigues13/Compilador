package Exception;

import Scanner.ScannerCompilador;
import Buffer.Buffer;

public class FloatMalFormadoException extends Exception{
    public FloatMalFormadoException(){
        System.out.printf("Valor float mal formado na linha: %d e na coluna: %d\n",ScannerCompilador.getLinha(),Buffer.getColuna());
    }
    
}
