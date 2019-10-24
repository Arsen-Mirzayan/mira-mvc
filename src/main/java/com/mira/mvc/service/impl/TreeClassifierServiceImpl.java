package com.mira.mvc.service.impl;

import com.mira.jpa2.dao.TreeClassifierDao;
import com.mira.mvc.dto.TreeClassifierDto;
import com.mira.mvc.service.TreeClassifierService;
import com.mira.mvc.utils.EntityComparator;
import com.mira.jpa2.Orders;
import com.mira.jpa2.PageRequest;
import com.mira.jpa2.PageResponse;
import com.mira.jpa2.data.Classifier_;
import com.mira.jpa2.data.TreeClassifier;
import com.mira.jpa2.data.TreeClassifier_;

import java.util.Collections;
import java.util.List;

/**
 * Система для работы с классификаторами
 */
public abstract class TreeClassifierServiceImpl<ENTITY extends TreeClassifier<ENTITY>, SERVICE extends TreeClassifierDao<ENTITY>, DTO extends TreeClassifierDto>
    extends ClassifierServiceImpl<ENTITY, SERVICE, DTO>
    implements TreeClassifierService<ENTITY, SERVICE, DTO> {

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
