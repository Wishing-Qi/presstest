package luohuayu.ACProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import luohuayu.EndMinecraftPlus.Utils;
import org.spacehq.opennbt.NBTIO;
import org.spacehq.opennbt.tag.builtin.ByteArrayTag;
import org.spacehq.opennbt.tag.builtin.CompoundTag;
import org.spacehq.opennbt.tag.builtin.ListTag;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

public class AnotherStarAntiCheat {
    private void ctsEncode(ByteBuf buf, byte[][] md5s) {
        try {
            CompoundTag nbt = new CompoundTag("");
            ListTag strList = new ListTag("md5s", ByteArrayTag.class);
            for (final byte[] md5 : md5s) {
                strList.add(new ByteArrayTag("", md5));
            }
            nbt.put(strList);
            NBTIO.writeTag(new DataOutputStream(new ByteBufOutputStream(buf)), nbt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] stcDecode(ByteBuf buf) {
        try {
            CompoundTag nbt = (CompoundTag) NBTIO.readTag(new DataInputStream(new ByteBufInputStream(buf)));
            return ((ByteArrayTag) nbt.get("salt")).getValue();    
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] encodeCPacket(String[] md5s, String salt) {
        try {
            HashSet<byte[]> rsaMd5s = new HashSet<byte[]>();
            for (String md5 : md5s) {
                rsaMd5s.add(Utils.md5(md5 + salt).getBytes());
            }

            ByteBuf buf = Unpooled.buffer();
            buf.writeByte(1); // packet id
            ctsEncode(buf, rsaMd5s.toArray(new byte[0][]));
            return buf.array();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String decodeSPacket(byte[] data) {
        try {
            ByteBuf buf = Unpooled.copiedBuffer(data);
            buf.readByte(); // packet id
            return new String(stcDecode(buf), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
