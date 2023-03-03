package com.demodeller.service.impl;

import com.demodeller.entity.Domain;
import com.demodeller.mapper.DomainMapper;
import com.demodeller.service.IDomainService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DomainService extends ServiceImpl<DomainMapper, Domain> implements IDomainService {
    @Autowired
    private DomainMapper domainMapper;
    @Override
    public void createDomain(Domain domain) {
        domainMapper.createDomain(domain);
    }

    @Override
    public void deleteDomain(Integer id) {
        domainMapper.deleteDomain(id);
    }
}
