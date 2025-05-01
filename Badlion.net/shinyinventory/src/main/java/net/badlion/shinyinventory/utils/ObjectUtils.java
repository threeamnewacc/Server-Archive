package net.badlion.shinyinventory.utils;

import java.util.List;

/**
 * Created by ShinyDialga45 on 3/28/2015.
 */
public class ObjectUtils {

    public static <T> List<T> paginate(List<T> objects, int page, int pageLength) {
        if (objects.size() <= ((page - 1) * pageLength)) {
            return null;
        }
        int firstItem = ((page * pageLength) - pageLength);
        int lastItem = (firstItem + pageLength);
        int max = objects.size() <= lastItem ? objects.size() : lastItem;
        if (max < firstItem) {
            firstItem = max - 1;
        }
        return objects.subList(firstItem, max);
    }

}
