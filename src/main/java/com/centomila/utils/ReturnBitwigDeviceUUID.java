package com.centomila.utils;
import java.util.UUID;


public class ReturnBitwigDeviceUUID {
    // "Drum Machine" = 8ea97e45-0255-40fd-bc7e-94419741e9d1
    // "Polymer" = 8f58138b-03aa-4e9d-83bd-a038c99a4ed5

    // Create an array with the previously mentioned UUIDs and relative names
    private static final String[][] DEVICE_UUIDS = {
            { "Drum Machine", "8ea97e45-0255-40fd-bc7e-94419741e9d1" },
            { "Polymer", "8f58138b-03aa-4e9d-83bd-a038c99a4ed5" },
            { "Chain", "c86d21fb-d544-4daf-a1bf-57de22aa320c" },
            { "FM-4", "7a0a94df-3aa4-4bb5-8e24-2511999871ad" },
            { "HW CV Instrument", "c511bc17-9ebf-43de-a20c-4a9e40028fdf" },
            { "HW Instrument", "6a27aef7-bba5-4b0d-af98-7c192f84fbc2" },
            { "Instrument Layer", "5024be2e-65d6-4d40-bbfe-8b2ea993c445" },
            { "Instrument Selector", "9588fbcf-721a-438b-8555-97e4231f7d2c" },
            { "Organ", "f2dcfe9a-7b66-4c84-984a-b25685a1c21a" },
            { "Phase-4", "252723bf-68a6-4ee6-81f8-95ba4d0fb467" },
            { "Poly Grid", "a33bba66-8cd4-4f89-aee5-68bf67f70a54" },
            { "Polysynth", "a9ffacb5-33e9-4fc7-8621-b1af31e410ef" },
            { "Sampler", "468bc14b-b2e7-45a1-9666-e83117fe404e" },
            { "V0 Cymbal", "7b21c41e-67c3-4dd0-aa64-4fbb03d95cdb" },
            { "V0 Hat", "212c6aa0-04b6-49b7-a77f-c1fcee5d33a1" },
            { "V0 Kick", "8415c7af-1379-4730-97bf-16380f96d0fe" },
            { "V0 Snare", "446c58e3-ee39-4a22-b1e1-a62c614f98d4" },
            { "V0 Tom", "3c6105ad-176e-4403-993b-3eedefdf6dda" },
            { "V0 Zap Kick", "eef2e851-925b-4e86-81c6-67463e17c5f7" },
            { "V1 Clap", "89eba41d-46d3-4506-8ce6-ba9fe3e3bee4" },
            { "V1 Cowbell", "dd594db1-a908-453f-a1b9-0a1b6c4c3b32" },
            { "V1 Hat", "742e4a89-df78-4ca5-b6b0-ca78889d5953" },
            { "V1 Kick", "c6d5de18-a6f1-4daa-90a9-d9254527601a" },
            { "V1 Snare", "db22eb41-c8a0-4055-b617-637614dfa185" },
            { "V1 Tom", "b5c7c298-e6af-42b3-8f14-26e25bb72d48" },
            { "V8 Clap", "b13d3937-6002-4e88-8e50-e99119708072" },
            { "V8 Claves", "1b709991-7d7d-45d1-aec8-847c01611bfb" },
            { "V8 Cowbell", "f3e8fa57-dd7a-4d94-91dd-1376c1c8304a" },
            { "V8 Cymbal", "0af9f363-ff81-4e72-b2b2-31c8c9682e28" },
            { "V8 Hat", "85d9c654-088f-4a8a-bbfc-e98af9eafb7b" },
            { "V8 Kick", "10fba33b-8e65-4eea-a5cf-312986178240" },
            { "V8 Maracas", "03ec3a24-b3c9-4ba4-b6dc-855178d60de7" },
            { "V8 Rimshot", "9412c72e-acec-4345-acd1-ff5dc20bc2a4" },
            { "V8 Snare", "97938f59-c3d2-4b2c-8640-21c2fd2cc516" },
            { "V8 Tom", "e1be73d9-ba43-4011-91b7-2178bc4af5ea" },
            { "V9 Clap", "3df67ed2-4d70-4a86-a966-14762e2aeea4" },
            { "V9 Crash", "84bd7819-2007-46e0-b930-b4dacff1974a" },
            { "V9 Hat Closed", "5c147bc8-7b62-408b-b057-c4023c4e1adb" },
            { "V9 Hat Open", "94fc934e-a4ba-44f1-aaaa-ae30920fab17" },
            { "V9 Kick", "32a4c607-039a-4998-be9c-578468f25454" },
            { "V9 Ride", "38f52e07-2339-491e-9dd4-8bf6a95c2dae" },
            { "V9 Rimshot", "f88c7dda-c8cd-456f-8bdf-ac25fa5bfea1" },
            { "V9 Snare", "90600c24-04c5-412e-b978-6d3cef1522da" },
            { "V9 Tom", "60f69854-fda1-4538-9ff1-c1553ea25224" },
            { "Vocoder", "a0cb2ec0-2464-461c-8165-296f98905539" },
            { "XY Instrument", "bab3f04d-d3b6-4dfa-86f9-506e0b091ca8" },
            { "Amp", "41be8f3a-6d24-4442-9508-8548dbe62d47" },
            { "Audio Receiver", "46b3e40a-629c-42c2-9e14-a1ccbcaa903b" },
            { "Bit-8", "43875255-6f1f-4d54-a5ad-c45bff793477" },
            { "Blur", "72a3018d-788b-472c-b1d7-16419d00f4c6" },
            { "Chorus+", "1b8f2226-c432-4a0a-9830-69bc76d1a276" },
            { "Chorus", "d275f9a6-0e4a-409c-9dc4-d74af90bc7ae" },
            { "Comb", "20e18780-8438-48d3-b234-40dcbaa947b8" },
            { "Compressor+", "42b32cd2-6275-4ff1-970f-4fac71d15ad9" },
            { "Compressor", "2b1b4787-8d74-4138-877b-9197209eef0f" },
            { "Convolution", "528f7939-87c0-4997-8e71-6331d2eee388" },
            { "DC Offset", "ee445061-a0ee-4322-991a-b60212db04ed" },
            { "De-Esser", "8750db61-e9d3-4d0e-a610-e734006a64dc" },
            { "Delay+", "f2baa2a8-36c5-4a79-b1d9-a4e461c45ee9" },
            { "Delay-1", "2a7a7328-3f7a-4afb-95eb-5230c298bb90" },
            { "Delay-2", "71539d5d-1c7a-4dac-8f74-29e23b89b599" },
            { "Delay-4", "f95a0e18-5a8b-4f53-93ad-8be73fd668bd" },
            { "Distortion", "b5b2b08e-730e-4192-be71-f572ceb5069b" },
            { "Dual Pan", "c94820f8-3779-438b-a85b-868e57b746cc" },
            { "Dynamics", "22e785a2-a187-41e9-a0f2-66343694014c" },
            { "EQ+", "e4815188-ba6f-4d14-bcfc-2dcb8f778ccb" },
            { "EQ-2", "01af068e-1e49-4777-a6e6-7f1dc679227a" },
            { "EQ-5", "227e2e3c-75d5-46f3-960d-8fb5529fe29f" },
            { "EQ-DJ", "3cc1b71a-e22a-42cf-89f0-316475368fb3" },
            { "Filter+", "6d621c1c-ab64-43b4-aea3-dad37e6f649c" },
            { "Filter", "4ccfc70e-59bd-4e97-a8a7-d8cdce88bf42" },
            { "Flanger+", "a99f8c3c-7813-4e6b-a18a-302c74286efc" },
            { "Flanger", "8393c436-b11b-4fee-85dd-b2ef0a2ed380" },
            { "Focus", "42208fc5-02fd-42b4-9681-a8fadb46575f" },
            { "Freq Shifter+", "eb28831d-2478-4918-bd51-bcc1ff4c7eed" },
            { "Freq Shifter", "7ec87fdf-0bf8-42e7-b54b-5d8b68e330b1" },
            { "Freq Split", "3f3c3200-e6aa-4578-8e06-f573ed65206e" },
            { "FX Grid", "d641f61b-d4db-4006-930e-cdd7aeb3e9d7" },
            { "FX Layer", "a0913b7f-096b-4ac9-bddd-33c775314b42" },
            { "FX Selector", "956e396b-07c5-4430-a58d-8dcfc316522a" },
            { "Gate", "556300ac-3a6e-4423-966a-5d5dde459a1b" },
            { "Harmonic Split", "c90b6d52-898b-4dad-aa58-2c58add7c94f" },
            { "HW Clock Out", "a2b59797-2b25-4860-862d-6ab72393b4ca" },
            { "HW CV Out", "d0e71e2d-d491-4cce-a227-fbe118cc4e52" },
            { "HW FX", "29b93a99-eb3a-4b19-8c12-8b4391f5a1ea" },
            { "Ladder", "abfbbd63-8801-4bdb-a1ad-4b197f4d41e0" },
            { "Loud Split", "6e75a854-ceab-475b-94c5-75188ee998b8" },
            { "Mid-Side Split", "a6c9b12f-45a5-43e3-b100-b74ecf77367b" },
            { "Multiband FX-2", "214857d6-b468-4257-9bc9-92f017af1782" },
            { "Multiband FX-3", "f97699d1-3b8e-4363-8ede-4994e276cc97" },
            { "Oscilloscope", "ffe670a2-09aa-4c9b-8822-5161a9cca686" },
            { "Over", "41b34699-8e5d-4534-a429-a67d488ba6ac" },
            { "Peak Limiter", "8da7251e-2578-4bcc-b3c4-8f4ec2e115d0" },
            { "Phaser+", "fd7a9e6c-6992-40c2-be3b-ac8ed48553e9" },
            { "Phaser", "fc87ae07-1624-449f-8dae-2db5d93e1aa9" },
            { "Pitch Shifter", "384fe469-6023-4f69-9560-e0c2eec2da49" },
            { "Replacer", "c8ed6372-d24f-47e0-9e9b-5b2a37949c45" },
            { "Resonator Bank", "b64070ae-5a59-4640-bb6a-194619bc12d8" },
            { "Reverb", "5a1cb339-1c4a-4cc7-9cae-bd7a2058153d" },
            { "Ring-Mod", "374feaeb-c785-4243-9d08-3f9099b4c0cb" },
            { "Rotary", "8fc25e70-b92b-4096-8270-42e492df501a" },
            { "Saturator", "93d11348-86ae-4ead-9fe7-84ac03b9369c" },
            { "Sculpt", "8d9d63db-9991-4e46-8b4c-77755d1fcaab" },
            { "Spectrum", "fcd9aa65-ebbb-4337-a97e-69929322ef47" },
            { "Stereo Split", "96196ffe-658f-46c4-84ba-153799be3657" },
            { "Sweep", "ab52804f-1169-4657-b8c8-8db5532cf717" },
            { "Test Tone", "20b72dbc-0fe1-47c5-867c-f0ab1510f723" },
            { "Tilt", "061dcec6-543f-46f6-b679-f092eeefdbe4" },
            { "Time Shift", "861bb5b0-5cd6-4066-9681-1cc561cb898f" },
            { "Tool", "e67b9c56-838d-4fba-8e3e-ae4e02cccbcb" },
            { "Transient Control", "71e6dbd8-a117-4ff0-85e8-5650f5a76d98" },
            { "Transient Split", "7c3c7bb2-625d-4915-ae95-943ee9aa807d" },
            { "Treemonster", "e45e00d2-85a0-4c05-8321-819694befa09" },
            { "Tremolo", "f3b90fff-402b-4187-9aab-620f441577b9" },
            { "XY FX", "51169152-c144-4a38-95ba-1390fb579a1f" }
    };

    // Create a method that returns the UUID of a device given its name
    public static UUID getDeviceUUID(String deviceName) {
        for (String[] device : DEVICE_UUIDS) {
            if (device[0].equals(deviceName)) {
                return UUID.fromString(device[1]);
            }
        }
        return null;
    }
}
