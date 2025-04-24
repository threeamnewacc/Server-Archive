package club.minemen.practice.arena;

import club.minemen.core.util.CustomLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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
