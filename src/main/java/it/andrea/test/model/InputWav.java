package it.andrea.test.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InputWav {
    private String byteArrStr;

    public InputWav(String _byteArrStr){
        byteArrStr = _byteArrStr;
    }
}
