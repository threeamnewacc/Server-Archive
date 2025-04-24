package me.devkevin.util.wrapper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents ban data from an SQL request
 *
 * Created by DevKevin
 * Project: WebSocketAPI
 * Date: 23/02/2022 @ 14:13
 */
@Getter @RequiredArgsConstructor
public class BanWrapper {
    private final String message;
    private final boolean banned;
}
