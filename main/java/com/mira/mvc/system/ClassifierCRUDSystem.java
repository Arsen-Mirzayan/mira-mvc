package com.mira.mvc.system;

import com.mira.jpa2.Orders;
import com.mira.jpa2.PageRequest;
import com.mira.jpa2.PageResponse;
import com.mira.jpa2.data.Classifier;
import com.mira.jpa2.data.Classifier_;
import com.mira.jpa2.service.ClassifierService;
import com.mira.mvc.dto.ClassifierDto;

/**
 * ������������ ����� ��� ������ ������ � ����������������
 *
 * @param <ENTITY>  ����� ��������
 * @param <SERVICE> ����� ������� ��� ������ � ���������
 * @param <DTO>     ����� DTO
 */
public abstract class ClassifierCRUDSystem<ENTITY extends Classifier, SERVICE extends ClassifierService<ENTITY>, DTO extends ClassifierDto> extends DefaultCRUDSystem<ENTITY, SERVICE, DTO> {
  /**
   * ������� ������� �������������� �� ����
   *
   * @param code ���
   * @return ������� ��������������
   */
  public DTO findByCode(String code) {
    return convert(getDalService().findByCode(code));
  }

  /**
   * ����� �� ���� � �� �����
   *
   * @param code     ���
   * @param name     ���
   * @param page     ����� ��������
   * @param pageSize ������ ��������
   * @return �������� � �����������
   */
  public PageResponse<DTO> search(String code, String name, long page, long pageSize) {
    PageRequest<ENTITY> request = new PageRequest<>(page, pageSize, new Orders<>(Classifier_.code));
    return convert(getDalService().search(code, name, request));
  }
}
