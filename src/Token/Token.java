package Token;

import Exception.CharMalFormadoException;
import Exception.FloatMalFormadoException;
import Buffer.Buffer;

public class Token {
    private String lexema;
    private int tipo;

    public Token(String tokenLido) throws CharMalFormadoException, FloatMalFormadoException {
        this.tipo = classificarToken(tokenLido);
    }

    public Token(int tipo) {
        this.tipo = tipo;
    }

    public String getLexema() {
        return this.lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public int getTipo() {
        return this.tipo;
    }

    public void setTipo(int classificacao) {
        this.tipo = classificacao;
    }

    public int classificarToken(String tokenLido) throws CharMalFormadoException, FloatMalFormadoException {
        if (tokenLido.equals("main")) {
            return 0;
        } else if (tokenLido.equals("if")) {
            this.lexema = "if";
            return 1;
        } else if (tokenLido.equals("else")) {
            this.lexema = "else";
            return 2;
        } else if (tokenLido.equals("while")) {
            this.lexema = "while";
            return 3;
        } else if (tokenLido.equals("do")) {
            this.lexema = "do";
            return 4;
        } else if (tokenLido.equals("for")) {
            this.lexema = "for";
            return 5;
        } else if (tokenLido.equals("int")) {
            this.lexema = "int";
            return 6;
        } else if (tokenLido.equals("float")) {
            this.lexema = "float";
            return 7;
        } else if (tokenLido.equals("char")) {
            this.lexema = "char";
            return 8;
        } else if (tokenLido.equals("==")) {
            this.lexema = "==";
            return 10;
        } else if (tokenLido.equals("!=")) {
            this.lexema = "!=";
            return 11;
        } else if (tokenLido.equals(">")) {
            this.lexema = ">";
            return 12;
        } else if (tokenLido.equals(">=")) {
            this.lexema = ">=";
            return 13;
        } else if (tokenLido.equals("<")) {
            this.lexema = "<";
            return 14;
        } else if (tokenLido.equals("<=")) {
            this.lexema = "<=";
            return 15;
        } else if (tokenLido.equals("=")) {
            this.lexema = "=";
            return 16;
        } else if (tokenLido.equals("(")) {
            this.lexema = "(";
            return 20;
        } else if (tokenLido.equals(")")) {
            this.lexema = ")";
            return 21;
        } else if (tokenLido.equals("{")) {
            this.lexema = "{";
            return 22;
        } else if (tokenLido.equals("}")) {
            this.lexema = "}";
            return 23;
        } else if (tokenLido.equals(";")) {
            this.lexema = ";";
            return 24;
        } else if (tokenLido.equals(",")) {
            this.lexema = ",";
            return 25;
        } else if (tokenLido.equals("//")) {
            this.lexema = "//";
            return 30;
        } else if (tokenLido.equals("/*")) {
            this.lexema = "/*";
            return 31;
        } else if (tokenLido.equals("*/")) {
            this.lexema = "*/";
            return 32;
        } else if (tokenLido.equals("+")) {
            this.lexema = "+";
            return 40;
        } else if (tokenLido.equals("-")) {
            this.lexema = "-";
            return 41;
        } else if (tokenLido.equals("*")) {
            this.lexema = "*";
            return 42;
        } else if (tokenLido.equals("/")) {
            this.lexema = "/";
            return 43;
        } else if (tokenLido.equals("\t")) {
            return 150;
        } else {
            // preciso identificar se eh um caractere, um inteiro, um float ou um
            // identificador
            if (validarInt(tokenLido)) {
                this.lexema = tokenLido;
                return 90;
            } else if (validarFloat(tokenLido)) {
                this.lexema = tokenLido;
                return 91;
            } else if (validarChar(tokenLido)) {
                this.lexema = tokenLido;
                return 92;
            } else if (validarIdentificador(tokenLido)) {
                this.lexema = tokenLido;
                return 99;
            } else {
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

    private boolean validarChar(String element) throws CharMalFormadoException {
        // ele verifica a construcao do char se eh um: ' -> element -> '
        if (element.charAt(0) == '\'' && Character.isLetterOrDigit(element.charAt(1)) && element.charAt(2) == '\'') {
            return true;
        } else {
            return false;
        }
    }

    private boolean validarInt(String element) {
        int contador = 0;
        for (int x = 0; x < element.length(); x++) {
            if (!Character.isDigit(element.charAt(x))) {
                return false;
            }
            if (Character.isDigit(element.charAt(x)))
                contador++;
        }
        return contador > 0;
    }

    private boolean validarFloat(String element) throws FloatMalFormadoException {
        boolean existePonto = false;
        boolean valido = false;
        int posicaodoPonto = 0;
        int x;
        for (x = 0; x < element.length(); x++) {
            if (!Character.isDigit(element.charAt(x)) && element.charAt(x) != '.') {
                // se conter um elemento que nao seja um numero ou ponto, retorna falso
                return false;
            }
            if (element.charAt(x) == '.') {
                posicaodoPonto = x;
                existePonto = true;
                break;
            }

        }
        if (existePonto && posicaodoPonto < element.length()) {
            for (; x < element.length(); x++) {
                if (!Character.isDigit(element.charAt(x)) && element.charAt(x) != '.') {
                    // se conter um elemento que nao seja um numero, ou um ponto, retorna falso
                    return false;
                }
                if (Character.isDigit(element.charAt(x))) {
                    valido = true;
                }
            }
        } else {
            // caso nao exista ponto ou dps do ponto nao exista nenhum elemento
            throw new FloatMalFormadoException();
        }
        return valido;
    }

    @Override
    public String toString() {
        return "{" + " lexema='" + lexema + "'" + ", classificacao='" + tipo + "'" + ",coluna=" + Buffer.getColuna()
                + " }";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Token) {
            Token object = (Token) o;
            return this.lexema.equals(object.getLexema()) && this.getTipo() == object.getTipo();
        }
        return false;
    }

}
