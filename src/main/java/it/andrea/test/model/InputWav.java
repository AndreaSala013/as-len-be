package it.andrea.test.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InputWav {
    private byte[] byteArr;

    public InputWav(byte[] _byteArr){
        byteArr = _byteArr;
    }
}
