package com.amsidh.mvc.repository;

import com.amsidh.mvc.entity.Employee;
import com.amsidh.mvc.repository.core.MasterDataRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends MasterDataRepository<Employee, Long> {

}
