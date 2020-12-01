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
        if (this.valor != null){
            return this.valor;
        }else{
            Token tk;
            switch(tipo){
                
                case 6:
                // int
                    tk = new Token(90);
                    break;
                case 7:
                // float
                    tk = new Token(91);
                    break;
                default:
                // char 
                    tk = new Token(92);
                    break;
            }
            tk.setLexema(token.getLexema());
            return tk;
        }
        
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


    @Override
    public String toString() {
        return "{" +
            " tipo='" + tipo + "'" +
            ", token='" + token + "'" +
            ", escopo='" + escopo + "'" +
            ", valor='" + getValor() + "'" +
            "}";
    }





    


}
