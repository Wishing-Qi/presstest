package luohuayu.ACProtocol;

import luohuayu.EndMinecraftPlus.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class AntiCheat3 {

    public byte[] getCheckData(String acFile, String code, String[] md5List) {
        try {
            byte[] buf1 = code.substring(0, 30).getBytes();

            FileInputStream in = new FileInputStream(new File("lib", acFile));
            byte[] buf2 = new byte[in.available()];
            in.read(buf2);

            byte[] buf3 = new byte[buf1.length + buf2.length];
            System.arraycopy(buf1, 0, buf3, 0, buf1.length);
            System.arraycopy(buf2, 0, buf3, buf1.length, buf2.length);

            try {
                in.close();
            } catch (IOException e2) {
            }

            String result = "";
            if (md5List != null) {
                for (String md5 : md5List) {
                    result += md5 + ",";
                }
            }
            result += Utils.md5(buf3);
            return compress(result);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] compress(String str) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes());
            gzip.close();
        } catch (IOException e) {
        }
        return out.toByteArray();
    }

    public String uncompress(byte[] data) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } catch (IOException e) {
        }

        return new String(out.toByteArray());
    }
}
