package Exception;

import Scanner.ScannerCompilador;
import Buffer.Buffer;
public class CaractereInvalidoException extends Exception{
    public CaractereInvalidoException(){
        System.out.printf("Caractere Invalido na linha: %d e na coluna: %d\n",ScannerCompilador.getLinha()+1,Buffer.getColuna()+1);
    }
}
