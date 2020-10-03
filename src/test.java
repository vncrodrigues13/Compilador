
public class test {
    public static void main(String[] args) {
        int contador, linha;

        contador = linha = 0;

        while (contador != 10) {
            if (contador % 2 == 0) {
                contador++;
                continue;
            }
            linha++;
        }
        System.out.println("Contador => " + contador);
        System.out.println("Linha => " + linha);

    }
}