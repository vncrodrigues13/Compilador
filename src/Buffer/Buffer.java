package Buffer;

import Token.Token;
import java.lang.StringBuilder;

import Exception.CaractereInvalidoException;
import Exception.CharMalFormadoException;
import Exception.FloatMalFormadoException;
import Exception.ExclamacaoSemIgualException;
import Exception.InteiroMalFormadoException;

public class Buffer {

    private String linha;
    private static int coluna;
    private StringBuilder strBuilder;
    private char[] linhaParaArray;

    public Buffer() {
        coluna = 0;
    }

    public Token scan(String line)
            throws InteiroMalFormadoException, FloatMalFormadoException, ExclamacaoSemIgualException,
            CaractereInvalidoException, CharMalFormadoException {
        linhaParaArray = line.toCharArray();
        int colunaTemporaria;
        strBuilder = new StringBuilder();
        if (this.linha != null && this.linha.equals(line)) {
            // se a linha que for passada ja estiver em analise, continue.
            colunaTemporaria = this.coluna;
            //caso a linha ja tenha sido lida, preciso checar se ela ja acabou de ser lida
            if (coluna - 1 >= line.length()) {
                // caso a linha ja tenha sido acabada;
                return null;
            }
        } else {
            // caso essa linha nunca tenha sido lida
            colunaTemporaria = 0;
            this.coluna = 0;
            this.linha = line;
        }

        for (int x = colunaTemporaria; x < linhaParaArray.length; x++, coluna++) {
            // o valor da colunaTemporaria, se a linha ja estiver sendo lida, vai ser a
            // continuacao
            // caso n, vai ser igual a 0
            if (Character.isSpace(linhaParaArray[x])) {
                // se o buffer pegar um espaço em branco ele lê a prox posição
                continue;

            } else if (linhaParaArray[x] == '!' || linhaParaArray[x] == '=' || linhaParaArray[x] == '>'
                    || linhaParaArray[x] == '<') {

                // precisa fazer o LOOKAHEAD
                strBuilder.append(linhaParaArray[x]); // adiciona o valor lido
                coluna++; // vai para proxima coluna
                return lookAhead(); // vai adicionar o prox simbolo e retornar o token formado

            } else if (Character.isDigit(linhaParaArray[x])) {

                // apenas um valor float ou inteiro comeca com numero
                return buildInteiro();

            } else if (linhaParaArray[x] == '.') {
                // se houver ponto forma float
                strBuilder.append('.');
                return buildFloat();

            } else if (Character.isLetter(linhaParaArray[x])) {
                // caso leia uma letra, so pode ser identificador ou palavra restrita
                return continuarScan();

            } else if (linhaParaArray[x] == '\'') {
                // caso ele leia uma '
                strBuilder.append(linhaParaArray[x]);
                coluna++; // ler o proximo elemento
                return readChar();

            } else if (linhaParaArray[x] == ';') {
                coluna++;
                return new Token(";");
            } else if (linhaParaArray[x] == '(') {
                coluna++;
                return new Token("(");
            } else if (linhaParaArray[x] == ')') {
                coluna++;
                return new Token(")");
            } else if (linhaParaArray[x] == '{') {
                coluna++;
                return new Token("{");
            } else if (linhaParaArray[x] == '}') {
                coluna++;
                return new Token("}");
            } else if (linhaParaArray[x] == '+') {
                coluna++;
                return new Token("+");
            } else if (linhaParaArray[x] == '-') {
                coluna++;
                return new Token("-");
            } else if (linhaParaArray[x] == '*') {
                coluna++;
                return new Token("*");
            } else if (linhaParaArray[x] == '/') {
                coluna++;
                return new Token("/");
            } else if (linhaParaArray[x] == '\t') {
                coluna += 4;
                return new Token("\t");
            }else{
                throw new CaractereInvalidoException();
            }
        }
        return null;
    }

    private Token continuarScan() throws CharMalFormadoException, FloatMalFormadoException {
        // pode ser um identificador ou um uma palavra reservada
        for (int x = coluna; x < linhaParaArray.length; x++, coluna++) {
            if (linhaParaArray[x] == '_' || Character.isDigit(linhaParaArray[x])) {
                // se houver um caractere '_' ou um numero, significa que eh um identificador
                return buildID();
            } else if (Character.isLetter(linhaParaArray[x])) {
                strBuilder.append(linhaParaArray[x]);
            } else {
                // caso apareca um caractere diferente de letra, numero ou _; ele retorna
                return new Token(strBuilder.toString());
            }
        }
        coluna++;
        return new Token(strBuilder.toString());
    }

    private Token lookAhead() throws ExclamacaoSemIgualException, CharMalFormadoException, FloatMalFormadoException {
        if ((strBuilder.toString().equals(">") || strBuilder.toString().equals("<")
                || strBuilder.toString().equals("=")) && linhaParaArray[coluna] != '=') {
            // caso o '>','<','=' não tenha o '=' como prox caractere
            return new Token(strBuilder.toString());
        }
        if (strBuilder.toString().equals("!") && linhaParaArray[coluna] != '=') {
            throw new ExclamacaoSemIgualException();
        }
        strBuilder.append(linhaParaArray[coluna]);
        coluna++;
        return new Token(strBuilder.toString());
    }

    private Token readChar() throws CharMalFormadoException, FloatMalFormadoException {
        // apos ele ler um ', ele le os proximos dois caracteres

        if (coluna + 2 >= linhaParaArray.length){
            throw new CharMalFormadoException();
        }
        if (Character.isLetterOrDigit(linhaParaArray[coluna])){
            // le a letra ou digito e vai para a proxima posicao
            strBuilder.append(linhaParaArray[coluna]);
            coluna++;
            if (linhaParaArray[coluna] == '\''){
                // tem que fechar o char, caso nao feche, ele retorna erro
                strBuilder.append('\'');
            }else{
                throw new CharMalFormadoException();
            }
        }else{
            throw new CharMalFormadoException();
        }

        // for (int x = coluna; x < linhaParaArray.length; x++, coluna++) {
        //     if (Character.isLetterOrDigit(linhaParaArray[x])) {
        //         strBuilder.append(linhaParaArray[x]);
        //     }
        //     if (linhaParaArray[x] == '\'') {
        //         strBuilder.append(linhaParaArray[x]);
        //         coluna++;
        //         return new Token(strBuilder.toString());
        //     }
        // }
        coluna++;
        return new Token(strBuilder.toString());
    }

    private Token buildID() throws CharMalFormadoException, FloatMalFormadoException {
        // ele ja leu um _ no comeco
        for (int x = coluna; x < linhaParaArray.length; x++, coluna++) {
            if (Character.isLetterOrDigit(linhaParaArray[x]) || linhaParaArray[x] == '_') {
                // se o caractere for um digito, caractere ou _ ele continua adicionando
                strBuilder.append(linhaParaArray[x]);
            } else {
                return new Token(strBuilder.toString());
            }
        }
        return new Token(strBuilder.toString());
    }

    private Token buildInteiro() throws InteiroMalFormadoException, FloatMalFormadoException, CharMalFormadoException {
        for (int x = coluna; x < this.linhaParaArray.length; x++, coluna++) {
            if (Character.isDigit(linha.charAt(x))) {
                strBuilder.append(linha.charAt(x));
            } else if (linhaParaArray[x] == '.') {
                coluna++;
                strBuilder.append('.');
                return buildFloat();
            } else if (Character.isLetter(linha.charAt(x))) {
                // throw erro, contem caractere no meio de um inteiro
                coluna++;
                throw new InteiroMalFormadoException();
            } else {
                coluna++;
                return new Token(strBuilder.toString());
            }
        }
        coluna++;
        return new Token(strBuilder.toString());
    }

    private Token buildFloat() throws FloatMalFormadoException, CharMalFormadoException {
        int cont = 0;
        strBuilder.append(".");
        for (int x = coluna; x < this.linhaParaArray.length; x++, coluna++) {
            if (Character.isDigit(linhaParaArray[x])) {
                cont++;
                strBuilder.append(linhaParaArray[x]);
            } else if (Character.isLetter(this.linhaParaArray[x])) {
                // se houver letra no meio do valor float, retorna float mal formado
                coluna++;
                throw new FloatMalFormadoException();
            } else if (!Character.isDigit(linhaParaArray[x]) && cont == 0) {
                // não sem nenhum número após o '.'
                coluna++;
                throw new FloatMalFormadoException();
            }
        }
        
        return new Token(strBuilder.toString());
    }


    public static int getColuna(){
        return coluna;
    }
    

}
