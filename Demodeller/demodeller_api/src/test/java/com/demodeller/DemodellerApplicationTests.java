package com.demodeller;

import com.demodeller.entity.Domain;
import com.demodeller.service.IDomainService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class DemodellerApplicationTests {

    @Autowired
    private IDomainService IDomainService;

    @Test
    public void contextLoads() {
        Domain domain = new Domain();
        domain.setName("test2");
        boolean b = IDomainService.save(domain);
        System.out.println(b);
    }

}
