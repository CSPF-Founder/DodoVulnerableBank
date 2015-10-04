package org.cysecurity.example.dodobank.model;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamToString
{
    public static String convert(InputStream is)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            BufferedReader reader= new BufferedReader(new InputStreamReader(is));
            String line;
            while((line=reader.readLine())!=null)
            {
                sb.append(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "Failed to Read Stream";
        }
        return sb.toString();
    }
}
