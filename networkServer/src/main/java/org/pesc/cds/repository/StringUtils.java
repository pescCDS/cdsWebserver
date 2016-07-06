package org.pesc.cds.repository;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 4/12/16.
 */
public class StringUtils {

    public static boolean isEmpty(String target) {
        return target == null || target.trim().length()==0;
    }
}
