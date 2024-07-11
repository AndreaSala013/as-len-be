package it.andrea.test.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InputWav {
    private String name;
    private byte[] byteArr;
    private String byteArrStr;

    public InputWav(String _name, byte[] _byteArr){
        name = _name;
        byteArr = _byteArr;
    }

    public InputWav(String _name, String _byteArrStr){
        name = _name;
        byteArrStr = _byteArrStr;
    }
}
