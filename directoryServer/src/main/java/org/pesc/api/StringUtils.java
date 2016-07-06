package org.pesc.api;

import java.util.regex.Pattern;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 4/8/16.
 */
public class StringUtils {

    public static boolean isEmpty(String target) {
        return target == null || target.trim().length()==0;
    }

}
