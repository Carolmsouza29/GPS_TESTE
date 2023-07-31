package com.example.gps;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.Cipher;

public class Criptografia {

    private static final String ALGORITHM = "RSA";

    /**
     * Local da chave privada no sistema de arquivos.
     */
    @SuppressLint("SdCardPath")
    private static final String PATH_CHAVE_PRIVADA ="/data/data/com.example.gps/files/private.key";

    /**
     * Local da chave pública no sistema de arquivos.
     */
    @SuppressLint("SdCardPath")
    private static final String PATH_CHAVE_PUBLICA = "/data/data/com.example.gps/files/public.key";

    private ObjectInputStream inputStream = null;


    /**
     * Gera a chave que contém um par de chave Privada e Pública usando 1025 bytes.
     * Armazena o conjunto de chaves nos arquivos private.key e public.key
     */

    public Criptografia() {
        //Verifica se existe os arquivos das chaves publica e privada
        if (!verificaSeExisteChavesNoSO()) {
            geraChave();
        }
    }

    public static void geraChave() {
        //se já existirem os arquivos, salvam as chaves se não os arquivos são criados
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyGen.initialize(1024);
            final KeyPair key = keyGen.generateKeyPair();

            File chavePrivadaFile = new File(PATH_CHAVE_PRIVADA);
            File chavePublicaFile = new File(PATH_CHAVE_PUBLICA);

            // Cria os arquivos para armazenar a chave Privada e a chave Publica
            if (chavePrivadaFile.getParentFile() != null) {
                chavePrivadaFile.getParentFile().mkdirs();
            }

            chavePrivadaFile.createNewFile();

            if (chavePublicaFile.getParentFile() != null) {
                chavePublicaFile.getParentFile().mkdirs();
            }

            chavePublicaFile.createNewFile();

            // Salva a Chave Pública no arquivo
            ObjectOutputStream chavePublicaOS = new ObjectOutputStream(
                    new FileOutputStream(chavePublicaFile));
            chavePublicaOS.writeObject(key.getPublic());
            chavePublicaOS.close();

            // Salva a Chave Privada no arquivo
            ObjectOutputStream chavePrivadaOS = new ObjectOutputStream(
                    new FileOutputStream(chavePrivadaFile));
            chavePrivadaOS.writeObject(key.getPrivate());
            chavePrivadaOS.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static boolean verificaSeExisteChavesNoSO() {

        File chavePrivada = new File(PATH_CHAVE_PRIVADA);
        File chavePublica = new File(PATH_CHAVE_PUBLICA);

        if (chavePrivada.exists() && chavePublica.exists()) {
            return true;
        }

        return false;
    }


    private static byte[] criptografa(String texto, PublicKey chave) {
        byte[] cipherText = null;

        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            // Criptografa o texto puro usando a chave Púlica
            cipher.init(Cipher.ENCRYPT_MODE, chave);
            cipherText = cipher.doFinal(texto.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cipherText;
    }


    private static String decriptografa(byte[] texto, PrivateKey chave) {
        byte[] dectyptedText = null;

        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            // Decriptografa o texto puro usando a chave Privada
            cipher.init(Cipher.DECRYPT_MODE, chave);
            dectyptedText = cipher.doFinal(texto);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new String(dectyptedText);
    }

    public String criptografarTexto(String text,PublicKey chavePublica){
        try {

            final byte[] textoCriptografado = criptografa(text, chavePublica);
            return convertByteArrayToString(textoCriptografado);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String descriptografarTexto(String textoCriptografado){
        try {
            Log.d("Entrei Na descriptografia", textoCriptografado);
            byte[] textoCriptografadoBytes = convertStringToByteArray(textoCriptografado);
            // Decriptografa a Mensagem usando a Chave Privada
            inputStream = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PRIVADA));
            final PrivateKey chavePrivada = (PrivateKey) inputStream.readObject();
            final String textoPuro = decriptografa(textoCriptografadoBytes, chavePrivada);
            return textoPuro;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String convertByteArrayToString(byte[] bytes){
        String s = "";
        for (byte b : bytes) {
            s += Byte.toString(b);
            s+= ";";
        }
        return s;
    }

    private byte[] convertStringToByteArray(String s){
        String[] array = s.split(";");
        byte[] bytes = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            bytes[i] = Byte.parseByte(array[i]);
        }
        return bytes;
    }

    public String getPublicKeyString() {
        try {
            // Read the Public Key from the file
            inputStream = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PUBLICA));
            PublicKey publicKey = (PublicKey) inputStream.readObject();
            String publicKeyString = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            }

            return publicKeyString;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
