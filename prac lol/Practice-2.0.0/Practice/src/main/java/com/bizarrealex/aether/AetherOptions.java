package com.bizarrealex.aether;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
public class AetherOptions {

    private boolean hook;
    private boolean scoreDirectionDown;

    static AetherOptions defaultOptions() {
        return new AetherOptions().hook(false).scoreDirectionDown(false);
    }
}
