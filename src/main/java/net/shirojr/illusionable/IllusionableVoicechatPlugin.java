package net.shirojr.illusionable;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import net.shirojr.illusionable.compat.voice.IllusionVoiceChat;

public class IllusionableVoicechatPlugin implements VoicechatPlugin {
    public static VoicechatApi voicechatApi;
    
    @Override
    public String getPluginId() {
        return Illusionable.MOD_ID;
    }

    @Override
    public void initialize(VoicechatApi api) {
        VoicechatPlugin.super.initialize(api);
        voicechatApi = api;
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        VoicechatPlugin.super.registerEvents(registration);
        registration.registerEvent(MicrophonePacketEvent.class, IllusionVoiceChat::onMicrophonePacket);
    }
}
