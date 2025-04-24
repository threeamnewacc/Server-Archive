package me.devkevin.practice.arena.standalone;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.devkevin.practice.location.CustomLocation;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class StandaloneArena {

    private CustomLocation a;
    private CustomLocation b;

    private CustomLocation min;
    private CustomLocation max;
}