package it.andrea.test.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InputText {
    private String text;

    public InputText(String _text){
        text = _text;
    }
}
