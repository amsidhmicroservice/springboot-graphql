package com.amsidh.mvc.repository.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface MasterDataRepository<T, Integer> extends JpaRepository<T, Integer>, JpaSpecificationExecutor<T> {
}
