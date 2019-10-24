package com.mira.mvc.system.impl;

import com.mira.mvc.dto.TreeClassifierDto;
import com.mira.mvc.system.TreeClassifierCRUDSystem;
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
public abstract class TreeClassifierCRUDSystemImpl<ENTITY extends TreeClassifier<ENTITY>, SERVICE extends TreeClassifierService<ENTITY>, DTO extends TreeClassifierDto>
    extends ClassifierCRUDSystemImpl<ENTITY, SERVICE, DTO>
    implements TreeClassifierCRUDSystem<ENTITY, SERVICE, DTO> {

  @Override
  public List<DTO> findChildren(DTO parent) {
    List<ENTITY> children = getDalService().findChildren(toEntity(parent));
    Collections.sort(children, new EntityComparator<>(Classifier_.code));
    return convert(children);
  }

  @Override
  public List<DTO> findRoot() {
    List<ENTITY> result = getDalService().findRoot();
    Collections.sort(result, new EntityComparator<>(TreeClassifier_.code));
    return convert(result);
  }

  @Override
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
