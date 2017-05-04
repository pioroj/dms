package pl.com.bottega.dms.infrastructure;


import java.io.*;
import java.util.Scanner;

public class CesarOutputStream extends OutputStream {

    public static void main(String[] args) throws IOException {
        String fileOut = "E:\\Projekty\\plikout.txt";
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter text:");
        String text = scanner.nextLine();

        OutputStream outputStream = new CesarOutputStream(new FileOutputStream(fileOut), 1);

        PrintWriter pw = new PrintWriter(outputStream);
        pw.println(text);
        pw.close();

        InputStream is = new CesarInputStream(new FileInputStream(fileOut), 1);
        Scanner scanner1 = new Scanner(is);
        String decipher = scanner1.nextLine();
        System.out.println(decipher);
    }

    private OutputStream outputStream;
    private int key;

    public CesarOutputStream(OutputStream outputStream, int key) {
        this.outputStream = outputStream;
        this.key = key;
    }

    @Override
    public void write(int b) throws IOException {
        //musi przyjac obiekt output stream zaszyfrowac bajt i wywolac metode uotput streama write z tym nowym
        b += key;
        b = b % 0xff;
        outputStream.write(b);
    }
}

class CesarInputStream extends InputStream {

    private InputStream inputStream;
    private int key;

    public CesarInputStream(InputStream inputStream, int key) {
            this.inputStream = inputStream;
            this.key = key;
        }

        @Override
        public int read() throws IOException {
            int b = inputStream.read();
            if (b == -1) {
                return -1;
            }
            b -= key;
            b = b % 0xff;
            return b;
    }
}
