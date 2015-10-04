package org.cysecurity.example.dodobank.controller;

import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryption
{
    private IvParameterSpec ivParams;
    private byte[] iv;
    public void setIvParams(Cipher cipher)
    {
        iv = new byte[]{70,105,120,101,100,73,86,78,111,116,83,101,99,117,114,101};
        ivParams = new IvParameterSpec(iv);
    }
    public String encrypt(String key,String plainText)
    {
        try
        {
            byte[] keyBytes = key.getBytes();

            SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            setIvParams(cipher);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams);
            byte[] ciphertext = cipher.doFinal(plainText.getBytes("UTF-8"));
            String bCipher=new String(Base64.encode(ciphertext,Base64.NO_WRAP));
            return bCipher;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(String key,String cipherText)
    {
        byte[] cipherBytes = Base64.decode(cipherText.getBytes(), Base64.NO_WRAP);
        try
        {
            byte[] keyBytes = key.getBytes();

            SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            setIvParams(cipher);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams);
            byte[] plaintext = cipher.doFinal(cipherBytes);
            Log.d("Decrypted",new String(plaintext , "UTF-8"));
            return  new String(plaintext , "UTF-8");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
