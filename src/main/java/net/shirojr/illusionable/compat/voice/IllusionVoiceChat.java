package net.shirojr.illusionable.compat.voice;

import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.illusionable.cca.component.IllusionComponent;
import org.jetbrains.annotations.Nullable;

public class IllusionVoiceChat {
    @Nullable
    public static VoicechatServerApi voicechatServerApi;

    public static void onMicrophonePacket(MicrophonePacketEvent event) {
        voicechatServerApi = event.getVoicechat();
        if (voicechatServerApi == null) return;
        if (event.getSenderConnection() == null) return;
        if (!(event.getSenderConnection().getPlayer().getPlayer() instanceof ServerPlayerEntity player)) return;
        IllusionComponent illusionComponent = IllusionComponent.fromEntity(player);
        if (!illusionComponent.isIllusion()) return;

        event.cancel();

        for (ServerPlayerEntity targetPlayer : PlayerLookup.tracking(player)) {
            if (player.getUuid().equals(targetPlayer.getUuid())) continue;
            if (!illusionComponent.getTargets().contains(targetPlayer.getUuid())) continue;
            VoicechatConnection illusionConnection = voicechatServerApi.getConnectionOf(player.getUuid());
            VoicechatConnection targetConnection = voicechatServerApi.getConnectionOf(targetPlayer.getUuid());
            if (targetConnection == null || illusionConnection == null) continue;
            voicechatServerApi.sendLocationalSoundPacketTo(targetConnection,
                    event.getPacket().locationalSoundPacketBuilder()
                            .position(illusionConnection.getPlayer().getPosition())
                            .build());
        }
    }
}
