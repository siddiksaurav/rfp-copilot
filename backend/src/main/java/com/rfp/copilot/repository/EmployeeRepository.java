package com.rfp.copilot.repository;

import com.rfp.copilot.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query(value = """
        select e from employees e order by e.id limit 3
    """)
    List<Employee> findTop3Employee();
}
