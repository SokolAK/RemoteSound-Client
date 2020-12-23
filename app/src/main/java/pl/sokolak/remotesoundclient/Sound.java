package pl.sokolak.remotesoundclient;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Sound {
    private int soundId;
    private String soundName;

    @Override
    public String toString() {
        return soundName;
    }
}
