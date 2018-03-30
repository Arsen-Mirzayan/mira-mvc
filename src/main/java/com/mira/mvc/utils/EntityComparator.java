package com.mira.mvc.utils;



import org.springframework.beans.support.PropertyComparator;

import javax.persistence.metamodel.SingularAttribute;
import java.util.Comparator;

/**
 * Компаратор по свойстам сущностей
 */
public class EntityComparator<T> implements Comparator<T> {

    protected Comparator<T> comparator;

    public EntityComparator(SingularAttribute<? super T, ?> attribute) {
        this(attribute, true);
    }


    public EntityComparator(SingularAttribute<? super T, ?> attribute, boolean ascending) {
        comparator = new PropertyComparator(attribute.getName(), true, ascending);
    }

    @Override
    public int compare(T o1, T o2) {
        return comparator.compare(o1, o2);
    }

    @Override
    public boolean equals(Object obj) {
        return comparator.equals(obj);
    }
}
