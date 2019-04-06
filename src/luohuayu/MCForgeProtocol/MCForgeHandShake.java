package luohuayu.MCForgeProtocol;

import org.spacehq.packetlib.packet.Packet;

import java.io.IOException;

public abstract class MCForgeHandShake {
    protected MCForge forge;

    public MCForgeHandShake(MCForge forge) {
        this.forge = forge;
    }

    public abstract void handle(Packet recvPacket);
    public abstract String getFMLVersion();
}
