package xyz.upperlevel.uppercore.update;

public class PointDividedVersionComparator implements VersionComparator {
    public static final PointDividedVersionComparator INSTANCE = new PointDividedVersionComparator();

    @Override
    public Result compare(String curr, String other) {
        String[] currParts = curr.split("\\.");
        String[] otherParts = other.split("\\.");
        for(int i = 0; i < currParts.length; i++) {
            if(i >= otherParts.length)
                return Result.NEWER;
            int currPart = Integer.parseInt(currParts[i]);
            int otherPart = Integer.parseInt(otherParts[i]);

            if(currPart == otherPart)
                continue;
            if (currPart < otherPart)
                return Result.NEWER;
            else
                return Result.OLDER;
        }
        return currParts.length == otherParts.length ? Result.SAME : Result.NEWER;
    }
}
