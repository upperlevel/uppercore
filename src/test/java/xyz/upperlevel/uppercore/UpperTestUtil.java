package xyz.upperlevel.uppercore;

import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;

import static org.mockito.Mockito.mock;

public final class UpperTestUtil {

    public static void setup() {
        PlaceholderUtil.setManager(new FakePlaceholderManager());
        Uppercore core = mock(Uppercore.class);
        Uppercore.overrideInstance(core);
    }

    private UpperTestUtil() {}
}
