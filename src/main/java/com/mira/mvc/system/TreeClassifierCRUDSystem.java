package com.mira.mvc.system;

import com.mira.mvc.dto.TreeClassifierDto;
import com.mira.mvc.utils.EntityComparator;
import com.mira.jpa2.Orders;
import com.mira.jpa2.PageRequest;
import com.mira.jpa2.PageResponse;
import com.mira.jpa2.data.Classifier_;
import com.mira.jpa2.data.TreeClassifier;
import com.mira.jpa2.data.TreeClassifier_;
import com.mira.jpa2.service.TreeClassifierService;

import java.util.Collections;
import java.util.List;

/**
 * Система для работы с классификаторами
 */
public abstract class TreeClassifierCRUDSystem<ENTITY extends TreeClassifier<ENTITY>, SERVICE extends TreeClassifierService<ENTITY>, DTO extends TreeClassifierDto>
    extends ClassifierCRUDSystem<ENTITY, SERVICE, DTO> {

    /**
     * Находит список дочерних элементов
     *
     * @param parent родительский элемент
     * @return список дочерних элементов
     */
    public List<DTO> findChildren(DTO parent) {
        List<ENTITY> children = getDalService().findChildren(toEntity(parent));
        Collections.sort(children, new EntityComparator<>(Classifier_.code));
        return convert(children);
    }

    public List<DTO> findRoot() {
        List<ENTITY> result = getDalService().findRoot();
        Collections.sort(result, new EntityComparator<>(TreeClassifier_.code));
        return convert(result);
    }

    /**
     * Находит список дочерних категорий. Если параметр parent пустой, то возвращает список родительских категорий
     *
     * @param parent   родительская категория
     * @param page     номер страницы
     * @param pageSize размер страницы
     * @return список дочерних категорий
     */
    public PageResponse<DTO> findChildren(DTO parent, long page, long pageSize) {
        PageRequest<ENTITY> pageRequest = new PageRequest<>(page, pageSize, new Orders<>(TreeClassifier_.name));
        PageResponse<ENTITY> result;
        if (parent == null) {
            result = getDalService().findRoot(pageRequest);
        } else {
            result = getDalService().findChildren(toEntity(parent), pageRequest);
        }
        return convert(result);
    }

}
