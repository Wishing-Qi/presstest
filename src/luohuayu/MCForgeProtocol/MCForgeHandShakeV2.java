package luohuayu.MCForgeProtocol;

import io.netty.buffer.Unpooled;
import org.spacehq.mc.protocol.packet.login.client.LoginPluginResponsePacket;
import org.spacehq.mc.protocol.packet.login.server.LoginPluginRequestPacket;
import org.spacehq.packetlib.io.buffer.ByteBufferNetInput;
import org.spacehq.packetlib.io.buffer.ByteBufferNetOutput;
import org.spacehq.packetlib.io.stream.StreamNetOutput;
import org.spacehq.packetlib.packet.Packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MCForgeHandShakeV2 extends MCForgeHandShake {
    public MCForgeHandShakeV2(MCForge forge) {
        super(forge);
    }

    public void handle(Packet recvPacket) {
        LoginPluginRequestPacket packet = (LoginPluginRequestPacket) recvPacket;
        if (!packet.getChannel().equals("fml:loginwrapper")) return;

        try {
            ByteBufferNetInput in = new ByteBufferNetInput(ByteBuffer.wrap(packet.getData()));
            final String targetNetworkReceiver = in.readString();
            final int payloadLength = in.readVarInt();
            in = new ByteBufferNetInput(ByteBuffer.wrap(in.readBytes(payloadLength)));

            int packetID = in.readByte();
            switch (packetID) {
                case 1: {
                    // recv: S2CModList
                    final List<String> mods = new ArrayList<>();
                    int len = in.readVarInt();
                    for (int x = 0; x < len; x++)
                        mods.add(in.readString());

                    final Map<String, String> channels = new HashMap<>();
                    len = in.readVarInt();
                    for (int x = 0; x < len; x++)
                        channels.put(in.readString(), in.readString());

                    final List<String> registries = new ArrayList<>();
                    len = in.readVarInt();
                    for (int x = 0; x < len; x++)
                        registries.add(in.readString());
                    // send: C2SModListReply
                    ByteArrayOutputStream buf = new ByteArrayOutputStream();
                    StreamNetOutput out = new StreamNetOutput(buf);

                    out.writeByte(2);

                    out.writeVarInt(mods.size());
                    mods.forEach(m -> {
                        try {
                            out.writeString(m);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    out.writeVarInt(channels.size());
                    channels.forEach((k, v) -> {
                        try {
                            out.writeString(k);
                            out.writeString(v);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    out.writeVarInt(registries.size());
                    registries.forEach(r -> {
                        try {
                            out.writeString(r);
                            out.writeString("");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    sendPluginResponse(packet.getMessageId(), targetNetworkReceiver, buf.toByteArray());
                    break;
                }
                case 3: {
                    // recv: S2CRegistry
                    // send: C2SAcknowledge
                    ByteArrayOutputStream buf = new ByteArrayOutputStream();
                    StreamNetOutput out = new StreamNetOutput(buf);

                    out.writeByte(99);

                    sendPluginResponse(packet.getMessageId(), targetNetworkReceiver, buf.toByteArray());
                    break;
                }
                case 4: {
                    // recv: S2CConfigData
                    // send: C2SAcknowledge
                    ByteArrayOutputStream buf = new ByteArrayOutputStream();
                    StreamNetOutput out = new StreamNetOutput(buf);

                    out.writeByte(99);

                    sendPluginResponse(packet.getMessageId(), targetNetworkReceiver, buf.toByteArray());
                    break;
                }
            }
        } catch (Exception ex) {
            forge.session.disconnect("Failure to handshake", ex);
        }
    }

    public String getFMLVersion() {
        return "FML2";
    }

    public void sendPluginResponse(int id, String targetNetworkReceiver, byte[] payload) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        StreamNetOutput pb = new StreamNetOutput(buf);
        pb.writeString(targetNetworkReceiver);
        pb.writeVarInt(payload.length);
        pb.writeBytes(payload);

        forge.session.send(new LoginPluginResponsePacket(id, buf.toByteArray()));
    }
}
