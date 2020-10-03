package Exception;

import Scanner.ScannerCompilador;
import Buffer.Buffer;
public class InteiroMalFormadoException extends Exception{
    public InteiroMalFormadoException(){
        System.out.printf("Inteiro mal formado na linha: %d e na coluna %d \n",ScannerCompilador.getLinha(),Buffer.getColuna());
    }
}
