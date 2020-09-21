package Token;

import Exception.IdentificadorMalFormado;

public class Token {
    private String lexema;
    private int classificacao;

    public Token(String tokenLido) throws IdentificadorMalFormado {
        this.classificacao = classificarToken(tokenLido);
    }

    public String getLexema() {
        return this.lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public int getValor() {
        return this.classificacao;
    }

    public void setValor(int valor) {
        this.classificacao = valor;
    }

    public int classificarToken(String tokenLido) throws IdentificadorMalFormado {
        if (tokenLido.equals("main")) {
            return 0;
        } else if (tokenLido.equals("if")) {
            return 1;
        } else if (tokenLido.equals("else")) {
            return 2;
        } else if (tokenLido.equals("while")) {
            return 3;
        } else if (tokenLido.equals("do")) {
            return 4;
        } else if (tokenLido.equals("for")) {
            return 5;
        } else if (tokenLido.equals("int")) {
            return 6;
        } else if (tokenLido.equals("float")) {
            return 7;
        } else if (tokenLido.equals("char")) {
            return 8;
        } else if (tokenLido.equals("==")) {
            return 10;
        } else if (tokenLido.equals("!=")) {
            return 11;
        } else if (tokenLido.equals(">")) {
            return 12;
        } else if (tokenLido.equals(">=")) {
            return 13;
        } else if (tokenLido.equals("<")) {
            return 14;
        } else if (tokenLido.equals("<=")) {
            return 15;
        }else if (tokenLido.equals("=")){
            return 16;
        } else if (tokenLido.equals("(")) {
             return 20;
        } else if (tokenLido.equals(")")) {
            return 21;
        } else if (tokenLido.equals("{")) {
            return 22;
        } else if (tokenLido.equals("}")) {
            return 23;
        } else if (tokenLido.equals(";")) {
            return 24;
        } else if (tokenLido.equals(",")) {
            return 25;
        }else if (tokenLido.equals("//")) {
            return 30;
        } else if (tokenLido.equals("/*")) {
            return 31;
        } else if (tokenLido.equals("*/")) {
            return 32;
        }else if (tokenLido.equals("+")){
            return 40;
        }else if (tokenLido.equals("-")){
            return 41;
        }else if (tokenLido.equals("*")){
            return 42;
        }else if (tokenLido.equals("/")){
            return 43;
        }else {
            // preciso identificar se eh um caractere, um inteiro, um float ou um identificador
            if (validarInt(tokenLido)){
                this.lexema = tokenLido;
                return 90;
            }else if (validarFloat(tokenLido)){
                this.lexema = tokenLido;
                return 91;
            }else if (validarChar(tokenLido)){
                this.lexema = tokenLido;
                return 92;
            }else if (validarIdentificador(tokenLido)){
                this.lexema = tokenLido;
                return 99;
            }else{
                return 101;
            }
        }
    }
    private boolean validarIdentificador(String element) {
        char[] toCharArrayElement = element.toCharArray();

        for (int x = 0; x < toCharArrayElement.length; x++) {
            if (x == 0 && Character.isDigit(toCharArrayElement[x])) {
                // se o primeiro elemento for um numero, nao eh um identificador
                return false;
            }
            if (!Character.isLetterOrDigit(toCharArrayElement[x]) && toCharArrayElement[x] != '_') {
                // se houver algum caracter que não seja letra,digito ou _, ele recusa
                return false;
            }
        }
        return true;
    }

    private boolean validarChar(String element) {
        // ele verifica a construcao do char se eh um: ' -> element -> '
        return element.charAt(0) == '\'' && Character.isLetterOrDigit(element.charAt(1)) && element.charAt(2) == '\'';
    }

    private boolean validarInt(String element){
        int contador = 0;
        for (int x = 0; x < element.length(); x++){
            if (!Character.isDigit(element.charAt(x))){
                return false;
            }
            if (Character.isDigit(element.charAt(x)))
                contador++;
        }
        return contador > 0;
    }

    private boolean validarFloat(String element){
        boolean existePonto = false;
        boolean valido = false;
        for (int x = 0; x < element.length(); x++){
            if (!Character.isDigit(element.charAt(x)) && element.charAt(x) != '.'){
                // se conter um elemento que nao seja um numero, ou um ponto, retorna falso
                return false;
            }
            if (element.charAt(x) == '.'){
                existePonto = true;
            }

            if (Character.isDigit(element.charAt(x)) && existePonto){
                // se houver algum numero apos o ponto, ele bool valido = true;
                valido = true;
            }
        }
        return valido;
    }

}
