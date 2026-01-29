package com.audit.audit_poc.abs;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class UpdateUtils {

    public static void copyNonNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullOrCollectionPropertyNames(source));
    }

    private static String[] getNullOrCollectionPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            
            // Ignore Nulls
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            } 
            // Ignore Collections (Lists/Sets) to prevent wiping relationships
            else if (srcValue instanceof Collection) {
                emptyNames.add(pd.getName());
            }
        }
        
        // Always ignore "id" to be safe
        emptyNames.add("id");
        
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}