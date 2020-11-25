package Simbolo;
import Token.Token;

public class Simbolo {
    private int tipo;
    private Token token;
    private int escopo;
    private Token valor;

    public Simbolo(Token token, int escopo, int tipo){
        this.token = token;
        this.escopo = escopo;
        this.tipo = tipo;
    }
    
    public Token getToken() {
        return this.token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public int getEscopo() {
        return this.escopo;
    }

    public void setEscopo(int escopo) {
        this.escopo = escopo;
    }
    
    public int getTipo(){
        return this.tipo;
    }

    public void setTipo(int tipo){
        this.tipo = tipo;
    }


    public Token getValor(){
        return this.valor;
    }

    public void setValor(Token valor){
        this.valor = valor;
        this.valor.setLexema(token.getLexema());
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Simbolo){
            Simbolo object = (Simbolo)o;
            return this.escopo == object.escopo && 
            this.token.equals(object.token);
        }
        return false;
    }
    




    


}
