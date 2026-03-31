package com.flm.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.flm.client.DepartmentClient;
import com.flm.dto.DepartmentForm;
import com.flm.dto.EmployeeForm;
import com.flm.dto.Response;
import com.flm.entity.Employee;
import com.flm.exception.DepartmentNotFoundException;
import com.flm.exception.EmployeeNotFoundException;
import com.flm.repository.EmployeeRepository;
import com.flm.util.Converter;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {
	
	private final EmployeeRepository employeeRepository;
	//private final RestTemplate restTemplate;
	private final DepartmentClient departmentClient;
	private final Converter converter;
	
	@Transactional
	public Employee saveEmployee(Employee employee){
		return employeeRepository.save(employee);
	}

	@CircuitBreaker(name = "departmentService", fallbackMethod = "fallbackDepartment")
	public Response getEmployee(Long id) throws EmployeeNotFoundException, DepartmentNotFoundException{
		Response response = new Response();
		EmployeeForm eForm = null;
		Optional<Employee> optEmp = employeeRepository.findById(id);
		if (optEmp.isPresent()) { 
			eForm = converter.getEmployeeForm(optEmp.get());
			response.setEmployeeForm(eForm);
		}else
			throw new EmployeeNotFoundException("Employee is not found!!");
		//DepartmentForm departmentForm = restTemplate.getForObject("http://DEPARTMENT-SERVICE/dept/getDepartment/"+eForm.getDeptId(), DepartmentForm.class);
		DepartmentForm departmentForm = departmentClient.getDepartment(eForm.getDeptId());
		response.setDepartmentForm(departmentForm);
		return response; 
	}
	
	public Response fallbackDepartment(Long id, Exception ex) {

	    Optional<Employee> optEmp = employeeRepository.findById(id);

	    EmployeeForm eForm = null;

	    if (optEmp.isPresent()) {
	        eForm = converter.getEmployeeForm(optEmp.get());
	    }

	    // Default response when department service is DOWN
	    DepartmentForm dept = new DepartmentForm();
	    dept.setId(0L);
	    dept.setName("Default Department");
	    dept.setLocation("Not Available");

	    return new Response(eForm, dept);
	}

}
