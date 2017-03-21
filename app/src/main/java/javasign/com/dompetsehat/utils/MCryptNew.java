package javasign.com.dompetsehat.utils;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import javasign.com.dompetsehat.VeryFund.GeneralHelper;

public class MCryptNew {

    GeneralHelper helper = GeneralHelper.getInstance();
    private String SecretKey1 = "kDCkhnp5ihz3GqzSm21Dn4sX11X111VN";
    private String SecretKey2 = "aMMervp3ovy4GqbCg65Nc8xK17X122VN";
    private String SecretKey3 = "aJMpcbp6lcy6GqzEh52Hg7cQ56X217VN";
    private String SecretKey4 = "zCJlcgp6mdy8GqnFr72Vr5eW24X467VN";
    private String SecretKey5 = "cDFncyp9jey8GqhDt64Xt1fB34X328VN";
    private String SecretKey6 = "cZCjchp6iryfwefdn32Dy1gX14X338VN";
    private String SecretKey7 = "vDTjcip2iyy9123bYc72Du4tZ72X388VN";
    private String SecretKey8 = "kVBkcfp3ivy123GqzSn62Dn2sW27X408VN";
    private String SecretKey9 = "bADvfev1mkg3Cqwen12Dn2sZ88X478VN";
    private String SecretKey10 = "kVBkcfp3ivyadsdqwSn62Dn2sW27X408VN";
    private String SecretKey11 = "kVBkvdssdc3GyjSn56Dn2sD45W456VN";
    private String SecretKey12 = "kBCkjju4poq123dSn76Dn2xG66X845VN";
    private String SecretKey13 = "kXCkzzz1ogfe2zZn25Dn2wH21C225VN";
    private String SecretKey14 = "kUUkbfe6mvb3UizDn22Dn2rJ56V184VN";
    private String SecretKey15 = "kOGkter2ozx3IqwCn81Dn2yT33Z589VN";

    public MCryptNew() {

    }

    public String encrypt(String message) {

        try {
            byte[] iv = generateIv();

            SecretKeySpec skeySpec = new SecretKeySpec(helper.skeySpec.getBytes(), helper.AES);
            Cipher cipher = Cipher.getInstance(helper.cipher);
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivspec);
            byte[] encryptedAES = cipher.doFinal(message.getBytes(helper.UTF8));

            String iv64 = new String(Base64.encode(iv, Base64.DEFAULT), helper.UTF8);
            String encryptedString = new String(Base64.encode(encryptedAES, Base64.DEFAULT), helper.UTF8);
            String mac = hmacDigest(encryptedString, iv64, "HmacMD5");

            JSONObject json = new JSONObject();
            json.put("iv", iv64);
            json.put("value", encryptedString);
            json.put("mac", mac);

            String encrypt64 = new String(Base64.encode(json.toString().getBytes(), Base64.DEFAULT), helper.UTF8);
            return encrypt64;
        }catch (Exception e) {
            return null;
        }

    }

    public String decrypt(String message){
        JSONObject encryptedjson = getJSONPayload(message);

        byte[] iv64 = null,
                value64 = null,
                mac64 = null,
                iv = null,
                value = null;

        try {
            iv64 = encryptedjson.getString("iv").getBytes();
            value64 = encryptedjson.getString("value").getBytes();
            mac64 = encryptedjson.getString("mac").getBytes();
            iv = Base64.decode(iv64, Base64.DEFAULT);
            value = Base64.decode(value64, Base64.DEFAULT);
            if (macEquals(iv64, value64, mac64)) {
                SecretKeySpec skeySpec = new SecretKeySpec(helper.skeySpec.getBytes(), helper.AES);

                Cipher cipher = Cipher.getInstance(helper.cipher);

                IvParameterSpec ivspec = new IvParameterSpec(iv);
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivspec);

                byte[] decryptedByte = cipher.doFinal(value);
                String decrypted = new String(decryptedByte, helper.UTF8);
                return decrypted;
            }
        }
        catch (Exception e){
            return message;
        }
        catch (Error e){
            return message;
        }
        return message;
    }


    JSONObject getJSONPayload(String encrypted){
        String base64String = null;
        JSONObject obj = null;
        try {
            base64String = new String(Base64.decode(encrypted, Base64.DEFAULT), helper.UTF8);
            obj = new JSONObject(base64String);

        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        } catch (JSONException e) {
            //e.printStackTrace();
        }catch (NullPointerException e){
            //e.printStackTrace();
        }catch (Exception e){
            //e.printStackTrace();
        }

        return obj;
    }

    boolean macEquals(byte[] iv,byte [] value,byte[] mac){
        String ivS = null;
        String valueS = null;
        String macS = null;
        try {
            ivS = new String(iv, helper.UTF8);
            valueS = new String(value, helper.UTF8);
            macS = new String(mac, helper.UTF8);
        }catch(Exception e){
            return false;
        }

        boolean equal = hmacDigest(valueS,ivS,"HmacMD5").equals(macS);
        return equal;
    }

    public static String hmacDigest(String msg, String keyString, String algo) {
        String digest = null;
        try {
            SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), algo);
            Mac mac = Mac.getInstance(algo);
            mac.init(key);

            byte[] bytes = mac.doFinal(msg.getBytes("ASCII"));
            StringBuffer hash = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            digest = hash.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return digest;
    }

    private byte[] generateIv() throws NoSuchAlgorithmException {
        char[] chars = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 16; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();
        try {
            return output.getBytes(helper.UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}