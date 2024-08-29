package com.amsidh.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@RequiredArgsConstructor
@SpringBootApplication
//@Slf4j
public class SpringbootGraphqlApplication /*implements CommandLineRunner */{

   // private EmployeeRepository employeeRepository;

    public static void main(String[] args) {
        SpringApplication.run(SpringbootGraphqlApplication.class, args);
    }

    /*@Override
    public void run(String... args) throws Exception {

        Employee employee1 = Employee.builder().name("Amsidh").emailId("amsidh@gmail.com").build();
        Employee employee2 = Employee.builder().name("Anjali").emailId("anjali@gmail.com").build();
        this.employeeRepository.save(employee1);
        this.employeeRepository.save(employee2);
        this.employeeRepository.findAll().forEach(employee -> log.info("Employee saved {}", employee));
    }*/
}
