package com.demodeller.service;

import com.demodeller.entity.Domain;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 */
public interface IDomainService extends IService<Domain> {
    void createDomain(Domain domain);
    void deleteDomain(Integer id);
}
